package me.icicl.bingo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class GameState {

  private final int SIZE = 5;
  public boolean in_progress = false;
  private Material[][] goals = new Material[SIZE][SIZE];
  private int playercount = 0;
  public int TIME = 10 * 60;
  private int time;
  public List<BingoPlayer> players = new ArrayList();
  private int max_score;
  private int max_score_base;
  private int max_score_incr;
  private int decrement;
  private int primo_decrement;
  private boolean zero_on_bingo;
  private int bingo_static_bonus;
  private double bingo_dyn_bonus;
  public int[][] scores = new int[SIZE][SIZE];
  private Main plugin;
  private boolean unchanged = false;
  private World world = Bukkit.getWorlds().get(0);
  private TeleportLocationGenerator locgen=null;
  private boolean clearinv=false;

  private void getConfigs() {
    max_score_base = this.plugin.getConfig().getInt("initial-reward-base");
    max_score_incr =
      this.plugin.getConfig().getInt("initial-reward-per-player");
    decrement = this.plugin.getConfig().getInt("subsequent-decrement");
    primo_decrement = this.plugin.getConfig().getInt("initial-decrement");
    zero_on_bingo =
      this.plugin.getConfig().getBoolean("bingo-set-score-to-zero");
    bingo_dyn_bonus =
      this.plugin.getConfig().getDouble("bingo-bonus-multiplier");
    bingo_static_bonus = this.plugin.getConfig().getInt("bingo-bonus-static");
  }

  public double getDynBonus() {
    return this.bingo_dyn_bonus;
  }

  public double getStaticBonus() {
    return this.bingo_static_bonus;
  }

  public GameState(Main plugin) {
    this.plugin = plugin;
    getConfigs();
    this.goals = goals(this.TIME >= 25 * 60);
    this.time = this.TIME;
  }

  public GameState(Main plugin, int t) {
    this.plugin = plugin;
    getConfigs();
    this.time = t;
    this.goals = goals(t >= 25 * 60);
  }

  public GameState(Main plugin, int t, boolean advancedGoals) {
    this.plugin = plugin;
    getConfigs();
    this.time = t;
    this.goals = goals(advancedGoals);
  }

  public void find(int x, int y) {
    if (scores[x][y] == max_score) {
      scores[x][y] -= primo_decrement;
    } else {
      scores[x][y] = Math.max(0, scores[x][y] - decrement);
    }
    for (BingoPlayer bp : players) {
      bp.update();
    }
  }

  public void zero(int[] xy) {
    if (!zero_on_bingo) {
      return;
    }
    int x;
    int y;
    for (int xy_ : xy) {
      x = xy_ / 5;
      y = xy_ % 5;
      scores[x][y] = 0;
    }
    for (BingoPlayer bp : players) {
      bp.update();
    }
  }

  public Material goal(int x, int y) {
    return goals[x][y];
  }

  public Material[][] goals(boolean advancedGoals) {
    List<Material> materials = new ArrayList();
    for (String mat : this.plugin.getMaterials(advancedGoals)) {
      if (Material.getMaterial(mat.toUpperCase()) == null) {
        Bukkit
          .getLogger()
          .warning(mat + " in config.yml not recognized as a valid material.");
      } else {
        materials.add(Material.getMaterial(mat.toUpperCase()));
      }
    }
    if (materials.size() < SIZE * SIZE) {
      Bukkit
        .getLogger()
        .severe(
          "Only " +
          materials.size() +
          " materials detected, but " +
          SIZE *
          SIZE +
          " required. Add materials in [config.yml]."
        );
      return null;
    }
    Material[][] goals = new Material[SIZE][SIZE];
    Random rand = new Random();
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        goals[i][j] = materials.remove(rand.nextInt(materials.size()));
      }
    }
    return goals;
  }

  public Material[][] goals(boolean advancedGoals, int z) {
    List<Material> materials = new ArrayList();
    for (String mat : this.plugin.getMaterials(advancedGoals)) {
      if (Material.getMaterial(mat.toUpperCase()) == null) {
        Bukkit
          .getLogger()
          .warning(mat + " in config.yml not recognized as a valid material.");
      } else {
        materials.add(Material.getMaterial(mat.toUpperCase()));
      }
    }
    if (materials.size() < SIZE * SIZE) {
      Bukkit
        .getLogger()
        .severe(
          "Only " +
          materials.size() +
          " materials detected, but " +
          SIZE *
          SIZE +
          " required. Add materials in [config.yml]."
        );
      return null;
    }
    Material[][] goals = new Material[SIZE][SIZE];
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        goals[i][j] = materials.remove(z);
      }
    }
    return goals;
  }

  public BingoPlayer get_player_and_active(Player player) {
    if (!in_progress) {
      return null;
    }
    return get_player(player);
  }

  public BingoPlayer get_player(Player player) {
    for (BingoPlayer p : players) {
      if (p.player.getUniqueId().equals(player.getUniqueId())) {
        return p;
      }
    }
    return null;
  }

  public String get_player_score(Player player) {
    BingoPlayer bp = get_player(player);
    if (bp == null) {
      return "";
    }
    return "  §3" + bp.score;
  }

  public void join(Player player) {
    for (BingoPlayer bp : players) {
      bp.player.sendMessage(
        "§a" + player.getName() + "§f has joined the game!"
      );
    }
    BingoPlayer bp=new BingoPlayer(plugin, player);
    if (clearinv){
      player.getInventory().clear();
    }
    if (locgen!=null){
      bp.setSpawn(locgen.getNextLoc(player,Bukkit.getWorlds().get(0)));
    }
    this.playercount++;
    this.players.add(bp);
    this.plugin.tabagent.updatePlayer(player);

    if (this.in_progress) {
      bp.respawn();
      player.sendMessage(
        "The game has already begun. This did not prevent you from joining, but you are now at a disadvantage."
      );
      new GameHelper(this.plugin).give_map(this.get_player(player));
    } else {
      player.sendMessage(
        "You have joined the bingo game. You will receive a card with your objectives when the game starts."
      );
    }
  }

  public void start(
    GameHelper helper,
    boolean clearInventories,
    boolean teleportPlayers,
    boolean isolatePlayers
  ) {
    max_score = max_score_base + playercount * max_score_incr;
    for (int[] srow : scores) {
      Arrays.fill(srow, max_score);
    }
    plugin.daemon.start(this.time);
    this.in_progress = true;
    this.clearinv=clearInventories;
    world.setTime(0);
    if (teleportPlayers){
      locgen=new me.icicl.bingo.TeleportLocationGenerator(isolatePlayers);
    }
    for (BingoPlayer player : this.players) {
      if (clearInventories) {
        player.player.getInventory().clear();
      }
      if (teleportPlayers) {
        player.setSpawn(locgen.getNextLoc(player.player,world));
        player.respawn();
      }
      player.player.setHealth(player.player.getMaxHealth());
      player.player.setFoodLevel(20);
      player.player.setSaturation(20);
      helper.give_map(player);
      plugin.tabagent.updatePlayer(player.player);
      player.update();
      player.player.sendMessage("Bingo has begun!");
    }
  }

  public void end() {
    int max_score = 0;
    String tabmsg;
    List<BingoPlayer> victors = new ArrayList();
    for (BingoPlayer player : this.players) {
      if (player.score > max_score) {
        max_score = player.score;
        victors = new ArrayList();
      }
      if (player.score == max_score) {
        victors.add(player);
      }
    }
    String victors_str = "";
    for (BingoPlayer bp : victors) {
      victors_str += " §4§kP§5§kO §a" + bp.player.getName() + " §4§kG§5§kG§f,";
    }
    victors_str =
      "The victor" +
      (victors.size() > 1 ? "s are" : " is") +
      victors_str +
      " with §a" +
      max_score +
      "pts§f.";
    for (BingoPlayer player : this.players) {
      player.player.playSound(
        player.player.getLocation(),
        Sound.ENTITY_PLAYER_LEVELUP,
        1,
        1
      );
      player.player.sendMessage(
        "Bingo game has been ended. You scored §a" + player.score + "pts§a."
      );
      player.player.sendMessage(victors_str);
      if (victors.contains(player)) {
        tabmsg =
          "Bingo has ended; you were victorious with §a" +
          player.score +
          "pts§f!";
      } else {
        tabmsg =
          "Bingo has ended; you were not victorious.\nYou scored §a" +
          player.score +
          "pts§f, and the top score was §a" +
          max_score +
          "pts§f.";
      }
      player.player.setPlayerListFooter(tabmsg);
    }
    plugin.game = null;
  }
}
