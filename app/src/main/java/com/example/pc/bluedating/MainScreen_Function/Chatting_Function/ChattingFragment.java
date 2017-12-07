package com.example.pc.bluedating.MainScreen_Function.Chatting_Function;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.example.pc.bluedating.DataObject.DataUserResolver;
import com.example.pc.bluedating.MainScreen_Function.Chatting_Function.Dialog.Dialog;
import com.example.pc.bluedating.Object.User;
import com.example.pc.bluedating.R;
import com.example.pc.bluedating.Utils.BitmapUtils;
import com.example.pc.bluedating.Utils.BlueDatingApplication;
import com.github.nkzawa.emitter.Emitter;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by PC on 11/4/2017.
 */

public class ChattingFragment extends Fragment {


    private static final String STORAGE_MESSAGES = "message";

    @BindView(R.id.dialogsList)
    DialogsList mDialogList;
    DialogsListAdapter<Dialog> mDialogsListAdapter;
    ArrayList<Dialog> dialogs;
    @BindView(R.id.swipe_layout_dialog)
    SwipeRefreshLayout mDialogLayout;
    @BindView(R.id.messagesList)
    MessagesList mMessagesList;
    @BindView(R.id.messagesInput)
    MessageInput mMessageInput;

    AppCompatActivity context;
    Dialog mCurrentDialog;
    MessagesListAdapter<Message> mMessageAdapter;
    openMessageList instance;
    ArrayList<User> users;
    boolean isOpen=false;
    Author sender,receiver;
    ArrayList<Message> messHistory;
    User sendTo;

    Map<String,ArrayList<Message>> messages = new HashMap<>();

    public void setmDialogsListAdapter(DialogsListAdapter<Dialog> mDialogsListAdapter) {
        this.mDialogsListAdapter = mDialogsListAdapter;
    }

    public void setContext(AppCompatActivity context) {
        this.context = context;
    }

    public void setMessageAdapter(MessagesListAdapter<Message> mMessageAdapter) {
        this.mMessageAdapter = mMessageAdapter;
    }

    public void setInstance(openMessageList instance) {
        this.instance = instance;
    }

   public interface  openMessageList{
        void openMessageList(String name,Dialog dialog);
    }

   @BindView(R.id.chatting_layout)
   SwipeLayout mSwipeLayout;

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public SwipeLayout getSwipeLayout() {
        return mSwipeLayout;
    }

    public boolean isOpen()
    {
        return isOpen;
    }
    public void toggle()
    {
        isOpen = !isOpen;
    }


