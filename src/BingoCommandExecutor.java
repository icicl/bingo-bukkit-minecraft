package me.icicl.bingo;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.map.MapView;
import java.util.Iterator;
import org.bukkit.map.MapRenderer;
import java.io.IOException;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.ItemMeta;


public class BingoCommandExecutor implements CommandExecutor {
    private final Main plugin;
    private GameHelper helper;

    public BingoCommandExecutor(Main plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
        this.helper = new GameHelper(this.plugin);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("bingo")){
                if (args.length == 0) {

                    return false;
                }
                Player player=null;
                if (sender instanceof Player){
                    player=(Player)sender;
                }
                if (args[0].equalsIgnoreCase("new")){
                    if (args.length>3){
                        sender.sendMessage("Usage: /bingo new [timeInSeconds] [useAdvancedBlocks]");
                        return true;
                    }
                    if (args.length==3) {
                        boolean useAdv;
                        if (args[2].equalsIgnoreCase("true")) {
                            useAdv = true;
                        } else if (args[2].equalsIgnoreCase("false")) {
                            useAdv = false;
                        } else {
                            sender.sendMessage("/bingo new [time] [useAdvancedBlocks], where [useAdvancedBlocks] must be either true or false");
                            return true;
                        }
                        try {
                            if (plugin.game!=null) {
                                plugin.game.end();
                            }
                            plugin.game = new GameState(plugin, Integer.parseInt((args[1])), useAdv);
                            sender.sendMessage("New bingo game started.");
                            return true;
                        } catch (Exception e) {
                            sender.sendMessage("/bingo new [time] [useAdvancedBlocks], where time is an integer, representing game duration is seconds.");
                            return true;
                        }
                    }
                    if (args.length==2){
                        try {
                            if (plugin.game!=null) {
                                plugin.game.end();
                            }
                            plugin.game=new GameState(plugin, Integer.parseInt((args[1])));
                            sender.sendMessage("New bingo game started.");
                            return true;
                        } catch (Exception e) {
                            sender.sendMessage("/bingo new [time], where time is an integer, representing game duration is seconds.");
                            //plugin.game=new GameState(plugin);
                        }
                    }
                    else{
                        if (plugin.game!=null) {
                            plugin.game.end();
                        }
                        plugin.game=new GameState(plugin);
                        sender.sendMessage("New bingo game started.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("join")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("This command can only be run by a player.");
                        return true;
                    }
                    if (plugin.game!=null) {
                        if (plugin.game.get_player(player)!=null){
                            player.sendMessage("You are already a participant in the current bingo game.");
                            return true;
                        }
                        plugin.game.join(player);
                    } else {
                        player.sendMessage("No instance of bingo. Ask an admin to create one with §a/bingo new§f.");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("start")) {//TODO check if players
                    if (plugin.game!=null) {
                        plugin.game.start(this.helper);
                    } else {
                        player.sendMessage("No instance of bingo. Create one with §a/bingo new§f.");
                    }
                    return true;
                }
            if (args[0].equalsIgnoreCase("end")) {
                if (plugin.game != null) {
                    plugin.game.end();
                }
                return true;
            }
                if (args[0].equalsIgnoreCase("card")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("This command can only be run by a player.");
                        return true;
                    }
                    if (plugin.game==null){
                        player.sendMessage("No instance of bingo. Ask an admin to create one with §a/bingo new§f.");
                        return true;
                    }
                    helper.give_map(plugin.game.get_player(player));
                    return true;
                }
                if (args[0].equalsIgnoreCase("goals")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("This command can only be run by a player.");
                        return true;
                    }
                    if (plugin.game==null){
                        player.sendMessage("No instance of bingo. Ask an admin to create one with §a/bingo new§f.");
                        return true;
                    }
                    int row=-1;
                    int col=-1;
                    if (args.length>=3){
                        switch (args[2]){
                            case "1":
                                row=0;
                                break;
                            case "2":
                                row=1;
                                break;
                            case "3":
                                row=2;
                                break;
                            case "4":
                                row=3;
                                break;
                            case "54":
                                row=4;
                                break;
                        }
                    }
                    if (args.length>=2){
                        switch (args[1]){
                            case "1":
                                col=0;
                                break;
                            case "2":
                                col=1;
                                break;
                            case "3":
                                col=2;
                                break;
                            case "4":
                                col=3;
                                break;
                            case "5":
                                col=4;
                                break;
                        }
                    }
                    helper.text_goals(player, row, col);
                    return true;
                }
                if (args[0].equalsIgnoreCase("top")){
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("This command can only be run by a player.");
                        return true;
                    }
                    player.sendMessage("Tell the dev he forgot to implement this");
                }
                /*if (args[0].equals("h")){
                    String s="";
                    for (String ss:args){
                        s+=" "+ss;
                    }
                    player.setPlayerListHeader(s.substring(2));
                    return true;
                }
                if (args[0].equals("b")){
                    String s="";
                    for (String ss:args){
                        s+=" "+ss;
                    }
                    player.setPlayerListName(s.substring(2));
                    return true;
                }
                if (args[0].equals("f")){
                    String s="";
                    for (String ss:args){
                        s+=" "+ss;
                    }
                    player.setPlayerListFooter(s.substring(2));
                    return true;
                }*/
        }
        String pref=(sender instanceof Player)?"§a":"";
        String suff=(sender instanceof Player)?"§f":"";
        sender.sendMessage(pref+"/bingo new [timeInSeconds] [useDifficultItems]"+suff+" creates a new bingo game, and allows players to join it.");
        sender.sendMessage(pref+"/bingo join"+suff+" joins the current bingo game, if it exists and has not yet finished.");
        sender.sendMessage(pref+"/bingo start"+suff+" starts the currently queueing bingo game.");
        sender.sendMessage(pref+"/bingo top"+suff+" warps you to the surface.");
        sender.sendMessage(pref+"/bingo card"+suff+" gives you a new copy of your bingo card, in case you lost yours.");
        sender.sendMessage(pref+"/bingo goals [row] [<col>]"+suff+" tells you the specified goal(s), in text based format.");
        sender.sendMessage(pref+"/bingo help"+suff+" shows this help data.");
        return true;
    }
}