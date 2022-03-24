import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;

public class TeleportLocationGenerator {
    Random rand = new Random();
    int root_offset_dist;
    double root_offset_angle;
    int sub_offset_dist;
    double sub_offset_angle;
    int rx;
    int rz;
    int tx = 0;
    int tz = 0;
    boolean isolatePlayers;
    boolean ban_fluids=true;
    public TeleportLocationGenerator(boolean isolatePlayers) {
        this.isolatePlayers=isolatePlayers;
        root_offset_dist = rand.nextInt(1 << 20) << 4;
        root_offset_angle = rand.nextFloat() * (2 * Math.PI);
        rx = (int) (root_offset_dist * Math.sin(root_offset_angle));
        rz = (int) (root_offset_dist * Math.cos(root_offset_angle));
    }
    public Location getNextLoc(Player player, World world){
        Location loc = null;
        if (isolatePlayers) {
            root_offset_angle += 1;
            rx = (int) (root_offset_dist * Math.sin(root_offset_angle));
            rz = (int) (root_offset_dist * Math.cos(root_offset_angle));
        }
        if (world.getHighestBlockAt(rx,rz).getType()==Material.WATER){
            root_offset_angle+=1;
            rx = (int) (root_offset_dist * Math.sin(root_offset_angle));
            rz = (int) (root_offset_dist * Math.cos(root_offset_angle));
            return getNextLoc(player,world);
        }
        for (int i=0;i<16;i++){
            sub_offset_angle = rand.nextFloat() * (2 * Math.PI);
            sub_offset_dist = Math.max(rand.nextInt(3 << 5), rand.nextInt(3 << 5));
            tx = (int) (sub_offset_dist * Math.sin(sub_offset_angle));
            tz = (int) (sub_offset_dist * Math.cos(sub_offset_angle));
            loc=new Location(
                    world,
                    rx + tx,
                    (double) world.getHighestBlockYAt((int) (tx + rx), (int) (tz + rz)),
                    rz + tz,
                    player.getLocation().getYaw(),
                    player.getLocation().getPitch()
            );
                    if (!(ban_fluids&& world.getBlockAt(loc).isLiquid())){
                        return loc.add(0.5,1,0.5);
                    }
        }
        world.getBlockAt(loc).setType(Material.GLASS);
        return loc.add(0.5,1,0.5);//TODO water
        //return getNextLoc(player,world);
    }

}
