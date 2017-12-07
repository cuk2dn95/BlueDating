package com.example.pc.bluedating.Login_Function;

import com.example.pc.bluedating.MainScreen_Function.Chatting_Function.Message;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

/**
 * Created by KA on 12/5/2017.
 */

public class MessageListAdapter extends MessagesListAdapter {
    public MessageListAdapter(String senderId, ImageLoader imageLoader) {
        super(senderId, imageLoader);
    }

    public void addMessage(Message message,boolean b){
        getSelectedMessages();

    }
}
