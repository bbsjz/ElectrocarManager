package com.example.electrocarmanager.Service.LoginService;

import com.example.electrocarmanager.NetWork.LoginPost;

public class LoginService {

    static String token;

    /**
     * 输入用户名和密码进行登录
     * @param userName 用户名
     * @param pwd 密码
     */
    public static boolean login (String userName,String pwd) throws InterruptedException {
        LoginPost post=new LoginPost(userName,pwd);
        Thread login=new Thread(post);
        login.start();
        login.join();
        if(post.getCode()==200)
        {
            token=post.getInfo();
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String getToken()
    {
        return token;
    }
}
