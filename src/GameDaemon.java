import org.bukkit.scheduler.BukkitRunnable;

public class GameDaemon extends BukkitRunnable { //i dont know what daemon means, i just called it this because i like how it sounds

    private final Main plugin;
    private int timer;
    private int score_counter;
    private int SCORE_COUNTER;
    private int SCORE_DEC;

    public GameDaemon(Main plugin) {
        this.plugin = plugin;
        this.timer = -1;
        this.SCORE_COUNTER = this.plugin.getConfig().getInt("reward-decrease-interval");
        this.SCORE_DEC = this.plugin.getConfig().getInt("reward-decrease-amount");
    }

    @Override
    public void run() {
        if (timer < 0) {
            return;
        }
        if (timer == 0) {
            this.plugin.game.end();
            timer--;
            return;
        }
        if (plugin.game == null) {
            timer = -1;
            return;
        }

        if (timer == 60) {
            plugin.game.warnTimeLeft(timer);
        }

        if (timer == 60 * 5) {
            plugin.game.warnTimeLeft(timer);
        }

        plugin.tabagent.setBingoFooter(formatFooter(timer));
        timer--;
        score_counter--;
        if (score_counter == 0) {
            score_counter = SCORE_COUNTER;
            this.plugin.game.decrease_all(SCORE_DEC);
        }
    }

    public String formatFooter(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds %= 60;
        String time_str = hours != 0 ? "" + hours + ":" : ""; //TODO maybe add case for or prevent time>99:59:59
        time_str += minutes != 0 ? minutes : "0";
        time_str += (seconds < 10 ? ":0" : ":") + seconds;
        return (
                "\nCurrent bingo game has §5" + time_str + " §fremaining to find goals."
        );
    }

    public void start(int time) {
        this.timer = time;
        this.score_counter = SCORE_COUNTER + 1;
    }
}
