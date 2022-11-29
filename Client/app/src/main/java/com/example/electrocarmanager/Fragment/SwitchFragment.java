package com.example.electrocarmanager.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;

import com.example.electrocarmanager.MainActivity;
import com.example.electrocarmanager.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author bbg
 * 用于开锁关锁的fragment
 */
public class SwitchFragment extends Fragment {

    final String OPERATE_LOCK_ADDRESS="http://192.168.31.23:8080/lock";

    Handler handler;

    ImageView unLock;
    ImageView lock;
    //0表示关锁状态，1表示开锁状态，2表示启动状态
    int ifOpen=0;

    public SwitchFragment(Handler handler)
    {
        this.handler=handler;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedBundle)
    {
        super.onCreateView(layoutInflater,viewGroup,savedBundle);
        return layoutInflater.inflate(R.layout.switch_fragment,viewGroup,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedBundle)
    {
        super.onViewCreated(view,savedBundle);
        initUI(view);
    }

    void initUI(View view)
    {
        unLock=view.findViewById(R.id.un_lock);
        lock =view.findViewById(R.id.lock);
        if(ifOpen==0)
        {
            unLock.setImageResource(R.drawable.on);
        }
        else if(ifOpen==1)
        {
            unLock.setImageResource(R.drawable.start);
        }
        else if(ifOpen==2)
        {
            unLock.setImageResource(R.drawable.real_start);
        }

        unLock.setOnClickListener(v->{
            if(ifOpen==0)
            {
                unLock.setImageResource(R.drawable.start);
                sendCommand(2);
                Toast.makeText(getContext(),"已开锁",Toast.LENGTH_SHORT).show();
                ifOpen=1;

                //每次开锁都会自动关闭位移提醒
                if(MainActivity.realAlertOn)
                {
                    MainActivity.realAlertOn=false;
                    Message msg=new Message();
                    msg.what=4;
                    msg.obj="CLOSE_MOVING_ALERT";
                    handler.sendMessage(msg);
                }
            }
            else if(ifOpen==1)
            {
                unLock.setImageResource(R.drawable.real_start);
                sendCommand(3);
                Toast.makeText(getContext(),"已启动",Toast.LENGTH_SHORT).show();
                ifOpen=2;
            }
        });
        lock.setOnClickListener(v->{
            sendCommand(1);
            unLock.setImageResource(R.drawable.on);
            ifOpen=0;
            Toast.makeText(getContext(),"已关锁",Toast.LENGTH_SHORT).show();

            //每一次关锁都会自动打开位移提醒
            if(!MainActivity.realAlertOn)
            {
                MainActivity.realAlertOn=true;
                Message msg=new Message();
                msg.what=4;
                msg.obj="OPEN_MOVING_ALERT";
                handler.sendMessage(msg);
            }
        });
    }

    void sendCommand(int command)
    {
        new Thread(){
            @Override
            public void run()
            {
                try {
                    URL url = new URL(OPERATE_LOCK_ADDRESS+"?id="+command);
                    HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    InputStream in=httpURLConnection.getInputStream();
//                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
//                    StringBuilder builder=new StringBuilder();
//                    String oneLine;
//                    while((oneLine=reader.readLine())!=null)
//                    {
//                        builder.append(oneLine);
//                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
