package EventListener;

import Wrappers.MessageCountWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class MessageReactionHandler extends ListenerAdapter {

    private static HashMap<String, MessageCountWrapper> reactionsCount;
    public final String TICK = "\u2705";
    public final String CROSS = "\u274C";

    public MessageReactionHandler(){
        reactionsCount = new HashMap<>();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event){
        String id = event.getMessageId();
        Message msg = event.getChannel().retrieveMessageById(id).complete();
        if(msg.getContentRaw().startsWith("Voting")){
            addReactionToMap(id, event);
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event){
        String id = event.getMessageId();
        Message msg = event.getChannel().retrieveMessageById(id).complete();
        if(msg.getContentRaw().startsWith("Voting")){
            removeReactionFromMap(id, event);
        }
    }

    private void removeReactionFromMap(String msgID, MessageReactionRemoveEvent event){
        String emoteName = event.getReactionEmote().getName();
        String uID = event.getMember().getId();
        if(emoteName.equals(TICK) || emoteName.equals(CROSS)){
            MessageCountWrapper wrp = reactionsCount.get(msgID);
            if(!wrp.isVoteDuplicate(uID)){
                wrapperModify(wrp, 1, emoteName);
                wrp.setVoted(event.getMember().getId(),false);
                reactionsCount.replace(msgID, wrp);
            }else{
                wrp.setVoteDuplicate(uID, false);
            }
        }
    }

    public static Integer[] getReactionsCount(String msgID){
        Integer[] arr = reactionsCount.get(msgID).getCount();
        reactionsCount.remove(msgID);
        return arr;
    }

    private void addReactionToMap(String msgID, MessageReactionAddEvent event){
        String emoteName = event.getReactionEmote().getName();
        if(emoteName.equals(TICK) || emoteName.equals(CROSS)){
            if(!reactionsCount.containsKey(msgID)){
                reactionsCount.put(msgID, new MessageCountWrapper(event.getTextChannel().getGuild().getMembers()));  //TICK 0 CROSS 1
            }
            MessageCountWrapper wrp = reactionsCount.get(msgID);
            String uID = event.getMember().getId();
            if(!event.getGuild().getSelfMember().getId().equals(uID) && event.getUser().isBot()) return;
            else if(!event.getGuild().getSelfMember().getId().equals(uID)){
                //if user voted
                if(wrp.hasUserAlreadyVoted(uID)){
                    wrp.setVoteDuplicate(uID, true);
                    event.getTextChannel().removeReactionById(msgID, emoteName, event.getMember().getUser()).complete();
                }else{
                    wrapperModify(wrp, 0, emoteName);
                    wrp.setVoted(uID, true);
                    reactionsCount.replace(msgID, wrp);
                }
            }else{
                //it's me but do nothing
            }
        }
    }


    private void wrapperModify(MessageCountWrapper wrp, int tag, String emoteName){
        switch(emoteName) {
            case TICK:
                wrp.setVoteCount(0, tag);
                break;
            case CROSS:
                wrp.setVoteCount(1,tag);
                break;
        }
    }
}
