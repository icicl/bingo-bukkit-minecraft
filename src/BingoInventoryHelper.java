package me.icicl.bingo;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
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
    @EventHandler
    public void invEvent2(InventoryClickEvent e){
        BingoPlayer bp=null;
        plugin.log(e.getInventory().getType().toString());
        if (plugin.game!=null){
            bp=plugin.game.get_player_and_active((Player)e.getWhoClicked());
        }
        if (bp==null){
            return;
        }
        if (e.getRawSlot()==45){
            if (e.getCurrentItem().equals(bp.getMap())){
                e.setCancelled(true);
                return;
            }
            return;
        }
        if (e.getClick()==ClickType.SWAP_OFFHAND && e.getWhoClicked().getInventory().getItemInOffHand().equals(bp.getMap())){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invEvent6(PlayerSwapHandItemsEvent e){
        if (plugin.game!=null && plugin.game.get_player_and_active(e.getPlayer())!=null && plugin.game.get_player(e.getPlayer()).getMap().equals(e.getMainHandItem())){
            e.setCancelled(true);
        }
    }

    public void find_goal(BingoPlayer bplayer, Material mat){
        bplayer.find(mat);
        for (BingoPlayer bp:plugin.game.players){
            bp.update();
        }
        plugin.tabagent.updatePlayer(bplayer.player);
    }
}
