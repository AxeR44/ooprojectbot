package PlayerUtils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.concurrent.*;

public class TrackScheduler extends AudioEventAdapter{

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final HashMap<Integer, AudioTrack> tracksInQueue;
    private final AudioManager guildAudioManager;
    private final ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> future;

    public TrackScheduler(AudioPlayer player, AudioManager manager){
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.tracksInQueue = new HashMap<>();
        this.guildAudioManager = manager;
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    public void queue(AudioTrack track){
        if(future != null && !future.isCancelled()){
            future.cancel(false);
        }
        if(!player.startTrack(track, true)){
            this.queue.offer((track));
        }
        updateHashMap();
    }

    public boolean nextTrack(){
        boolean flag = true;
        if(queue.isEmpty()) {
            flag = false;
            future = scheduledExecutorService.schedule(this::leave, 2, TimeUnit.MINUTES);
        }
        player.startTrack(queue.poll(), false);
        updateHashMap();
        return flag;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
        if(endReason.mayStartNext){
            nextTrack();
        }
        updateHashMap();
    }

    private synchronized void updateHashMap(){
        int i = 0;
        this.tracksInQueue.clear();
        for(AudioTrack t : queue){
            tracksInQueue.put(i++, t);
        }
    }

    public void clearQueue(){
        this.queue.clear();
        updateHashMap();
    }

    public synchronized HashMap<Integer, AudioTrack> getTracksInQueue(){
        return this.tracksInQueue;
    }

    public synchronized void dequeueTrack(final int index) throws NullPointerException{
        queue.remove(tracksInQueue.get(index));
        updateHashMap();
    }

    public AudioTrack playingTrack(){
        return player.getPlayingTrack();
    }


    public void leave(){
        if(guildAudioManager.isConnected()){
            if(this.future != null && !this.future.isCancelled()){
                this.future.cancel(false);
            }
            if(!this.queue.isEmpty()){
                clearQueue();
            }
            guildAudioManager.closeAudioConnection();
        }
    }
}