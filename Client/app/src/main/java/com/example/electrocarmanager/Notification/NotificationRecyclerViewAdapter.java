package com.example.electrocarmanager.Notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electrocarmanager.R;

import java.util.List;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationHolder> {
    List<Notification> data;
    LayoutInflater inflater;

    public NotificationRecyclerViewAdapter(List<Notification> data, Context context)
    {
        inflater=LayoutInflater.from(context);
        this.data=data;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType)
    {
        View view=inflater.inflate(R.layout.list,parent,false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder,int position)
    {
        Notification notification=data.get(position);
        holder.time.setText(notification.time);
        holder.from.setText("移动起始位置:"+notification.from);
        holder.to.setText("移动结束位置:"+notification.to);
        holder.last.setText("移动持续时间:"+notification.last+"");
        holder.distance.setText("移动距离:"+notification.distance+"");
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

}
