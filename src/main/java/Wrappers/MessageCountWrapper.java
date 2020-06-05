package Wrappers;

import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;
import java.util.List;

public class MessageCountWrapper {

    private HashMap<String, Boolean[]> votingMembers;
    private Integer[] voteCount;

    public MessageCountWrapper(List<Member> guildMembers){
        voteCount = new Integer[]{0,0};
        votingMembers = new HashMap<>();
        for(Member m : guildMembers){
            votingMembers.put(m.getId(), new Boolean[]{false, false});
        }
    }

    public synchronized boolean hasUserAlreadyVoted(String uID){
        return this.votingMembers.get(uID)[0];
    }

    public synchronized Integer[] getCount(){
        return this.voteCount;
    }

    public synchronized void setVoted(String uID, boolean value){
        Boolean[] prev = this.votingMembers.get(uID);
        prev[0] = value;
        this.votingMembers.replace(uID, prev);
    }

    public synchronized void setVoteCount(int position, int tag){
        switch(tag){
            case 0:
                voteCount[position]++;
                break;
            case 1:
                voteCount[position]--;
                break;
        }
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
