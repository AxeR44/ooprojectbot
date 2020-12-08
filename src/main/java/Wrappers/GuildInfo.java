package Wrappers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.Set;

public class GuildInfo {
    private static HashMap<String, GuildRoleManagement> info = new HashMap<>();

    public static GuildRoleManagement getGuildRoleManagement(Guild g){
        return info.get(g.getId());
    }

    public static void addGuildRoleManagement(String guildID, String infoChannelID, String infoMessageID){
        info.put(guildID, new GuildRoleManagement(infoMessageID, infoChannelID));
    }

    public static void replaceGuildRoleManagement(String guildID, GuildRoleManagement management){
        info.replace(guildID, management);
    }

    public static class GuildRoleManagement{

        private String infoChannelID;
        private String infoMessage;
        private HashMap<String,Role> enabledRoles;

        public GuildRoleManagement(String m, String infoChannelID){
            this.enabledRoles = new HashMap<>();
            this.infoMessage = m;
            this.infoChannelID = infoChannelID;
        }

        public void addRole(Role role, String emoteString){
            this.enabledRoles.put(emoteString, role);
        }

        public boolean isRoleEnabled(Role role){
            return this.enabledRoles.containsValue(role);
        }

        public boolean isEmoteAssociated(String emoteString){
            return this.enabledRoles.containsKey(emoteString);
        }

        public String getInfoChannelID(){
            return this.infoChannelID;
        }

        public void setInfoChannelID(String infoChannelID){
            this.infoChannelID = infoChannelID;
        }

        public String getInfoMessage(){
            return this.infoMessage;
        }

        public void setInfoMessage(String infoMessage){
            this.infoMessage = infoMessage;
        }

        public Role getRoleFromEmote(String emoteString){
            return enabledRoles.get(emoteString);
        }

        public Set<String> getEmotes(){
            return this.enabledRoles.keySet();
        }
    }
}
