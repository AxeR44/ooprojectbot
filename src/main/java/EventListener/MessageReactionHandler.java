package EventListener;

import Wrappers.GuildInfo;
import Wrappers.MessageCountWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class MessageReactionHandler extends ListenerAdapter {

    private HashMap<String, MessageCountWrapper> reactionsCount;

    public MessageReactionHandler(){
        reactionsCount = new HashMap<>();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        String id = event.getMessageId();
        Message msg = event.getChannel().retrieveMessageById(id).complete();
        if (msg.getContentRaw().startsWith("@everyone\nVoting") && msg.getAuthor().getId().equals(event.getGuild().getSelfMember().getId())) {
            addReactionToMap(id, event);
        } else if (msg.getContentRaw().startsWith("Welcome to") && msg.getAuthor().getId().equals(event.getGuild().getSelfMember().getId())) {
            if (!event.getMember().getId().equals(event.getGuild().getSelfMember().getId())){
                GuildInfo.GuildRoleManagement management = GuildInfo.getGuildRoleManagement(event.getGuild());
                Role role = management.getRoleFromEmote(event.getReactionEmote().getAsReactionCode());
                event.getGuild().addRoleToMember(event.getMember(), GuildInfo.getGuildRoleManagement(event.getGuild()).getRoleFromEmote(event.getReactionEmote().getAsReactionCode())).queue();
                if(event.getMember().getRoles().contains(role)){
                    event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                }else{
                    event.getGuild().addRoleToMember(event.getMember(), role).queue();
                }
                event.getTextChannel().removeReactionById(management.getInfoMessage(), event.getReactionEmote().getAsReactionCode(), event.getUser()).complete();
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event){
        String id = event.getMessageId();
        Message msg = event.getChannel().retrieveMessageById(id).complete();
        if(msg.getContentRaw().startsWith("@everyone\nVoting") && msg.getAuthor().getId().equals(event.getGuild().getSelfMember().getId())){
            removeReactionFromMap(id, event);
        }
    }

    private synchronized void removeReactionFromMap(String msgID, MessageReactionRemoveEvent event){
        String emoteName = event.getReactionEmote().getName();
        String uID = event.getMember().getId();
        MessageCountWrapper wrp = reactionsCount.get(msgID);
        if(wrp.hasReaction(emoteName)){
            if(!wrp.isVoteDuplicate(uID)){
                if(wrp.setVoteCount(emoteName, 1)){
                    wrp.setVoted(event.getMember().getId(),false);
                }
            }else{
                wrp.setVoteDuplicate(uID, false);
            }
            reactionsCount.replace(msgID, wrp);
        }
    }

    public synchronized HashMap<String, Integer> getReactionsCount(String msgID){
        HashMap<String, Integer> count = reactionsCount.get(msgID).getCount();
        reactionsCount.remove(msgID);
        return count;
    }

    private synchronized void addReactionToMap(String msgID, MessageReactionAddEvent event){
        String emoteName = event.getReactionEmote().getName();
            if(!reactionsCount.containsKey(msgID)){
                reactionsCount.put(msgID, new MessageCountWrapper(event.getTextChannel().getGuild().getMembers()));  //TICK 0 CROSS 1
            }
            MessageCountWrapper wrp = reactionsCount.get(msgID);
            String uID = event.getMember().getId();
            if(!event.getGuild().getSelfMember().getId().equals(uID) && event.getUser().isBot()) return;
            else if(!event.getGuild().getSelfMember().getId().equals(uID)){
                if(wrp.hasUserAlreadyVoted(uID)){
                    wrp.setVoteDuplicate(uID, true);
                    event.getTextChannel().removeReactionById(msgID, emoteName, event.getMember().getUser()).complete();
                }else{
                    if(wrp.hasReaction(emoteName)){
                        if(wrp.setVoteCount(emoteName, 0)){
                            wrp.setVoted(uID, true);
                        }
                        reactionsCount.replace(msgID, wrp);
                    }else{
                        event.getTextChannel().removeReactionById(msgID, emoteName, event.getMember().getUser()).complete();
                    }
                }
            }else{
                wrp.botAddReaction(emoteName);
            }
    }
}
