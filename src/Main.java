package me.icicl.bingo;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin {
    // ran whenever the plugin is enabled.
    public GameState game;
    public TabAgent tabagent;
    public GameDaemon daemon;
    public void onEnable() {
        PluginCommand bingoCommand=this.getCommand("bingo");
        bingoCommand.setExecutor(new me.icicl.bingo.BingoCommandExecutor(this));
        bingoCommand.setTabCompleter(new TabCompletionManager(this));
        saveDefaultConfig();
        //loadConfigFile();
        Bukkit.getPluginManager().registerEvents(new BingoInventoryHelper(this),this);//TODO
        tabagent=new TabAgent(this);
        Bukkit.getPluginManager().registerEvents(tabagent,this);//TODO
        Bukkit.getLogger().info("Tutorial plugin has been enabled!");
        daemon=new GameDaemon(this);
        daemon.runTaskTimer(this,0,20);
//        daemon.runTask(this);
    }

    // ran whenever the plugin is disabled.
    public void onDisable() {
        Bukkit.getLogger().info("Tutorial plugin has been disabled!");
    }

    public List<String> getMaterials(boolean nether){
        Random rand = new Random();
        List<String> blocks = new ArrayList();
        for (String mat:getConfig().getStringList("overworld")){
            if (!(nether&&rand.nextInt(2)==0)){
                blocks.add(mat);
            }
        }
        for(String color:getConfig().getStringList("colors")){
            for(String suffix:getConfig().getStringList("colored")){
                if (rand.nextInt(4)==0) {
                    blocks.add(color+"_"+suffix);
                }
            }
        }
        if(nether){
            blocks.addAll(getConfig().getStringList("nether"));
        }
        return blocks;
    }
    public void log(String msg){
        Bukkit.getLogger().warning(msg);
    }

}