    Emitter.Listener onReceiveMessage ;



    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting,container,false);
        ButterKnife.bind(this,view);

        mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        mSwipeLayout.setSwipeEnabled(false);
        mSwipeLayout.setRightSwipeEnabled(false);
        initMessage();


        mMessagesList.setAdapter(mMessageAdapter);
        sender = new Author(DataUserResolver.getInstance().getUser());
        mMessagesList.setLayoutManager(new LinearLayoutManager(getContext()));


        mDialogsListAdapter.setDatesFormatter(new DateFomatter());
        mDialogsListAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener<Dialog>() {
            @Override
            public void onDialogClick(Dialog dialog) {

                dialog.clearUnReadMessage();
                mDialogsListAdapter.updateItemById(dialog);
                receiver = new Author(dialog.getUser());
                mCurrentDialog = dialog;
                isOpen = !isOpen;
                instance.openMessageList(dialog.getDialogName(),dialog);
                messHistory = messages.get(dialog.getUser().getEmail());
                    mMessageAdapter.clear();
                    mMessageAdapter.notifyDataSetChanged();
                if(messHistory !=null &&
                        messHistory.size()>0) {
                    mMessageAdapter.addToEnd(messHistory, false);
                    mMessageAdapter.notifyDataSetChanged();
                }
                mSwipeLayout.toggle(true);

            }
        });





        mDialogList.setAdapter(mDialogsListAdapter);
        mDialogLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDialogLayout.setRefreshing(true);
                getOnlineUser();
            }
        });
            MessageInput messageInput = (MessageInput)mSwipeLayout.findViewById(R.id.messagesInput);

        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                boolean isValid = false;
                if(!input.equals(""))
                {
                    isValid = true;
                    Message message = new Message(sender,input.toString(),
                            new Date());
                    saveMessage(mCurrentDialog.getId(),message);
                    mMessageAdapter.addToStart(message,false);
                    mMessageAdapter.notifyItemInserted(mMessageAdapter.getItemCount()+1);
                    JSONObject object = new JSONObject();
                    try {
                        object.put("sender_id",message.getId());
                        object.put("message_body",message.getText());
                        object.put("created_date",message.getCreatedAt());
                        object.put("receiver_id",mCurrentDialog.getId());
                        mCurrentDialog.setLastMessage(message);
                        mDialogsListAdapter.updateItemById(mCurrentDialog);
                        BlueDatingApplication.getSocket().emit("sendMessage",object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                return isValid;
            }
        });




        return view;
    }



    void getOnlineUser()
    {
        BlueDatingApplication.getSocket().emit("getOnlineUser");
    }

    void initMessage(){


        onReceiveMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
            LoadingMessage loadingMessage = new LoadingMessage();
                Executor executors = Executors.newFixedThreadPool(1);
                loadingMessage.executeOnExecutor(executors,args);
            }
        };


        BlueDatingApplication.getSocket().on("sendMessageFromTwoUsers",onReceiveMessage);
    }



    class LoadingMessage extends AsyncTask<Object,Void,ArrayList<Message>>{
        @Override
        protected ArrayList<Message> doInBackground(Object... objects) {
            JSONObject object = (JSONObject)objects[0];

            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");
            ArrayList<Message> messagesList = new ArrayList<Message>();
            try {
                JSONArray messages = object.getJSONArray("messages");

                sendTo = getUser(object.getString("receiver"));
                User user = DataUserResolver.getInstance().getUser();
                Author sender = new Author(user);
                Author receiver = new Author(sendTo);
                if(messages.length()>0)
                { for(int i =messages.length()-1; i>=0 ;--i)
                     {
                    JSONObject message = messages.getJSONObject(i);
                    String emailSender = message.getString("sender");

                         Date date = dateFormatter.parse(message.getString("date"));
                    Message mess=null;
                    if(emailSender.equals(user.getEmail()))
                    {
                        mess = new Message(sender,message.getString("body"),date);
                    }else if(emailSender.equals(sendTo.getEmail())){
                        mess = new Message(receiver,message.getString("body"),date);
                    }

                    messagesList.add(mess);
                     }
                }
                else {
                    return messagesList;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (ParseException e){
                e.printStackTrace();
            }

            return messagesList;
        }


        @Override
        protected void onPostExecute(ArrayList<Message> message) {
            super.onPostExecute(message);
            Toast.makeText(getContext(),message.size()+":size",Toast.LENGTH_SHORT).show();
            if(message.size()>0) {

//                for(Message m : message)
//                    Toast.makeText(getContext(),"sender:"+m.getId()+" body:"+m.getText(),Toast.LENGTH_SHORT).show();
                messages.put(sendTo.getEmail(),message);
                for(Dialog dialog : dialogs){
                                if(dialog.getId().equals(sendTo.getEmail())){
                                    dialog.setLastMessage(message.get(message.size()-1));
                                    mDialogsListAdapter.updateItemById(dialog);
                                }
                            }

//                for(Message m : message) {
//                    Toast.makeText(getContext(),"mess:"+m.getText(),Toast.LENGTH_SHORT).show();
//
//                    messages.put(sendTo.getEmail(),message);
//
//                    if(!m.getId().equals(DataUserResolver.getInstance().getUser().getEmail()))
//                    {
//                        {
//                            messages.put(m.getId(), message);
//                            for(Dialog dialog : dialogs){
//                                if(dialog.getId().equals(m.getId())){
//                                    dialog.setLastMessage(message.get(message.size()-1));
//                                    mDialogsListAdapter.updateItemById(dialog);
//                                }
//                            }
//                            return;
//                        }
//
//
//                    }else {
//                        messages.put(sendTo.getEmail(),message);
//                    }
//                }
            }
            else {
                messages.put(sendTo.getEmail(),message);
            }
          //  mCurrentDialog.setLastMessage(messages.get(messages.size()-1));

        }
    }



    public void loadOnlineUser(ArrayList<User> users)
    {
        mDialogLayout.setRefreshing(true);
         dialogs= new ArrayList<>();
        messages.clear();

       for(User user : users) {
           Dialog dialog = new Dialog(user);
           dialogs.add(dialog);
            loadMessage(user);
           dialog.setLastMessage(new Message(new Author(user), "", new Date()));


       }
        mDialogsListAdapter.setItems(dialogs);
        mDialogsListAdapter.notifyDataSetChanged();
        mDialogLayout.setRefreshing(false);


    }

    void loadMessage(User receiver){

        JSONObject object = new JSONObject();
        try {
            object.put("limit",20);
            object.put("sender_id",DataUserResolver.getInstance().getUser().getEmail());
            object.put("receiver_id",receiver.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BlueDatingApplication.getSocket().emit("getMessageFromTwoUsers",object);


    }

    class DateFomatter implements  DateFormatter.Formatter
    {

        @Override
        public String format(Date date) {
            if (DateFormatter.isToday(date)) {
                return DateFormatter.format(date, DateFormatter.Template.TIME);
            } else if (DateFormatter.isYesterday(date)) {
                return "yesterday";

            } else {
                return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
            }
        }
    }

    public User getUser(String id){
        for (Dialog dialog : dialogs){
            if(dialog.getUser().getEmail().equals(id))
                return dialog.getUser();
        }
        return null;
    }


    public void addDialog(Dialog dialog,ArrayList<Message> message)
    {
        dialog.addUnReadMessage();
        dialog.setLastMessage(message.get(message.size()-1));
        dialogs.add(dialog);
        mDialogsListAdapter.notifyItemInserted(dialogs.size()-1);
        messages.put(dialog.getUser().getEmail(),message);
    }


    public void saveMessage(String email,Message e){
        if(messages.get(email) !=null)
        messages.get(email).add(e);
        else {
            ArrayList<Message> message = new ArrayList<>();
            message.add(e);
            messages.put(email,message);
        }
    }


    public Dialog getDialog(String id)
    {
        for(Dialog dialog : dialogs)
            if(dialog.getId().equals(id))
                return dialog;
        return  null;
    }







}
