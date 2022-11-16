package com.example.electrocarmanager.Notification;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.electrocarmanager.R;

public class NotificationGroupHolder extends RecyclerView.ViewHolder {
    TextView date;
    RecyclerView notification;

    public NotificationGroupHolder(View view)
    {
        super(view);
        date=view.findViewById(R.id.date);
        notification=view.findViewById(R.id.notification);
    }

}
