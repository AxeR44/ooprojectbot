package Wrappers;

import com.iwebpp.crypto.TweetNaclFast;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;
import java.util.List;

public class MessageCountWrapper {

    private HashMap<String, Boolean[]> votingMembers;
    private HashMap<String, Integer> reactionCount;

    public MessageCountWrapper(List<Member> guildMembers){
        reactionCount = new HashMap<>();
        votingMembers = new HashMap<>();
        for(Member m : guildMembers){
            votingMembers.put(m.getId(), new Boolean[]{false, false});
        }
    }

    public synchronized boolean botAddReaction(String reaction){
        if(reactionCount.containsKey(reaction)){
            return false;
        }
        reactionCount.put(reaction, 0);
        return true;
    }

    public synchronized boolean hasReaction(String Reaction){
        return reactionCount.containsKey(Reaction);
    }

    public synchronized boolean hasUserAlreadyVoted(String uID){
        return this.votingMembers.get(uID)[0];
    }

    public synchronized HashMap<String, Integer> getCount(){
        return this.reactionCount;
    }

    public synchronized void setVoted(String uID, boolean value){
        Boolean[] prev = this.votingMembers.get(uID);
        prev[0] = value;
        this.votingMembers.replace(uID, prev);
    }

    public synchronized boolean setVoteCount(String emoteName, int tag){
        if(tag == 1 || tag == 0){
            Integer count = reactionCount.get(emoteName);
            switch(tag) {
                case 0:
                    ++count;
                    break;
                case 1:
                    --count;
                    break;
            }
            reactionCount.replace(emoteName, count);
            return true;
        }
        return false;
    }

    public synchronized void setVoteDuplicate(String uID, boolean value){
        Boolean[] prev = this.votingMembers.get(uID);
        prev[1] = value;
        this.votingMembers.replace(uID, prev);
    }

    public synchronized boolean isVoteDuplicate(String uID){
        return this.votingMembers.get(uID)[1];
    }
}
