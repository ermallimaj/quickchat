package com.example.quickchat.adapters;


import static com.example.quickchat.utils.TimeUtils.formatTimeAgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickchat.R;
import com.example.quickchat.models.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;

    public ChatAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.tvUsername.setText(chat.getUsername());
        holder.tvLastMessage.setText(chat.getLastMessage());
        holder.tvTimestamp.setText(formatTimeAgo(chat.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void removeItem(int position) {
        chatList.remove(position);
        notifyItemRemoved(position);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername, tvLastMessage, tvTimestamp;
        ImageView ivProfile;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            ivProfile = itemView.findViewById(R.id.iv_profile);
        }
    }
}
