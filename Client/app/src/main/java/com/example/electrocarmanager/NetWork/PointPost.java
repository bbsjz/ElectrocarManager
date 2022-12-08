package com.example.electrocarmanager.NetWork;

import com.example.electrocarmanager.Fragment.PointFragment;
import com.example.electrocarmanager.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PointPost implements Runnable{

    final String OLD_TRACK_POINT ="https://jp.safengine.xyz/point";
    Long id;
    String result;

    public PointPost(Long id)
    {
        this.id=id;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(OLD_TRACK_POINT+"/"+id);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization","Bearer  "+ MainActivity.token);
            InputStream in=httpURLConnection.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder builder=new StringBuilder();
            String oneLine;
            while((oneLine=reader.readLine())!=null)
            {
                builder.append(oneLine);
            }
            result=builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResult()
    {
        return result;
    }
}
