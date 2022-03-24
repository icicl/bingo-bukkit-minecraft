import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import java.util.ArrayList;
import java.util.List;

public class BingoPlayer {

    private final Main plugin;
    private final byte COLOR_GOOD = 6;
    private final byte COLOR_OK = 10;
    private final byte COLOR_BAD = (byte) (42 * 4 + 2);
    private final byte COLOR_BINGO = (byte) (41 * 4 + 2);
    public Player player; // TODO final??
    public int score = 0;
    public List<Material> goals_remaining = new ArrayList();
    public BingoRenderer renderer;
    private ItemStack map;
    private boolean inBingo = false;
    private int[][] scores = new int[5][5];
    private boolean[][] obtained = new boolean[5][5];
    private boolean[][] bingoed = new boolean[5][5];
    private Location spawn = null;
    public BingoPlayer(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                scores[i][j] = plugin.game.scores[i][j];
            }
        }
        for (int i = 0; i < 5 * 5; i++) {
            goals_remaining.add(this.plugin.game.goal(i / 5, i % 5));
        }
    }

    public void joinBingo() {
        this.inBingo = true;
    }

    public void leaveBingo() {
        this.inBingo = false;
    }

    public byte color_for(int x, int y) {
        if (bingoed[x][y]) {
            return COLOR_BINGO;
        }
        if (obtained[x][y]) {
            return COLOR_GOOD;
        }
        if (scores[x][y] == 0) {
            return COLOR_BAD;
        }
        return COLOR_OK;
    }

    public int score_for(int x, int y) {
        return scores[x][y];
    }

    public void respawn() {
        ItemStack tool;
        ItemMeta meta;
        PlayerInventory pinv=this.player.getInventory();
        for (String item:this.plugin.getConfig().getStringList("initial-items-unbreakable")){
            tool=new ItemStack(Material.getMaterial(item.toUpperCase()));
            meta=tool.getItemMeta();
            meta.setUnbreakable(true);
            tool.setItemMeta(meta);
            if (tool==null){
                this.plugin.log(item+" not recognized as valid material.");
            } else {
                pinv.addItem(tool);
            }
        }
        for (String item : this.plugin.getConfig().getConfigurationSection("initial-items").getKeys(false)) {
            tool=new ItemStack(Material.getMaterial(item.toUpperCase()),this.plugin.getConfig().getInt("initial-items."+item));
            if (tool==null){
                this.plugin.log(item+" not recognized as valid material.");
            } else {
                pinv.addItem(tool);
            }
        }
/*        tool=new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta=tool.getItemMeta();
        meta.setUnbreakable(true);
        tool.setItemMeta(meta);
        pinv.addItem(tool);
        tool=new ItemStack(Material.IRON_AXE);
        tool.getItemMeta().setUnbreakable(true);
        pinv.addItem(tool);
        tool=new ItemStack(Material.IRON_SHOVEL);
        tool.getItemMeta().setUnbreakable(true);
        pinv.addItem(tool);
        this.player.getInventory().addItem(new ItemStack(Material.GOLDEN_CARROT, 64));*/
        //this.player.getInventory().addItem(new ItemStack(Material.FURNACE,64));
        //this.player.getInventory().addItem(new ItemStack(Material.COAL,64));
        //this.player.getInventory().addItem(new ItemStack(Material.RAW_IRON,64));
        if (this.map != null) {
            this.player.getInventory().setItemInOffHand(this.map);
        }
        if (this.spawn == null) {
            return;
        }
        this.player.teleport(this.spawn);
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public void setSpawn(Location loc) {
        this.spawn = loc;
    }

    public ItemStack getMap() {
        return this.map;
    }

    public void setMap(ItemStack item) {
        this.map = item;
    }

    public void setMapLocked(boolean state) {
        ((MapMeta) this.map.getItemMeta()).getMapView().setLocked(state);
    }

    public void find(Material mat) {
        if (goals_remaining.remove(mat)) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (plugin.game.goal(i, j) == mat) {
                        obtained[i][j] = true;
                        plugin.game.find(i, j);
                        score += scores[i][j];
                        for (BingoPlayer bp : plugin.game.players) {
                            bp.player.playSound(
                                    bp.player.getLocation(),
                                    Sound.BLOCK_NOTE_BLOCK_HARP,
                                    1,
                                    1
                            );
                            bp.player.sendMessage(
                                    (bp.equals(this) ? "You" : "§a" + player.getName() + "§f") + " found §6" +
                                            mat
                                                    .getKey()
                                                    .toString()
                                                    .replace("minecraft:", "")
                                                    .replace("_", " ")
                                                    .toUpperCase() +
                                            "§f, worth §a" +
                                            scores[i][j] +
                                            "pts§f."
                            );
                        }
                        boolean[] bingos = {true, true, i == j, i + j == 5 - 1}; // - | / \
                        for (int k = 0; k < 5; k++) {
                            bingos[0] &= obtained[i][k];
                            bingos[1] &= obtained[k][j];
                            bingos[2] &= obtained[k][k];
                            bingos[3] &= obtained[k][(5 - 1) - k];
                        }
                        int[] bonuses = new int[5];
                        int score_bonus = 0;
                        int num_bingos = 0;
                        if (!(bingos[0] || bingos[1] || bingos[2] || bingos[3])) {
                            return;
                        }
                        if (bingos[0]) {
                            num_bingos++;
                            for (int k = 0; k < 5; k++) {
                                bonuses[k] = 5 * i + k;
                                if (!bingoed[i][k]) {
                                    score_bonus += Math.round(scores[i][k] * plugin.game.getDynBonus());
                                    scores[i][k] += Math.round(scores[i][k] * plugin.game.getDynBonus());
                                    bingoed[i][k] = true;
                                }
                            }
                            plugin.game.zero(bonuses);
                        }
                        if (bingos[1]) {
                            num_bingos++;
                            for (int k = 0; k < 5; k++) {
                                bonuses[k] = 5 * k + j;
                                if (!bingoed[k][j]) {
                                    score_bonus += Math.round(scores[k][j] * plugin.game.getDynBonus());
                                    scores[k][j] += Math.round(scores[k][j] * plugin.game.getDynBonus());
                                    bingoed[k][j] = true;
                                }
                            }
                            plugin.game.zero(bonuses);
                        }
                        if (bingos[2]) {
                            num_bingos++;
                            for (int k = 0; k < 5; k++) {
                                bonuses[k] = 5 * k + k;
                                if (!bingoed[k][k]) {
                                    score_bonus += Math.round(scores[k][k] * plugin.game.getDynBonus());
                                    scores[k][k] += Math.round(scores[k][k] * plugin.game.getDynBonus());
                                    bingoed[k][k] = true;
                                }
                            }
                            plugin.game.zero(bonuses);
                        }
                        if (bingos[3]) {
                            num_bingos++;
                            for (int k = 0; k < 5; k++) {
                                bonuses[k] = 5 * k + (5 - 1) - k;
                                if (!bingoed[k][(5 - 1) - k]) {
                                    score_bonus += Math.round(scores[k][(5 - 1) - k] * plugin.game.getDynBonus());
                                    scores[k][(5 - 1) - k] += Math.round(scores[k][(5 - 1) - k] * plugin.game.getDynBonus());
                                    bingoed[k][(5 - 1) - k] = true;
                                }
                            }
                            plugin.game.zero(bonuses);
                        }
                        if (num_bingos > 0) {
                            player.sendMessage(
                                    "You found " +
                                            (num_bingos == 1 ? "a bingo" : num_bingos + " bingos") +
                                            ", gaining an additional §a" +
                                            score_bonus +
                                            "pts§f!"
                            );
                            for (BingoPlayer bp : plugin.game.players) {
                                bp.player.playSound(
                                        bp.player.getLocation(),
                                        Sound.BLOCK_AMETHYST_BLOCK_BREAK,//TODO version ok
                                        1,
                                        0.8f
                                );
                                if (!bp.equals(this)) {
                                    bp.player.sendMessage(
                                            "§a" +
                                                    this.player.getName() +
                                                    "§f found " + (num_bingos == 1 ? "a bingo" : num_bingos + " bingos") +
                                                    ", gaining an additional §a" +
                                                    score_bonus +
                                                    "pts§f!"
                                    );
                                }
                            }
                        }
                        score +=
                                score_bonus + plugin.game.getStaticBonus();
                    }
                }
            }
        }
        return;
    }

    public void update() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (!obtained[i][j]) {
                    scores[i][j] = plugin.game.scores[i][j]; //todo optimize?
                }
            }
        }
        this.renderer.unchanged = false;
    }

    public void setTab(String header, String body, String footer) {
        this.player.setPlayerListHeader(header);
        this.player.setPlayerListName(body);
        this.player.setPlayerListFooter(footer);
    }
}
