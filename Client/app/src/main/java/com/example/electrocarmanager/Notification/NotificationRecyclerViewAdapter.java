package com.example.electrocarmanager.Notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electrocarmanager.Entity.Notify;
import com.example.electrocarmanager.R;

import java.util.List;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationHolder> {
    List<Notify> data;
    LayoutInflater inflater;
    ItemClickListener clickListener;

    public NotificationRecyclerViewAdapter(List<Notify> data, Context context)
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
        Notify notification=data.get(position);
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

    Notify getItem(int id)
    {
        return data.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class NotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
            {
                if(time.getText().toString().contains("-"))
                {
                    clickListener.onItemClick(getItem(getAdapterPosition()),false);
                }
                else
                {
                    clickListener.onItemClick(getItem(getAdapterPosition()),true);
                }
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(Notify notification,boolean ifReal);
    }
}
