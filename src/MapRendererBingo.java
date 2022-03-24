import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapRendererBingo implements Listener {

  private final Main plugin;

  public MapRendererBingo(Main plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onMap(MapInitializeEvent event) {
    MapView map = event.getMap();
    for (MapRenderer renderer : map.getRenderers()) {
      map.removeRenderer(renderer);
    }
  }
}
