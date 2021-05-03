package com.consumers.sampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class MainActivity extends AppCompatActivity {
    List<String> commands = new ArrayList<>();
    List<String> leftOr = new ArrayList<>();
    GoogleSignInOptions gso;
    FirebaseAuth auth;
    FastDialog fastDialog;
    DatabaseReference reference;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    EditText editText;
    Button button;
    GoogleSignInClient client;
    String url = "https://jethiya-ai.herokuapp.com/api/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        fastDialog = new FastDialogBuilder(MainActivity.this, Type.PROGRESS)
                .progressText("Loading Previous Chat...")
                .setAnimation(Animations.FADE_IN)
                .create();
        fastDialog.show();
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

                    fastDialog.dismiss();
                }else
                    fastDialog.dismiss();
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
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                chat chat = new chat(editText.getText().toString().trim(),auth.getUid()+"",""+System.currentTimeMillis(),"0");
                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Chat").child(Objects.requireNonNull(auth.getUid()));
                reference.child(System.currentTimeMillis()+"").setValue(chat);

                updateChat();
                Uri uri = Uri.parse(url)
                        .buildUpon()
                        .appendPath(editText.getText().toString()).build();
                URL newU = null;
                try {
                    newU = new URL(uri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Log.i("URL",newU+"");
                url = url + editText.getText().toString();
                Log.i("url",url);
                editText.setText("");
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String reply = String.valueOf(response.get("Response"));
//                            Toast.makeText(MainActivity.this, ""+response.get("Response"), Toast.LENGTH_SHORT).show();
                            chat chat = new chat(reply,auth.getUid(),""+System.currentTimeMillis(),"1");
                            reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Chat").child(Objects.requireNonNull(auth.getUid()));
                            reference.child(System.currentTimeMillis()+"").setValue(chat);
                            updateChat();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Failure " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjectRequest);
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