package com.example.electrocarmanager.Notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electrocarmanager.Entity.NotifyGroup;
import com.example.electrocarmanager.R;
import com.example.electrocarmanager.Utils.PxUtils;

import java.util.List;

public class NotificationGroupRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    NotificationRecyclerViewAdapter.ItemClickListener itemClickListener;
    List<NotifyGroup> data;
    LayoutInflater inflater;
    Context context;
    int ifLoading=0;//0表示不显示，1表示正在加载，2表示没有更多数据

    private int normalType = 0;     // 第一种ViewType，正常的item
    private int footType = 1;       // 第二种ViewType，底部的提示View

    public NotificationGroupRecycleAdapter(List<NotifyGroup> data, NotificationRecyclerViewAdapter.ItemClickListener itemClickListener, Context context)
    {
        this.data=data;
        this.itemClickListener = itemClickListener;
        inflater=LayoutInflater.from(context);
        this.context=context;
    }

    public void setIfLoading(int ifLoading)
    {
        this.ifLoading=ifLoading;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType==normalType)
        {
            View view=inflater.inflate(R.layout.list_group,parent,false);
            return new NotificationGroupHolder(view);
        }
        else
        {
            View view=inflater.inflate(R.layout.foot,parent,false);
            return new FootHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,int position)
    {
        if(holder instanceof NotificationGroupHolder)
        {
            NotifyGroup notificationGroup=data.get(position);
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
            NotificationRecyclerViewAdapter  adapter=
                    new NotificationRecyclerViewAdapter(notificationGroup.notifications,context);
            adapter.setClickListener(itemClickListener);
            ((NotificationGroupHolder) holder).notification.setLayoutManager(linearLayoutManager);
            ((NotificationGroupHolder) holder).notification.setAdapter(adapter);
            ((NotificationGroupHolder) holder).date.setText(notificationGroup.date);
//            if(position==data.size()-1)
//            {
//                ((NotificationGroupHolder) holder).notification.setPadding(PxUtils.dp2px(context,10),PxUtils.dp2px(context,50),PxUtils.dp2px(context,10),PxUtils.dp2px(context,80));
//            }
        }
        else if(holder instanceof FootHolder)
        {
            if(ifLoading==1)
            {
                ((FootHolder) holder).foot.setText("正在加载中");
                ((FootHolder) holder).foot.setVisibility(View.VISIBLE);
            }
            else if(ifLoading==2)
            {
                ((FootHolder) holder).foot.setText("没有更多数据");
                ((FootHolder) holder).foot.setVisibility(View.VISIBLE);
            }
            else if(ifLoading==0)
            {
                ((FootHolder) holder).foot.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return data.size()+1;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position==getItemCount()-1)
        {
            return footType;
        }
        else
        {
            return normalType;
        }
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

    public class FootHolder extends RecyclerView.ViewHolder{
        TextView foot;

        public FootHolder(View view)
        {
            super(view);
            foot=view.findViewById(R.id.foot);
        }
    }

    public void setData(List<NotifyGroup> data)
    {
        this.data=data;
    }

    public void updateState()
    {
        notifyDataSetChanged();
    }
}
