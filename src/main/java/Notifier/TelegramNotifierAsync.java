package Notifier;

import Wrappers.ChannelList;
import Wrappers.TelegramWrapper;
import net.dv8tion.jda.api.JDA;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import Main.ProvaBot;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramNotifierAsync extends TelegramLongPollingBot{

    private ChannelList chList;

    public TelegramNotifierAsync(ChannelList list){
        this.chList = list;
    }

    @Override
    public String getBotUsername(){
        return "Ooprojectbot";
    }

    @Override
    public void onUpdateReceived(Update update){
        new Thread(()->{
            handleCommand(update);
        }).start();
    }

    @Override
    public String getBotToken(){
        return new ProvaBot().getTelegramAPIK();
    }

    private void handleCommand(Update update){
        if(update.hasMessage() && update.getMessage().hasText()){
            Message m = update.getMessage();
            String messageText = m.getText();
            final SendMessage sendMessage = new SendMessage();
            if(messageText.startsWith("/sendDiscord ")) {
                String[] args = messageText.substring(13).split(" -- "); // args[0]: message args[1]: guildName
                User u = m.getFrom();
                TelegramWrapper wrapper = chList.getWrapperFromGuildName(args[1]);
                if (wrapper != null) {
                    if (wrapper.hasChannelID(m.getChatId().toString())) {
                        JDA jda = ProvaBot.getJda();
                        jda.getGuildById(wrapper.getGuildID())
                                .getDefaultChannel()
                                .sendMessage("Telegram User " + u.getUserName() + " Wrote " + args[0])
                                .queue(success -> {
                                    sendMessage.setChatId(m.getChatId())
                                            .setText("Done")
                                    .setReplyToMessageId(m.getMessageId());
                                    try{
                                        execute(sendMessage);
                                    }catch(TelegramApiException e){
                                        e.printStackTrace();
                                    }
                                }, error ->{
                                    sendMessage.setChatId(m.getChatId())
                                            .setText("Error")
                                            .setReplyToMessageId(m.getMessageId());
                                    try{
                                        execute(sendMessage);
                                    }catch(TelegramApiException e){
                                        e.printStackTrace();
                                    }
                                });
                        return;
                    }
                }

            }
            sendMessage.setChatId(m.getChatId())
                    .setText("Error")
                    .setReplyToMessageId(m.getMessageId());
            try{
                execute(sendMessage);
            }catch(TelegramApiException e){
                e.printStackTrace();
            }
        }
    }

    public boolean sendMessage(String guildID, String channelName ,String message, String sender){
        TelegramWrapper wrapper = chList.getWrapperFromGuildID(guildID);
        if(wrapper != null){
            if(wrapper.hasChannelName(channelName)){
                SendMessage sendMessage = new SendMessage()
                        .setChatId(wrapper.getIdFromName(channelName))
                        .setText(sender + " wrote " + message + " on guild " + wrapper.getGuildName());
                try{
                    execute(sendMessage);
                    return true;
                }catch (TelegramApiException e){
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
