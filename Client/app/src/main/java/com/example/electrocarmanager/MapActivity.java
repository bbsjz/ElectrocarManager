package com.example.electrocarmanager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;


public class MapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WalkNavigateHelper navigateHelper=WalkNavigateHelper.getInstance();
        setContentView(R.layout.activity_main);
        View view = navigateHelper.onCreate(this);
        if (view != null) {
            setContentView(view);
        }
        navigateHelper.startWalkNavi(this);
    }
}
