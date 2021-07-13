package me.icicl.bingo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.Dimension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import org.bukkit.Material;
import java.util.Dictionary;
import java.util.Hashtable;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import java.lang.ref.SoftReference;

public class BingoRenderer extends MapRenderer {
    private Player player;
    private Main plugin;
    public boolean unchanged=false;
    private int spacing_size=3;
    private int square_size=22;
    private final byte COLOR_GOOD=6;
    private final byte COLOR_OK=10;
    private final byte COLOR_BAD=(byte)(42*4+2);
    private final byte COLOR_BLACK=119;
    private final int TEXT_SIZE=1;
    private final short[] NUMBERS={0b111111000111111, 0b010011111100001, 0b101111010111101, 0b101011010111111, 0b111000010011111, 0b111011010110111, 0b111111010110111, 0b100011001011100, 0b111111010111111, 0b111011010111111};
    private final Dictionary MATERIALS_DISPLAY=new MaterialDisplay().get();
    public BingoRenderer(Player player, Main plugin){
        this.player=player;
        this.plugin=plugin;
    }
    @Override
    public void render(MapView view, MapCanvas canvas, Player player){
        if(unchanged){
            return;
        }
        BingoPlayer bplayer=plugin.game.get_player(player);
        for (int x=0;x<5;x++){
            for (int y=0;y<5;y++){
                render_square(canvas,x,y, bplayer);
            }
        }
        unchanged=true;
    }
    private void render_square(MapCanvas canvas, int x, int y, BingoPlayer player){
        int score=player.score_for(x,y);
        byte color = player.color_for(x,y);
        for (int i=spacing_size+(square_size+spacing_size)*x; i<(square_size+spacing_size)*(x+1);i++){
            for (int j=spacing_size+(square_size+spacing_size)*y; j<(square_size+spacing_size)*(y+1);j++){
                canvas.setPixel(i,j,color);
            }
        }
        byte[][] pixels=(byte[][])MATERIALS_DISPLAY.get(this.plugin.game.goal(x,y));
        if (pixels==null){
            Bukkit.getLogger().severe("No texture pixel map found for "+this.plugin.game.goal(x,y).getKey());
            return;
        }
        int i0=(square_size+spacing_size)*x+spacing_size+(square_size-16)/2;
        int j0=(square_size+spacing_size)*y+spacing_size+(square_size-16)/2;
        for (int di=0;di<16;di++){
            for (int dj=0;dj<16;dj++){
                if (pixels[dj][di]!=-1) {
                    canvas.setPixel(i0 + di, j0 + dj, pixels[dj][di]);
                }
            }
        }
        int x_=(x+1)*(square_size+spacing_size)+1-3*TEXT_SIZE;
        int y_=(y+1)*(square_size+spacing_size)+1-5*TEXT_SIZE;
        boolean unAltered=true;
        while (score>0 || unAltered){
            unAltered=false;
            for (int i=0;i<3;i++){
                for(int j=0;j<5;j++){
                    if ((NUMBERS[score%10]&(1<<(14-(5*i+j))))>0){
                        for (int ii=0;ii<TEXT_SIZE;ii++){
                            for (int jj=0;jj<TEXT_SIZE;jj++){
                                canvas.setPixel(x_+TEXT_SIZE*i+jj, y_+TEXT_SIZE*j+jj, COLOR_BLACK);
                            }
                        }
                    }
                }
            }
            score/=10;
            x_-=4*TEXT_SIZE;
        }
    }
 }