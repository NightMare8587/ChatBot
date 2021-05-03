package com.consumers.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> commands = new ArrayList<>();
    GoogleSignInOptions gso;
    FirebaseAuth auth;
    DatabaseReference reference;
    GoogleSignInClient client;
    String url = "http://jethiya-ai.herokuapp.com/api/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();

    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot();
    }
}