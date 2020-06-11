package PlayerUtils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;

public class Player{

    private final AudioPlayerManager playerManager;
    private final HashMap<Long, GuildMusicManager> musicManagers;

    public Player(){
        this.playerManager = new DefaultAudioPlayerManager();

        musicManagers = new HashMap<>();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild){
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if(musicManager == null){
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    public void loadAndPlay(final TextChannel channel, final String trackUrl, final boolean hideNotification){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                if(!hideNotification){
                    channel.sendMessage("Aggiungo alla coda " + audioTrack.getInfo().title).queue();
                }
                play(musicManager, audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                if(firstTrack == null){
                    firstTrack = audioPlaylist.getTracks().get(0);
                }

                play(musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nessun risultato " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                channel.sendMessage("Impossibile riprodurre: " + e.getMessage()).queue();
            }
        });
    }

    private void play(GuildMusicManager musicManager, AudioTrack track){
        musicManager.scheduler.queue(track);
    }

    public void skipTrack(final TextChannel channel){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        if(musicManager.scheduler.nextTrack()) {
            channel.sendMessage("Riproduzione della traccia successiva").queue();
        }else{
            channel.sendMessage("La coda è terminata").queue();
        }
    }

    public void clearAll(final TextChannel channel){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.clearQueue();
        musicManager.scheduler.nextTrack();
    }

    public void removeTrack(int index, final TextChannel channel){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.dequeueTrack(index);
    }

    public HashMap<Integer, AudioTrack> getTracksInQueue(final TextChannel channel){
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        return musicManager.scheduler.getTracksInQueue();
    }

    public AudioTrack getPlayingTrack(final TextChannel channel){
        return getGuildAudioPlayer(channel.getGuild()).scheduler.playingTrack();
    }

}