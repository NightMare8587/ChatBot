package com.consumers.sampleapp;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<String> commands = new ArrayList<>();
    List<String> leftOr = new ArrayList<>();
    int send = 0;
    public adapter(List<String> commands,List<String> leftOr){
        this.commands = commands;
        this.leftOr = leftOr;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if(viewType == send){
            view = layoutInflater.inflate(R.layout.card_message_revive,parent,false);
            return new ReciveHolder(view);
        }else{
            view = layoutInflater.inflate(R.layout.card_message_send,parent,false);
            return new SentViewHolder(view);
        }
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{
        TextView send;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            send = itemView.findViewById(R.id.textSend);
        }
    }

    public class ReciveHolder extends RecyclerView.ViewHolder{
        TextView recive;
        public ReciveHolder(@NonNull View itemView) {
            super(itemView);
            recive = itemView.findViewById(R.id.textRecive);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(Integer.parseInt(leftOr.get(position)) == send)
            return 0;
        else
            return 1;
//        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getClass() == SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder) holder;
            ((SentViewHolder) holder).send.setText(commands.get(position));
            ((SentViewHolder) holder).send.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipboardManager cm = (ClipboardManager)view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(((SentViewHolder) holder).send.getText().toString());
                    Toast.makeText(view.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }else{
            ReciveHolder reciveHolder = (ReciveHolder) holder;
            ((ReciveHolder) holder).recive.setText(commands.get(position));
            ((ReciveHolder) holder).recive.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipboardManager cm = (ClipboardManager)view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(((ReciveHolder) holder).recive.getText().toString());
                    Toast.makeText(view.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return commands.size();
    }


}
