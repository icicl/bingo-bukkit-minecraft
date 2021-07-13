package me.icicl.bingo;

import org.bukkit.Material;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GameState {
        private final int SIZE=5;
        public boolean in_progress=false;
        private Material[][] goals = new Material[SIZE][SIZE];
        private int playercount = 0;
        public int TIME=10*60;
        private int time;
        public List<BingoPlayer> players = new ArrayList();
        private int max_score;
        private int decrement;
        private int primo_decrement;
        private boolean zero_on_bingo;
        private int bingo_static_bonus;
        private double bingo_dyn_bonus;
        public int[][] scores = new int[SIZE][SIZE];
        private Main plugin;
        private boolean unchanged=false;
        //private List<Player> need_update=new ArrayList();
    private void getConfigs(){
        max_score=this.plugin.getConfig().getInt("initial-reward");
        decrement=this.plugin.getConfig().getInt("subsequent-decrement");
        primo_decrement=this.plugin.getConfig().getInt("initial-decrement");
        zero_on_bingo=this.plugin.getConfig().getBoolean("bingo-set-score-to-zero");
        bingo_dyn_bonus=this.plugin.getConfig().getDouble("bingo-bonus-multiplier");
        bingo_static_bonus=this.plugin.getConfig().getInt("bingo-bonus-static");
    }
    public double getDynBonus(){
        return this.bingo_dyn_bonus;
    }
    public double getStaticBonus(){
        return this.bingo_static_bonus;
    }
    public GameState(Main plugin){
        this.plugin=plugin;
        getConfigs();
        this.goals=goals(this.TIME>=15*60);
        this.time=this.TIME;
        for (int[] srow:scores){
            Arrays.fill(srow,max_score);
        }
            /*Material[] m={Material.STONE, Material.DIRT, Material.DIAMOND};
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    scores[x][y] = max_score;
                    goals[x][y] = m[new Random().nextInt(3)];
                }
            }*/
    }
    public GameState(Main plugin, int t){
        this.plugin=plugin;
        getConfigs();
        this.time=t;
        this.goals=goals(t>=15*60);
        for (int[] srow:scores){
            Arrays.fill(srow,max_score);
        }
    }
    public GameState(Main plugin, int t, boolean advancedGoals){
        this.plugin=plugin;
        getConfigs();
        this.time=t;
        this.goals=goals(advancedGoals);
        for (int[] srow:scores){
            Arrays.fill(srow,max_score);
        }
    }
        /*public boolean unchanged(){
            return unchanged;
        }
        public void set_changed(){
            unchanged=false;
        }
        public void inform_updated(Player player){
            need_update.remove(player);
            if need_update.length.
        }*/
        public void find(int x, int y){
            if (scores[x][y]==max_score){
                scores[x][y]-=primo_decrement;
            }
            scores[x][y]=Math.max(0,scores[x][y]-decrement);
            for (BingoPlayer bp:players){
                bp.update();
            }
        }
        public void zero(int[] xy){
            if (!zero_on_bingo){
                return;
            }
            int x;
            int y;
            for (int xy_:xy){
                x=xy_/5;
                y=xy_%5;
                scores[x][y]=0;
            }
            for (BingoPlayer bp:players){
                bp.update();
            }
        }
    public Material goal(int x, int y){
        return goals[x][y];
    }
    public Material[][] goals(boolean advancedGoals){
        List<Material> materials= new ArrayList();
        for (String mat:this.plugin.getMaterials(advancedGoals)){
            if (Material.getMaterial(mat.toUpperCase())==null){
                Bukkit.getLogger().warning(mat+" in config.yml not recognized as a valid material.");
            } else {
                materials.add(Material.getMaterial(mat.toUpperCase()));
            }
        }
        if (materials.size()<SIZE*SIZE){
            Bukkit.getLogger().severe("Only "+materials.size()+" materials detected, but "+SIZE*SIZE+" required. Add materials in [config.yml].");
            return null;
        }
        Material[][] goals=new Material[SIZE][SIZE];
        Random rand = new Random();
        for (int i=0;i<SIZE;i++){
            for (int j=0;j<SIZE;j++){
                goals[i][j]=materials.remove(rand.nextInt(materials.size()));
            }
        }
        return goals;
    }
    public Material[][] goals(boolean advancedGoals, int z){
        List<Material> materials= new ArrayList();
        for (String mat:this.plugin.getMaterials(advancedGoals)){
            if (Material.getMaterial(mat.toUpperCase())==null){
                Bukkit.getLogger().warning(mat+" in config.yml not recognized as a valid material.");
            } else {
                materials.add(Material.getMaterial(mat.toUpperCase()));
            }
        }
        if (materials.size()<SIZE*SIZE){
            Bukkit.getLogger().severe("Only "+materials.size()+" materials detected, but "+SIZE*SIZE+" required. Add materials in [config.yml].");
            return null;
        }
        Material[][] goals=new Material[SIZE][SIZE];
        for (int i=0;i<SIZE;i++){
            for (int j=0;j<SIZE;j++){
                goals[i][j]=materials.remove(z);
            }
        }
        return goals;
    }
    public BingoPlayer get_player(Player player){
            for (BingoPlayer p:players){
                if (p.player.getUniqueId().equals(player.getUniqueId())){
                    return p;
                }
            }
            return null;
    }
    public String get_player_score(Player player){
            BingoPlayer bp=get_player(player);
            if (bp==null){
                return "";
            }
            return "  §3"+bp.score;
    }
    public void join(Player player){//TODO no duplicates
            for (BingoPlayer bp:players){
                bp.player.sendMessage("§a"+player.getName()+"§f has joined the game!");
            }
            this.playercount++;
            this.players.add(new BingoPlayer(plugin,player));
            this.plugin.tabagent.updatePlayer(player);
            //this.need_update.add(player);

        if (this.in_progress){
            player.sendMessage("The game has already begun. This did not prevent you from joining, but you are now at a disadvantage.");
            new GameHelper(this.plugin).give_map(this.get_player(player));
        } else {
            player.sendMessage("You have joined the bingo game. You will receive a card with your objectives when the game starts.");
        }
    }
    public void start(GameHelper helper){
            plugin.daemon.start(this.time);
            this.in_progress=true;
            for (BingoPlayer player:this.players){
                helper.give_map(player);
                plugin.tabagent.updatePlayer(player.player);
                player.player.sendMessage("Bingo has begun!");
            }
    }
    public void end(){
            int max_score=0;
            String tabmsg;
            List<BingoPlayer> victors=new ArrayList();
            for (BingoPlayer player:this.players){
                if (player.score>max_score){
                    max_score=player.score;
                    victors=new ArrayList();
                }
                if (player.score==max_score){
                    victors.add(player);
                }
            }
            String victors_str="";
            for (BingoPlayer bp:victors){
                victors_str+=" §4§kP§5§kO §a"+bp.player.getName()+" §4§kG§5§kG§f,";
            }
            victors_str = "The victor"+(victors.size()>1?"s are":" is")+victors_str+" with §a"+max_score+"pts§f.";
            for (BingoPlayer player:this.players){
                player.player.playSound(player.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                player.player.sendMessage("Bingo game has been ended. You scored §a"+player.score+"pts§a.");
                player.player.sendMessage(victors_str);
                if (victors.contains(player)){
                    tabmsg="Bingo has ended; you were victorious with §a"+player.score+"pts§f!";
                } else {
                    tabmsg="Bingo has ended; you were not victorious.\nYou scored §a"+player.score+"pts§f, and the top score was §a"+max_score+"pts§f.";
                }
                player.player.setPlayerListFooter(tabmsg);
                //player.player.sendMessage("0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#0123456789abcde#");
            }
            //plugin.daemon.cancel();
            plugin.game=null;
    }
}
