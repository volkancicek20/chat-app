package com.volkancicek.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    RecyclerView recyclerView;
    EditText messageText;
    RecyclerAdapter recyclerAdapter;
    public ArrayList<String> chatMessages = new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_signout)
        {
            auth.signOut();
            Intent intent = new Intent(getApplicationContext(),SignActivity.class);

            startActivity(intent);

        }else if(item.getItemId() == R.id.menu_profile){
            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageText = findViewById(R.id.chatText);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerAdapter = new RecyclerAdapter(chatMessages);

        RecyclerView.LayoutManager recyclerManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(recyclerAdapter);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        getData();
        auth = FirebaseAuth.getInstance();
    }
    public void sendButton(View view)
    {
        String sendMessage = messageText.getText().toString();
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        FirebaseUser user = auth.getCurrentUser();
        String userEmail = user.getEmail();

        databaseReference.child("Chats").child(uuidString).child("usermessage").setValue(sendMessage);
        databaseReference.child("Chats").child(uuidString).child("usermail").setValue(userEmail);
        databaseReference.child("Chats").child(uuidString).child("usermessagetime").setValue(ServerValue.TIMESTAMP);
        messageText.setText("");
        getData();
    }

    public void getData(){

        DatabaseReference newDataReference = database.getReference("Chats");
        Query query = newDataReference.orderByChild("usermessagetime");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatMessages.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HashMap<String,String> hashMap = (HashMap<String,String>) dataSnapshot.getValue();
                    String email = hashMap.get("usermail");
                    String message = hashMap.get("usermessage");

                    chatMessages.add(email+":  "+message);
                    recyclerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
}