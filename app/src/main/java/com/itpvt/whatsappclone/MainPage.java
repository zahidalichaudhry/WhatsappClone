package com.itpvt.whatsappclone;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itpvt.whatsappclone.Chat.ChatListAdapter;
import com.itpvt.whatsappclone.Chat.ChatObject;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity {
Button Logout;

    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;

    ArrayList<ChatObject> chatList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Fresco.initialize(this);
        Logout=(Button)findViewById(R.id.logout);
        Button mFindUser = findViewById(R.id.findUser);
        mFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FindUserActivity.class));
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getApplicationContext(),VerifyPhoneActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });
        getPermissions();
        initializeRecyclerView();
        getUserChatList();
    }
    private void getUserChatList(){
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");

        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                        boolean  exists = false;
                        for (ChatObject mChatIterator : chatList){
                            if (mChatIterator.getChatId().equals(mChat.getChatId()))
                                exists = true;
                        }
                        if (exists)
                            continue;
                        chatList.add(mChat);
                        mChatListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new
                    String[]{android.Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    private void initializeRecyclerView() {
        chatList = new ArrayList<>();
        mChatList= findViewById(R.id.chatList);
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(false);
        mChatListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mChatList.setLayoutManager(mChatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(mChatListAdapter);
    }
}
