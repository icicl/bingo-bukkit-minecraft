package me.icicl.bingo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

  public GameState game;
  public TabAgent tabagent;
  public GameDaemon daemon;

  public void onEnable() {
    PluginCommand bingoCommand = this.getCommand("bingo");
    bingoCommand.setExecutor(new me.icicl.bingo.BingoCommandExecutor(this));
    bingoCommand.setTabCompleter(new TabCompletionManager(this));
    saveDefaultConfig();
    Bukkit
      .getPluginManager()
      .registerEvents(new BingoInventoryHelper(this), this);
    tabagent = new TabAgent(this);
    Bukkit.getPluginManager().registerEvents(tabagent, this);
    Bukkit.getLogger().info("Tutorial plugin has been enabled!");
    daemon = new GameDaemon(this);
    daemon.runTaskTimer(this, 0, 20);
    //getMaterials(true);
  }

  public void onDisable() {
    Bukkit.getLogger().info("Tutorial plugin has been disabled!");
  }

  public List<String> getMaterials(boolean advanced) {
    Random rand = new Random();
    List<String> blocks = new ArrayList();
    List<Integer> weights=new ArrayList();
    int weight=0;
    int temp_weight;
    int tw;
    List<Integer> wl;
    for (String cfg : getConfig().getConfigurationSection("blocks").getKeys(false)) {
      if (getConfig().getConfigurationSection("blocks."+cfg)!=null) {
        temp_weight=0;
        for (String mat:getConfig().getConfigurationSection("blocks."+cfg).getKeys(false)){
          if (!mat.equalsIgnoreCase("weight")) {
            wl=getConfig().getIntegerList("blocks."+cfg+"."+mat);
            temp_weight += wl.get(0) + (advanced ? wl.get(1) : 0);
          }
        }
        temp_weight= rand.nextInt(temp_weight);
        for (String mat:getConfig().getConfigurationSection("blocks."+cfg).getKeys(false)){
          if (!mat.equalsIgnoreCase("weight")) {
            wl=getConfig().getIntegerList("blocks."+cfg+"."+mat);
            temp_weight -= wl.get(0) + (advanced ? wl.get(1) : 0);
            if (temp_weight<0){
              wl=getConfig().getIntegerList("blocks."+cfg+".weight");
              //this.log(mat+" "+wl.size());
              tw=wl.get(0) + (advanced ? wl.get(1)/2 : 0);
              if (tw>0){
                weights.add(tw);
                blocks.add(mat);
              }
              break;
            }
          }
        }
      } else {
        wl=getConfig().getIntegerList("blocks."+cfg);
        tw=wl.get(0) + (advanced ? wl.get(1)/2 : 0);
        if (tw>0) {
          weights.add(tw);
          blocks.add(cfg);
        }
      }
      /*if (mat.)
      if (!(nether && rand.nextInt(2) == 0)) {
        blocks.add(mat);
      }
    }
    for (String color : getConfig().getStringList("colors")) {
      for (String suffix : getConfig().getStringList("colored")) {
        if (rand.nextInt(8) == 0) {
          blocks.add(color + "_" + suffix);
        }
      }
    }
    if (nether) {
      blocks.addAll(getConfig().getStringList("nether"));
    }
    return blocks;*/
    }
    List<String> res=new ArrayList();
    int totw=0;
    int r;
    int i;
    while (blocks.size()>0){
      //this.log(blocks.size()+" "+weights.size());
      totw=0;
      for (int ii:weights){
        totw+=ii;
      }
      r= rand.nextInt(totw);
      i=0;
      while (true) {
        r -= weights.get(i);
        if (r < 0) {
          res.add(blocks.remove(i));
          weights.remove(i);
          break;
        }
        i++;
      }
    }
      return res;
  }

  public void log(String msg) {
    Bukkit.getLogger().warning(msg);
  }

  public void log(int msg) {
    Bukkit.getLogger().warning("" + msg);
  }
}
