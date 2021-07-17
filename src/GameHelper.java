package me.icicl.bingo;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class GameHelper {

    Main plugin;

    public GameHelper(Main plugin) {
        this.plugin = plugin;
    }

    public void give_map(BingoPlayer bplayer) {
        if (!plugin.game.in_progress) {
            bplayer.player.sendMessage(
                    "The bingo game is still queueing. Consider asking an admin to start the game with §a/bingo start§f."
            );
            return;
        }
        if (
                bplayer.getMap() != null &&
                        bplayer.getMap().equals(bplayer.player.getInventory().getItemInOffHand())
        ) {
            bplayer.player.sendMessage("You already have your card. Forcing inventory update.");
            bplayer.player.updateInventory();
            return;
        }
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapView view = Bukkit.createMap(Bukkit.getWorlds().get(0));
        view.getRenderers().forEach(view::removeRenderer);
        bplayer.renderer = new BingoRenderer(bplayer.player, this.plugin);
        view.addRenderer(bplayer.renderer);
        view.setLocked(false);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapView(view);
        item.setItemMeta(meta);
        PlayerInventory inventory = bplayer.player.getInventory();
        inventory.setItemInOffHand(item);
        bplayer.setMap(item);
        bplayer.player.sendMessage("Here is your bingo card!");
    }

    public String nice_text(Material mat) {
        return mat
                .getKey()
                .getKey()
                .replace("minecraft:", "")
                .replace("_", " ")
                .toUpperCase();
    }

    public void text_goals(Player player, int row, int col) {
        if (!plugin.game.in_progress) {
            player.sendMessage(
                    "The bingo game is still queueing. Consider asking an admin to start the game with §a/bingo start§f."
            );
            return;
        }
        String s;
        String m;
        if (row == -1) {
            if (col == -1) {
                player.sendMessage(
                        "Use §a/bingo goals [row] [<col>]§f to get the names of your objectives. You are allowed up to one wildcard, and [col] defaults to wildcard."
                );
                return;
            }
            for (int i = 0; i < 5; i++) {
                player.sendMessage(nice_text(plugin.game.goal(i, col)));
            }
            return;
        }
        if (col == -1) {
            for (int i = 0; i < 5; i++) {
                player.sendMessage(nice_text(plugin.game.goal(row, i)));
            }
            return;
        }
        player.sendMessage(nice_text(plugin.game.goal(row, col)));
        return;
    }
}
