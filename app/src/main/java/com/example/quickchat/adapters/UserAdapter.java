package com.example.quickchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickchat.R;
import com.example.quickchat.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnItemClickListener onItemClickListener;

    public UserAdapter(List<User> userList, OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUserName.setText(user.getName() + " " + user.getSurname());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> filteredUserList) {
        this.userList = filteredUserList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_username);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }
}