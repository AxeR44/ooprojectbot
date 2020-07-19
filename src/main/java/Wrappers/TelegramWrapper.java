package Wrappers;

import java.util.HashMap;
import java.util.Map;

public class TelegramWrapper {
    private final String guildName;
    private final String guildID;
    private final HashMap<String, String> guildChannels;

    public TelegramWrapper(String guildID, String guildName){
        this.guildID = guildID;
        this.guildName = guildName;
        this.guildChannels= new HashMap<>();
    }

    public TelegramWrapper(String guildID, String guildName ,HashMap<String,String> values){
        this.guildID = guildID;
        this.guildName = guildName;
        this.guildChannels = values;
    }

    public synchronized void addChannel(String channelName, String channelID){
        guildChannels.put(channelID, channelName);
    }

    public synchronized void removeChannel(String channelName){
        guildChannels.remove(getIdFromName(channelName));
    }

    public synchronized String getIdFromName(String channelName){
        for(Map.Entry<String,String> entry : guildChannels.entrySet()){
            if(entry.getValue().equals(channelName)){
                return entry.getKey();
            }
        }
        return null;
    }

    public synchronized String getNameFromId(String channelId){
        return guildChannels.get(channelId);
    }

    public String getGuildID(){
        return this.guildID;
    }

    public synchronized HashMap<String,String> getGuildChannels(){
        return this.guildChannels;
    }

    public synchronized boolean hasChannelID(String channelID){
        return this.guildChannels.containsKey(channelID);
    }

    public synchronized boolean hasChannelName(String channelName){
        return this.guildChannels.containsValue(channelName);
    }

    public String getGuildName(){
        return this.guildName;
    }
}
