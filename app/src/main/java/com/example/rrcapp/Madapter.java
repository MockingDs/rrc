package com.example.rrcapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Madapter extends RecyclerView.Adapter<Madapter.ViewHolder>{

    private List<Mdatamodel> userList;
    private Context context;
    private boolean isSelectionMode = false;
    private List<Integer> selectedPositions = new ArrayList<>();
    private OnItemClickListener listener;


    public Madapter(Context context, List<Mdatamodel> userList,OnItemClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener=listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclecard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mdatamodel user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setSelectionMode(boolean b) {

    }

    public List<Integer> getSelectedPositions() {
        return selectedPositions;
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener ,View.OnClickListener {
        private TextView userNameTextView;
        private TextView emailTextView;
        private CheckBox userCheckBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.t2);
            emailTextView = itemView.findViewById(R.id.t1);
            userCheckBox = itemView.findViewById(R.id.checkbox);

            userCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelectionMode) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            if (userCheckBox.isChecked()) {
                                selectedPositions.add(position);
                            } else {
                                selectedPositions.remove(Integer.valueOf(position));
                            }
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

        }

        public void onClick(View v) {
            if (!isSelectionMode) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(position);
            }
            }
        }

        public void bind(Mdatamodel user) {
            userNameTextView.setText(user.getUserName());
            emailTextView.setText(user.getEmailId());
            userCheckBox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
            userCheckBox.setChecked(selectedPositions.contains(getAdapterPosition()));
        }


        @Override
        public boolean onLongClick(View v) {
            isSelectionMode = !isSelectionMode;

            for (Mdatamodel user : userList) {
                user.setCheckboxVisible(isSelectionMode);
            }
            if (!isSelectionMode){
                selectedPositions.clear();
            }
           if (isSelectionMode) {
               if (selectedPositions.size() == 0) {

                    for (Mdatamodel user : userList) {

                        userCheckBox.setVisibility(View.GONE);
                        selectedPositions.clear();
                    }
                }
            }
            notifyDataSetChanged();
            return true;
        }


    }
    public void clearSelections() {

        selectedPositions.clear();

        notifyDataSetChanged();
    }
    public boolean isSelectionMode() {

        return isSelectionMode;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }



}
