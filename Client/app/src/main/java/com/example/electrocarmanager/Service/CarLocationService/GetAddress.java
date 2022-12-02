package com.example.electrocarmanager.Service.CarLocationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetAddress implements Runnable{
    String address="";
    double lat,log;
    final String LAT_LOG_TO_ADDRESS="https://api.map.baidu.com/reverse_geocoding/v3/?" +
            "ak=8x9xaeuwUVt82E5CddhjDMHrYo2932fT&output=json&" +
            "coordtype=bd09ll&extensions_poi=1&location=";

    public GetAddress(double lat, double log)
    {
        this.lat=lat;
        this.log=log;
    }

    public void run()
    {
        URL url = null;
        try {
            url = new URL(LAT_LOG_TO_ADDRESS+lat+","+log);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            InputStream in=httpURLConnection.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder builder=new StringBuilder();
            String oneLine;
            while((oneLine=reader.readLine())!=null)
            {
                builder.append(oneLine);
            }
            String json=builder.toString();
            JSONObject jsonObject=new JSONObject(json);
            if(jsonObject.optInt("status")!=0)
            {
                address="地址解析失败";
                return;
            }
            JSONObject result=jsonObject.getJSONObject("result");
            address=result.optString("formatted_address")+jsonObject.optString("sematic_description");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAddress()
    {
        return address;
    }
}