package com.example.electrocarmanager.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electrocarmanager.Notification.Notification;
import com.example.electrocarmanager.Notification.NotificationGroup;
import com.example.electrocarmanager.Notification.NotificationGroupRecycleAdapter;
import com.example.electrocarmanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bbg
 * 在开启位移提醒时，用于显示移动结果的fragment
 */
public class TrackFragment extends Fragment {
    List<NotificationGroup> data;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             @Nullable ViewGroup viewGroup, @Nullable Bundle savedBundle)
    {
        super.onCreateView(layoutInflater,viewGroup,savedBundle);
        return layoutInflater.inflate(R.layout.track_fragment,viewGroup,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedBundle)
    {
        test();
        recyclerView=view.findViewById(R.id.notifications);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        NotificationGroupRecycleAdapter adapter=new NotificationGroupRecycleAdapter(data,getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    void test()
    {
        data=new ArrayList<>();
        Notification notification1=new Notification("16:22","武汉大学","华中科技大学",1001,1200);

        Notification notification2=new Notification("16:27","街道口","广埠屯",101,66);

        NotificationGroup notificationGroup=new NotificationGroup();
        notificationGroup.notifications.add(notification1);
        notificationGroup.notifications.add(notification2);
        notificationGroup.date="11月6日";

        data.add(notificationGroup);

        NotificationGroup notificationGroup1=new NotificationGroup();
        notificationGroup1.notifications.add(notification1);
        notificationGroup1.notifications.add(notification2);
        notificationGroup1.date="11月7日";

        data.add(notificationGroup1);

    }
}
