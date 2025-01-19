package com.example.rrcapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private List<Membersmodel> members;

    public void setMembers(List<Membersmodel> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclecard, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Membersmodel member = members.get(position);
        holder.usernameTextView.setText(member.getUsername());
        holder.emailIDTextView.setText(member.getEmailID());
    }

    @Override
    public int getItemCount() {
        return members == null ? 0 : members.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView emailIDTextView;

        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.t1);
            emailIDTextView = itemView.findViewById(R.id.t2);
        }
    }
}
