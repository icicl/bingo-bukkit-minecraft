package me.icicl.bingo;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class BingoInventoryHelper implements Listener {

    private final int FURNACE_SPEED;
    boolean full_inv;
    private Main plugin;
    private int burntime;

    public BingoInventoryHelper(Main plugin) {
        this.plugin = plugin;
        this.FURNACE_SPEED = this.plugin.getConfig().getInt("furnace-speed");
    }

    @EventHandler
    public void onInventoryPickup(EntityPickupItemEvent e) {
        if (plugin.game == null || plugin.game.in_progress == false) {
            return;
        }
        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) e.getEntity();
        BingoPlayer bplayer = this.plugin.game.get_player(player);
        if (
                bplayer.goals_remaining.contains(e.getItem().getItemStack().getType())
        ) {
            find_goal(bplayer, e.getItem().getItemStack().getType());
        }
    }

 /* @EventHandler
  public void onCraft(CraftItemEvent e) {
    if (plugin.game == null || plugin.game.in_progress == false) {
      return;
    }
    Player player = (Player) e.getWhoClicked();
    BingoPlayer bplayer = this.plugin.game.get_player(player);
    if (bplayer.goals_remaining.contains(e.getRecipe().getResult().getType())) {
      find_goal(bplayer, e.getRecipe().getResult().getType());
    }
  } */ //not needed with inventory click event

    @EventHandler
    public void onSmelt(FurnaceExtractEvent e) {
        if (plugin.game == null || plugin.game.in_progress == false) {
            return;
        }
        Player player = e.getPlayer();
        BingoPlayer bplayer = this.plugin.game.get_player(player);
        if (bplayer.goals_remaining.contains(e.getItemType())) {
            find_goal(bplayer, e.getItemType());
        }
    }


    @EventHandler
    public void invEvent2(InventoryClickEvent e) {
        BingoPlayer bp = null;
        if (plugin.game != null) {
            bp = plugin.game.get_player_and_active((Player) e.getWhoClicked());
        }
        if (bp == null) {
            return;
        }
        if (e.getRawSlot() == 45) {
            if (e.getCurrentItem().equals(bp.getMap())) {
                e.setCancelled(true);
                return;
            }
            return;
        }
        if (
                e.getClick() == ClickType.SWAP_OFFHAND &&
                        e.getWhoClicked().getInventory().getItemInOffHand().equals(bp.getMap())
        ) {
            e.setCancelled(true);
            if (e.getInventory().getType() != InventoryType.PLAYER && e.getInventory().getType() != InventoryType.CRAFTING) {
                bp.player.sendMessage("You weren't supposed to do that.");
                bp.player.sendMessage("You still have your card, but the client and server are probably desynced.");
                bp.player.sendMessage("Use §a/bingo card§f to resyncronize your inventory.");//TODO auto update
            }
        }
        full_inv = true;
        int i = 0;
        for (ItemStack istack : e.getWhoClicked().getInventory().getContents()) {
            i++;
            if (i > 36) {
                break;
            }
            //plugin.log(""+istack);
            if (istack == null) {
                full_inv = false;
                break;
            }
        }

        //plugin.log(e.getAction().toString());                //TODO
        if (e.getCurrentItem() != null && e.getAction()!=InventoryAction.NOTHING && (!e.isCancelled()) && (e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY || (!full_inv)) //TODO add better conditions
                && e.getInventory() != null && bp.goals_remaining.contains(e.getCurrentItem().getType())) {
            find_goal(bp, e.getCurrentItem().getType());
        }
        //plugin.log(e.getAction().toString()+" "+e.getInventory().toString()+" "+e.getResult()+" "+e.isCancelled()+" ");
    }

    @EventHandler
    public void invEvent6(PlayerSwapHandItemsEvent e) {
        if (
                plugin.game != null &&
                        plugin.game.get_player_and_active(e.getPlayer()) != null &&
                        plugin.game.get_player(e.getPlayer()).getMap().equals(e.getMainHandItem())
        ) {
            e.setCancelled(true);
        }
    }

    public void find_goal(BingoPlayer bplayer, Material mat) {
        bplayer.find(mat);
        for (BingoPlayer bp : plugin.game.players) {
            bp.update();
        }
        plugin.tabagent.updatePlayer(bplayer.player);
    }

    /*  private final int cook_speed=4;
      @EventHandler
      public void onBurn(FurnaceBurnEvent e){
        Furnace f= (Furnace) e.getBlock().getState();
        Bukkit.getScheduler().runTask(this.plugin, () -> {
          plugin.log("aa"+f.getBurnTime()+" "+f.getCookTimeTotal());
          f.setCookTimeTotal(f.getCookTimeTotal()/cook_speed);
          ((Furnace) e.getBlock().getState()).update();
          //f.update();
        });
      }*/
  /*boolean first_;
  private final int cook_speed=4;//TODO config
  private void startUpdate(final Furnace tile, final int increase) {
    first_=true;
    new BukkitRunnable() {
      public void run() {
        plugin.log(tile.getCookTime()+" "+tile.getBurnTime());
        if (first_ || tile.getCookTime() > 0 || tile.getBurnTime() > 0) {
          first_=false;
          tile.setCookTime((short)(tile.getCookTime() + increase));
          tile.update();
        }
        else {
          this.cancel();
        }
      }
    }.runTaskTimer(this.plugin, 1L, 1L);
  }

  @EventHandler
  public void onFurnaceBurn(final FurnaceBurnEvent event) {
    this.startUpdate((Furnace)event.getBlock().getState(), cook_speed);
  }*/
 /* @EventHandler//fast smelt
  public void onFurnaceBurn(FurnaceBurnEvent event) {
    new BukkitRunnable() {
      public void run() {
        Furnace block = (Furnace) event.getBlock().getState();
        short newcooktime = (short) (block.getCookTimeTotal() - block.getCookTimeTotal() / cook_speed);
        plugin.log("" + newcooktime);
        if (block.getCookTime() == 0) {
          block.setCookTimeTotal(block.getCookTimeTotal() / cook_speed);
        }
        block.setBurnTime((short) (block.getBurnTime() / cook_speed));
        block.update();
      }
    }.runTask(this.plugin);
  }

  @EventHandler//fast smelt
  public void onFurnaceCook(FurnaceSmeltEvent event) {
    Furnace block= (Furnace) event.getBlock().getState();
    block.setCookTimeTotal(block.getCookTimeTotal()/cook_speed);
    block.update();
//    block.setCookTime((short) (block.getCookTimeTotal()-block.getCookTimeTotal()/cook_speed));
  }*/
    @EventHandler//fast smelt
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        startUpdate((Furnace) event.getBlock().getState(), FURNACE_SPEED, event.getBlock());//event.getBurnTime()
    }

    private void startUpdate(Furnace block_, int speed, Block fblock) {
        FurnaceInventory finv = block_.getInventory();
        new BukkitRunnable() {
            public void run() {
                Furnace block = finv.getHolder();
                if (block == null) {
                    cancel();
                }
                block.setCookTime((short) (block.getCookTime() + speed - 1));
                block.setBurnTime((short) (block.getBurnTime() - speed + 1));
                //plugin.log(""+block.getCookTime()+" "+block.getCookTimeTotal()+" "+block.getBurnTime());
                //block.setBurnTime((short) (block.getBurnTime()-speed));
                //plugin.log(finv.getFuel().toString()+" "+finv.getSmelting()+" "+finv.getResult());
                if (block.getCookTime() >= block.getCookTimeTotal()) {
                    block.setCookTime((short) (block.getCookTimeTotal() - 1));
                    //new FurnaceSmeltEvent(fblock, finv.getSmelting(), finv.getResult());
                }
                block.update();
                if (block.getBurnTime() <= 0) {
                    //plugin.log("w"+burntime);
                    cancel();
                }
        /*if (block.getBurnTime() > 0) {
          plugin.log(""+block.getCookTime()+"*"+block.getBurnTime());
          block.update();
        } else {
          plugin.log(""+block.getCookTime()+"*"+block.getBurnTime());
          cancel();
        }*/
            }
        }.runTaskTimer(this.plugin, 1, 1);
    }

}
