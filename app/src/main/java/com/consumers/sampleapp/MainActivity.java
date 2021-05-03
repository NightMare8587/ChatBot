package com.consumers.sampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    List<String> commands = new ArrayList<>();
    List<String> leftOr = new ArrayList<>();
    GoogleSignInOptions gso;
    FirebaseAuth auth;
    DatabaseReference reference;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    EditText editText;
    Button button;
    GoogleSignInClient client;
    String url = "http://jethiya-ai.herokuapp.com/api/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Chat").child(Objects.requireNonNull(auth.getUid()));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    commands.clear();
                    leftOr.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        commands.add(dataSnapshot.child("message").getValue(String.class));
                        leftOr.add(dataSnapshot.child("sender").getValue(String.class));
                    }
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(new adapter(commands,leftOr));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.length() == 0){
                    editText.requestFocus();
                    editText.setError("Field can't be empty");
                    return;
                }
                auth = FirebaseAuth.getInstance();

                chat chat = new chat(editText.getText().toString().trim(),auth.getUid()+"",""+System.currentTimeMillis(),"0");
                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Chat").child(Objects.requireNonNull(auth.getUid()));
                reference.child(System.currentTimeMillis()+"").setValue(chat);

                updateChat();


            }
        });
    }

    private void updateChat() {
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Chat").child(Objects.requireNonNull(auth.getUid()));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    commands.clear();
                    leftOr.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        commands.add(dataSnapshot.child("message").getValue(String.class));
                        leftOr.add(dataSnapshot.child("sender").getValue(String.class));
                    }
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(new adapter(commands,leftOr));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot();
        editText = findViewById(R.id.enterText);
        button = findViewById(R.id.buttonSend);
        recyclerView = findViewById(R.id.myView);
    }
}