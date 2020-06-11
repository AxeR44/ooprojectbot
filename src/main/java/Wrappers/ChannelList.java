package Wrappers;

import Main.ProvaBot;

import java.util.ArrayList;
import java.util.List;

public class ChannelList {
    private List<TelegramWrapper> channelGuilds;

    public ChannelList(){
        this.channelGuilds = new ArrayList<>();
    }

    public synchronized boolean add(String guildId, String channelName, String channelID){
        TelegramWrapper wrapper = getWrapperFromGuildID(guildId);
        if(wrapper != null){
            if(!wrapper.hasChannelID(channelID) && !wrapper.hasChannelName(channelName)){
                wrapper.addChannel(channelName, channelID);
                return true;
            }
            return false;
        }
        wrapper = new TelegramWrapper(guildId, ProvaBot.getJda().getGuildById(guildId).getName());
        wrapper.addChannel(channelName, channelID);
        channelGuilds.add(wrapper);
        return true;
    }

    public synchronized boolean remove(String guildId, String channelName){
        TelegramWrapper wrapper = getWrapperFromGuildID(guildId);
        if(wrapper != null){
            if(wrapper.hasChannelName(channelName)){
                wrapper.removeChannel(channelName);
                return true;
            }
        }
        return false;
    }

    public synchronized TelegramWrapper getWrapperFromGuildName(String guildName){
        for(TelegramWrapper wrp : channelGuilds){
            if(guildName.equals(wrp.getGuildName())){
                return wrp;
            }
        }
        return null;
    }

    public synchronized TelegramWrapper getWrapperFromGuildID(String guildID){
        for(TelegramWrapper wrp : channelGuilds){
            if(guildID.equals(wrp.getGuildID())){
                return wrp;
            }
        }
        return null;
    }
}
