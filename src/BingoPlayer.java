package me.icicl.bingo;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class BingoPlayer {
    public BingoPlayer(Main plugin, Player player){
        this.plugin=plugin;
        this.player=player;
        for (int i=0;i<5;i++){
            for (int j=0;j<5;j++){
                scores[i][j]=plugin.game.scores[i][j];
            }
        }
/*        for (int[] q:scores){
            Arrays.fill(q,plugin.game.scores[i]);
            i++;
        }*/
        for (int i=0;i<5*5;i++){
             goals_remaining.add(this.plugin.game.goal(i/5,i%5));
        }
    }
    private final Main plugin;
    public Player player;// TODO final??
    private final byte COLOR_GOOD=6;
    private final byte COLOR_OK=10;
    private final byte COLOR_BAD=(byte)(42*4+2);
    public int score=0;
    private boolean inBingo=false;
    public void joinBingo(){
        this.inBingo=true;
    }
    public void leaveBingo(){
        this.inBingo=false;
    }
    private int[][] scores=new int[5][5];
    private boolean[][] obtained=new boolean[5][5];
    private boolean[][] bingoed=new boolean[5][5];
    public List<Material> goals_remaining=new ArrayList();
    public BingoRenderer renderer;
    public byte color_for(int x,int y){
        if (obtained[x][y]){
            return COLOR_GOOD;
        }
        if (scores[x][y]==0){
            return COLOR_BAD;
        }
        return COLOR_OK;
    }
    public int score_for(int x,int y){
        return scores[x][y];
    }
    public void find(Material mat){
        if (goals_remaining.remove(mat)){
            for (int i=0;i<5;i++){
                for (int j=0;j<5;j++){
                    if (plugin.game.goal(i,j)==mat){
                        plugin.log(""+player.getUniqueId());
                        obtained[i][j]=true;
                        plugin.game.find(i,j);
                        score+=scores[i][j];
                        player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                        //plugin.log("You found §6"+mat.getKey().toString().replace("minecraft:","").replace("_"," ").toUpperCase()+"§f, worth §a"+scores[i][j]+"pts§f.");
                        player.sendMessage("You found §6"+mat.getKey().toString().replace("minecraft:","").replace("_"," ").toUpperCase()+"§f, worth §a"+scores[i][j]+"pts§f.");
                        boolean[] bingos={true,true,i==j,i+j==5-1};// - | / \
                        for (int k=0;k<5;k++){
                            bingos[0]&=obtained[i][k];
                            bingos[1]&=obtained[k][j];
                            bingos[2]&=obtained[k][k];
                            bingos[3]&=obtained[k][(5-1)-k];
                        }
                        int[] bonuses=new int[5];
                        int score_bonus=0;
                        int num_bingos=0;
                        if (!(bingos[0]||bingos[1]||bingos[2]||bingos[3])){
                            return;
                        }
                        if (bingos[0]){
                            num_bingos++;
                            for (int k=0;k<5;k++){
                                bonuses[k]=5*i+k;
                                if (!bingoed[i][k]){
                                    score_bonus+=scores[i][k];
                                    bingoed[i][k]=true;
                                }
                            }
                            plugin.game.zero(bonuses);
                        }
                        if (bingos[1]){
                            num_bingos++;
                            for (int k=0;k<5;k++){
                                bonuses[k]=5*k+j;
                                if (!bingoed[k][j]){
                                    score_bonus+=scores[k][j];
                                    bingoed[k][j]=true;
                                }
                            }
                            plugin.game.zero(bonuses);
                        }
                        if (bingos[2]){
                            num_bingos++;
                            for (int k=0;k<5;k++){
                                bonuses[k]=5*k+k;
                                if (!bingoed[k][k]){
                                    score_bonus+=scores[k][k];
                                    bingoed[k][k]=true;
                                }
                            }
                            plugin.game.zero(bonuses);
                        }
                        if (bingos[3]){
                            num_bingos++;
                            for (int k=0;k<5;k++){
                                bonuses[k]=5*k+(5-1)-k;
                                if (!bingoed[k][(5-1)-k]){
                                    score_bonus+=scores[k][(5-1)-k];
                                    bingoed[k][(5-1)-k]=true;
                                }
                            }
                            plugin.game.zero(bonuses);
                        }
                        if (num_bingos>0){
                            player.sendMessage("You found "+(num_bingos==1?"a bingo":num_bingos+" bingos")+", gaining an additional §a"+score_bonus+"pts§f!");
                            for (BingoPlayer bp:plugin.game.players){
                                if (!bp.equals(this)){
                                    bp.player.playSound(bp.player.getLocation(),Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 0.5f);
                                    bp.player.sendMessage("§a"+this.player.getName()+"§f found a bingo. Some items are now worth no points.");
                                }
                            }
                        }
                        score+=Math.round(plugin.game.getDynBonus()*score_bonus)+plugin.game.getStaticBonus();
                    }
                }
            }
        }
        return;
    }
    public void update(){
        for (int i=0;i<5;i++){
            for (int j=0;j<5;j++){
                if (!obtained[i][j]){
                    scores[i][j]=plugin.game.scores[i][j];//todo optimise?
                }
            }
        }
        this.renderer.unchanged=false;
    }
    public void setTab(String header, String body, String footer){
        this.player.setPlayerListHeader(header);
        this.player.setPlayerListName(body);
        this.player.setPlayerListFooter(footer);
    }
}
