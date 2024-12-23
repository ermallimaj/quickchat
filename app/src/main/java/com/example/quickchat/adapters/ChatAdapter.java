package com.example.quickchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quickchat.R;
import com.example.quickchat.models.Chat;
import com.example.quickchat.utils.TimeUtils;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;

    public ChatAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each chat item (using your previous layout)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.tvUsername.setText(chat.getUsername());
        // Here, you can also bind lastMessage and timestamp if you have these properties in your Chat model

        // If you have timestamp in your model, format it
        holder.tvTimestamp.setText(TimeUtils.formatTimeAgo(chat.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername, tvTimestamp;  // You can add tvLastMessage if needed

        public ChatViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_chat_username);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp); // assuming timestamp TextView exists in XML
        }
    }
}
