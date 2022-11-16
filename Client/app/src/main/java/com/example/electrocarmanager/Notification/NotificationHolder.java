package com.example.electrocarmanager.Notification;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.electrocarmanager.R;

public class NotificationHolder extends RecyclerView.ViewHolder {

    TextView time,from,to,last,distance;
    LinearLayout wholeItem;

    public NotificationHolder(View view)
    {
        super(view);
        this.time=view.findViewById(R.id.time);
        this.from=view.findViewById(R.id.from);
        this.to=view.findViewById(R.id.to);
        this.last=view.findViewById(R.id.last);
        this.distance=view.findViewById(R.id.distance);
        this.wholeItem=view.findViewById(R.id.wholeItem);
    }
}
