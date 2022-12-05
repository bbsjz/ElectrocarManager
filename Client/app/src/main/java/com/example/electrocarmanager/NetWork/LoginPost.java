package com.example.electrocarmanager.NetWork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginPost implements Runnable{

    final String LOGIN_URL="http://jp.safengine.xyz:8080/authentication/login";
    String userName;
    String pwd;
    String ex;
    String result;
    int code;//状态码

    public LoginPost(String userName,String pwd)
    {
        this.userName=userName;
        this.pwd=pwd;
    }

    @Override
    public void run() {
        try
        {
            URL url = new URL(LOGIN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            String json="{\"name\":\""+userName+"\",\"password\":\""+pwd+"\"}";
            // 往服务器里面发送数据
            byte[] data = json.getBytes(StandardCharsets.UTF_8);
            OutputStream os = conn.getOutputStream();
            os.write(data, 0, data.length);
            os.close();
            InputStream in= conn.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder response=new StringBuilder();
            String oneLine;
            while((oneLine=reader.readLine())!=null)
            {
                response.append(oneLine);
            }
            result=response.toString();
            code=conn.getResponseCode();
        } catch (ProtocolException e) {
            ex=e.getMessage();
        } catch (MalformedURLException e) {
            ex=e.getMessage();
        } catch (IOException e) {
            ex=e.getMessage();
        }
    }

    public int getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return result;
    }
}
