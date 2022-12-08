package com.example.electrocarmanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electrocarmanager.Service.LoginService.LoginService;

public class LoginActivity extends AppCompatActivity {
    EditText account;
    EditText password;
    TextView log;
    TextView register;
    TextView change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initUI();
        initUser();
    }

    @Override
    protected void onDestroy()
    {
        SharedPreferences preferences=getSharedPreferences("last_user",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("user",account.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.commit();
        super.onDestroy();
    }

    private void initUI()
    {
        account=findViewById(R.id.account);
        password=findViewById(R.id.password);
        log=findViewById(R.id.log);
        register=findViewById(R.id.register);
        change=findViewById(R.id.change);

        log.setOnClickListener(View->{
            try {
                if(LoginService.login(account.getText().toString(),password.getText().toString()))//用户名不存在
                {
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("token",LoginService.getToken());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    ((Activity)LoginActivity.this).overridePendingTransition(R.anim.in,
                            R.anim.out);
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void initUser()
    {
        SharedPreferences preferences=getSharedPreferences("last_user",MODE_PRIVATE);
        String user=preferences.getString("user","");
        String pwd=preferences.getString("password","");
        account.setText(user);
        password.setText(pwd);
    }

}
