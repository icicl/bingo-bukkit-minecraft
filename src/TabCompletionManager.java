package me.icicl.bingo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompletionManager implements TabCompleter {
    private final Main plugin;
    private List<String> root_args=new ArrayList();

    public TabCompletionManager(Main plugin){
        this.plugin=plugin;
        root_args.add("new");
        root_args.add("join");
        root_args.add("start");
        root_args.add("card");
        root_args.add("top");
        root_args.add("goals");
        root_args.add("help");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args){
        if (!(sender instanceof Player)){
            return null;
        }
        // TODO check perms
        List<String> list=new ArrayList();
        if (cmd.getName().equalsIgnoreCase("bingo")){
            if (args.length==1){
                for (String s:root_args){
                    if (s.startsWith(args[0])){
                        list.add(s);
                    }
                }
                return list;
            }
            if (args[0].equalsIgnoreCase("new")){
                if (args.length==2){
                    if (args[1].equals("")){
                        list.add("900");
                        list.add("600");
                        list.add("1500");
                        return list;
                    }
                    try {
                        Integer.parseInt(args[1]);
                        list.add(args[1]);
                        return list;
                    } catch (Exception e){
                        list.add("900");
                        list.add("600");
                        list.add("1500");
                        return list;
                    }
                }
                if (args.length==3){
                    list.add("true");
                    list.add("false");
                    return list;
                }
            }
            if (args[0].equalsIgnoreCase("goals")){
                if (args.length==2 || args.length==3){
                    list.add("1");
                    list.add("2");
                    list.add("3");
                    list.add("4");
                    list.add("5");
                    if (!args[1].equals("*")) {
                        list.add("*");
                    }
                    return list;
                }
            }
        }
        return  list;
    }
}
