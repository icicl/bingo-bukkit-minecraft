import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class TabAgent implements Listener {

    private Main plugin;

    public TabAgent(Main plugin) {
        this.plugin = plugin;
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (
                (e.getEntityType() == EntityType.PLAYER) &&
                        (e.getCause() != EntityDamageEvent.DamageCause.VOID)
        ) {
            Player player = (Player) e.getEntity();
            updatePlayer(player, (int) (player.getHealth() - e.getFinalDamage()));
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) e.getEntity();
            updatePlayer(player, (int) (player.getHealth() + e.getAmount()));
        }
    }

    @EventHandler
    public void onJoinServer(PlayerJoinEvent e) {
        if (plugin.game != null && plugin.game.get_player(e.getPlayer()) != null) {
            plugin.game.get_player(e.getPlayer()).player = e.getPlayer(); //scuffed but it works, so idc
        }
        updatePlayer(e.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (plugin.game != null && plugin.game.get_player(e.getPlayer()) != null) {
            e.setRespawnLocation(plugin.game.get_player(e.getPlayer()).getSpawn());
            plugin.game.get_player(e.getPlayer()).respawn();
        }
        updatePlayer(e.getPlayer(), 20);
    }

    @EventHandler
    public void onWorldTransition(PlayerChangedWorldEvent e) {
        updatePlayer(e.getPlayer());
    }

    public void updatePlayer(Player player) {
        updatePlayer(player, (int) player.getHealth());
    }

    public void updatePlayer(Player player, int health) { //TODO update on respawn;
        String health_str = "§4";
        for (int i = 0; i < health / 2; i++) {
            health_str += "❤";
        }
        if (health % 2 == 1) {
            health_str += "§c❤";
        }
        health_str += "§0";
        for (int i = health; i < 20 - 1; i += 2) {
            health_str += "❤";
        }
        if (plugin.game == null) {
            player.setPlayerListName("§a" + player.getName() + " " + health_str);
            return;
        }
        player.setPlayerListName(
                "§a" +
                        player.getName() +
                        " " +
                        health_str + " " + ((player.getWorld().getEnvironment() == World.Environment.NORMAL) ? "§f" : ((player.getWorld().getEnvironment() == World.Environment.NETHER) ? "§4" : "§a")) + "☀" +
                        plugin.game.get_player_score(player)
        );
    }

    public void setBingoFooter(String footer) {
        for (BingoPlayer bp : plugin.game.players) {
            bp.player.setPlayerListFooter(footer);
        }
    }
}
