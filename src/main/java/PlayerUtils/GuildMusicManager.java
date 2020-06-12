package PlayerUtils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.managers.AudioManager;

public class GuildMusicManager {

    public final AudioPlayer player;

    public final TrackScheduler scheduler;

    public GuildMusicManager(AudioPlayerManager manager, AudioManager aManager){
        player = manager.createPlayer();
        this.scheduler = new TrackScheduler(player, aManager);
        player.addListener(scheduler);
    }

    public AudioPlayerSendHandler getSendHandler(){
        return new AudioPlayerSendHandler(player);
    }
}
