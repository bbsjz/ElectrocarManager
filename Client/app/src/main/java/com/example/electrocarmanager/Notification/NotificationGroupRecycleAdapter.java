package com.example.electrocarmanager.Notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electrocarmanager.Entity.NotificationGroup;
import com.example.electrocarmanager.R;

import java.util.List;

public class NotificationGroupRecycleAdapter extends RecyclerView.Adapter<NotificationGroupRecycleAdapter.NotificationGroupHolder> {
    NotificationRecyclerViewAdapter.ItemClickListener itemClickListener;
    List<NotificationGroup> data;
    LayoutInflater inflater;
    Context context;

    public NotificationGroupRecycleAdapter(List<NotificationGroup> data, NotificationRecyclerViewAdapter.ItemClickListener itemClickListener,Context context)
    {
        this.data=data;
        this.itemClickListener = itemClickListener;
        inflater=LayoutInflater.from(context);
        this.context=context;
    }

    @NonNull
    @Override
    public NotificationGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view=inflater.inflate(R.layout.list_group,parent,false);
        return new NotificationGroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationGroupHolder holder,int position)
    {
        NotificationGroup notificationGroup=data.get(position);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        NotificationRecyclerViewAdapter  adapter=
                new NotificationRecyclerViewAdapter(notificationGroup.notifications,context);
        adapter.setClickListener(itemClickListener);
        holder.notification.setLayoutManager(linearLayoutManager);
        holder.notification.setAdapter(adapter);
        holder.date.setText(notificationGroup.date);
        if(position==data.size()-1)
        {
            holder.notification.setPadding(0,150,0,200);
        }
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

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
}
