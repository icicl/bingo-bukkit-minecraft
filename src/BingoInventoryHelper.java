package me.icicl.bingo;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BingoInventoryHelper implements Listener{
    private Main plugin;
    public BingoInventoryHelper(Main plugin){
        this.plugin=plugin;
    }

    @EventHandler
    public void onInventoryPickup(EntityPickupItemEvent e){
        if (plugin.game==null || plugin.game.in_progress==false){return;}
        if (e.getEntityType()!=EntityType.PLAYER){
            return;
        }
        Player player=(Player)e.getEntity();
        BingoPlayer bplayer=this.plugin.game.get_player(player);
        if (bplayer.goals_remaining.contains(e.getItem().getItemStack().getType())){
            find_goal(bplayer,e.getItem().getItemStack().getType());
        };
//        primary(bplayer);

    }
    @EventHandler
    public void onCraft(CraftItemEvent e){
        if (plugin.game==null || plugin.game.in_progress==false){return;}
        Player player=(Player)e.getWhoClicked();
        BingoPlayer bplayer=this.plugin.game.get_player(player);
        if(bplayer.goals_remaining.contains(e.getRecipe().getResult().getType())){
            find_goal(bplayer,e.getRecipe().getResult().getType());
        }
    }
    @EventHandler
    public void onSmelt(FurnaceExtractEvent e){
        if (plugin.game==null || plugin.game.in_progress==false){return;}
        Player player=e.getPlayer();
        BingoPlayer bplayer=this.plugin.game.get_player(player);
        if(bplayer.goals_remaining.contains(e.getItemType())){
            find_goal(bplayer,e.getItemType());
        }
    }
    /*public List<Material> checkInventory(BingoPlayer bplayer){
        List<Material> found=new ArrayList();
        Inventory inv=bplayer.player.getInventory();
        for (Material mat:bplayer.goals_remaining){
            plugin.log(""+mat.getKey());
            if (inv.contains(mat)){
                found.add(mat);
            }
        }
        return found;
    }
    public void primary(BingoPlayer bplayer){
        List<Material> found=checkInventory(bplayer);
        plugin.log("size "+found.size());
        if (found.size()==0){
            return;
        }
        for (Material mat:found){
            bplayer.find(mat);
        }
        for (BingoPlayer bp:plugin.game.players){
            bp.update();
        }
        plugin.tabagent.updatePlayer(bplayer.player);
    }*/
    public void find_goal(BingoPlayer bplayer, Material mat){
        bplayer.find(mat);
        for (BingoPlayer bp:plugin.game.players){
            bp.update();
        }
        plugin.tabagent.updatePlayer(bplayer.player);
    }
}
