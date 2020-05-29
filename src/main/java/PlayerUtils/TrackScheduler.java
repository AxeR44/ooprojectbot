package PlayerUtils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final HashMap<Integer, AudioTrack> tracksInQueue;

    public TrackScheduler(AudioPlayer player){
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.tracksInQueue = new HashMap<>();
    }

    public void queue(AudioTrack track){
        if(!player.startTrack(track, true)){
            this.queue.offer((track));
        }
        updateHashMap();
    }

    public boolean nextTrack(){
        boolean flag = true;
        if(queue.isEmpty()) {
            flag = false;
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
}