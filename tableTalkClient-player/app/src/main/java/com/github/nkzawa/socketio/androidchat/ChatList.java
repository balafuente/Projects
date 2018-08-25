package com.github.nkzawa.socketio.androidchat;

/**
 * Created by Blake on 4/18/2018.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import org.json.*;



import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;




import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatList extends ListActivity {

    private List<String> mTopics;
    private List<String> mUsernames;
    private Socket mSocket;
    private TextView text;



    protected List<String> getActiveChats(){
        List<String> activeChats = new ArrayList<String>();

        // Ask server for a list of chat IDs and return them to the create function

        return activeChats;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // We'll define a custom screen layout here (the one shown above), but
        // typically, you could just use the standard ListActivity layout.
        setContentView(R.layout.topic_list);  //change activity?

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.on("sendConversationsList", sendConversationList);

        text = (TextView) findViewById(R.id.mainText);
        mTopics= new ArrayList<String>();


        mSocket.emit("getConversationsList");








        mTopics.add("New Topic");

        // initiate the listadapter
        ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this,
                R.layout.topics_list_item, R.id.listText, mTopics);
        // assign the list adapter
        setListAdapter(myAdapter);





        setContentView(R.layout.topic_list);
    }

    private Emitter.Listener sendConversationList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            try {
                System.out.println("trying to get chats");
                JSONArray conversationList= new JSONArray();
                conversationList = (JSONArray) args[0];


                for(int i=0;i<conversationList.length();i++){
                    JSONObject chat = conversationList.getJSONObject(i);
                    String topic = chat.getString("topic");
                    mTopics.add(topic);
                }
            } catch (JSONException e) {
                return;
            }
            System.out.println("got chats");
            //mSocket.emit("getFullPlayerList");

        }
    };


    private Emitter.Listener getFullPlayerList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
                //mUsernames = data.get("Players");
            } catch (JSONException e) {
                return;
            }

        }
    };



            // when an item of the list is clicked

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        String selectedItem = (String) getListView().getItemAtPosition(position);
        //String selectedItem = (String) getListAdapter().getItem(position);
        //text.setText("You clicked " + selectedItem + " at position " + position);



        //Option if starting new conversation
        if(selectedItem == "New Topic"){
            //get user to input new topic title
            //get user to select players?
            //Start blank main fragement with settings
        }



        //Option if continuing old conversation
        else{
            String selectedTopic = selectedItem;
            System.out.println("selected " + selectedItem);
            Intent intent = new Intent();
            intent.putExtra("topic", selectedTopic);
            //startActivity(intent);
            finish();

        }





    }






}
