package com.example.quickchat.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickchat.R;
import com.example.quickchat.models.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context context;
    private ArrayList<Message> messages;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            // Sent message layout
            view = LayoutInflater.from(context).inflate(R.layout.item_message_right, parent, false);
        } else {
            // Received message layout
            view = LayoutInflater.from(context).inflate(R.layout.item_message_left, parent, false);
        }
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvMessage.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        SharedPreferences preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        int currentUserId = preferences.getInt("userId", -1);

        Message message = messages.get(position);
        return message.getUserId() == currentUserId ? 1 : 0; // Sent if userId matches currentUserId
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        public MessageViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == 1) {
                // Sent message
                tvMessage = itemView.findViewById(R.id.tv_message_right);
            } else {
                // Received message
                tvMessage = itemView.findViewById(R.id.tv_message_left);
            }
        }
    }
}
