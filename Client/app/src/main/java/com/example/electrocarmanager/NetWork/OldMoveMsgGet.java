package com.example.electrocarmanager.NetWork;

import com.example.electrocarmanager.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OldMoveMsgGet implements Runnable{

    final String OLD_MOVING_URL="https://jp.safengine.xyz/move";
    int page;
    String json;

    public OldMoveMsgGet(int page)
    {
        this.page=page;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(OLD_MOVING_URL+"?pageNum="+page);
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
            json=builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getJson()
    {
        return json;
    }
}
