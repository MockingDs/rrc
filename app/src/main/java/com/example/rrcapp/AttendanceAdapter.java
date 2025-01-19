package com.example.rrcapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    private List<Attendancedata> attendanceList;
    private Context context;

    public AttendanceAdapter(Context context, List<Attendancedata> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attrecycle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attendancedata attendanceData = attendanceList.get(position);
        holder.bind(attendanceData);
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public List<Integer> getSelectedPositions() {
        List<Integer> selectedPositions = new ArrayList<>();
        for (int i = 0; i < attendanceList.size(); i++) {
            if (attendanceList.get(i).isSelected()) {
                selectedPositions.add(i);
            }
        }
        return selectedPositions;
    }

    public void clearSelectedPositions() {
        for (Attendancedata data : attendanceList) {
            data.setSelected(false);
        }
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView emailTextView;
        private TextView usernameTextView;
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.t1);
            usernameTextView = itemView.findViewById(R.id.t2);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Attendancedata attendanceData = attendanceList.get(position);
                attendanceData.setSelected(checkBox.isChecked());
            }
        }

        public void bind(Attendancedata attendanceData) {
            emailTextView.setText(attendanceData.getEmailID());
            usernameTextView.setText(attendanceData.getUsername());
            checkBox.setChecked(attendanceData.isSelected());
        }


    }
}
