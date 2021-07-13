package me.icicl.bingo;

import org.bukkit.scheduler.BukkitRunnable;

public class GameDaemon extends BukkitRunnable {//i dont know what daemon means, i just called it this because i like how it sounds
    private final Main plugin;
    private int timer;

    public GameDaemon(Main plugin){
        this.plugin=plugin;
        this.timer=-1;
    }

    @Override
    public void run(){
        //plugin.log(""+timer);
        if (timer<0){
            return;
        }
        if (timer==0){
            this.plugin.game.end();
            //plugin.tabagent.setBingoFooter("\nBingo game has ended.");
            timer--;
            return;
        }
        if (plugin.game==null){
            timer=-1;
            return;
        }
        plugin.tabagent.setBingoFooter(formatFooter(timer));
        timer--;
    }
    public String formatFooter(int seconds){
        int hours=seconds/3600;
        int minutes=(seconds%3600)/60;
        seconds%=60;
        String time_str=hours!=0?""+hours+":":"";//TODO maybe add case for or prevent time>99:59:59
        time_str+=minutes!=0?minutes:"0";
        time_str+=(seconds<10?":0":":")+seconds;
        //plugin.log("1"+time_str+"1");
        return "\nCurrent bingo game has §5"+time_str+" §fremaining to find goals.";
    }
    public void start(int time){
        this.timer=time;
    }
}
