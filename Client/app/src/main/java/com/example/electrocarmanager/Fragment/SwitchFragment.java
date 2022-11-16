package com.example.electrocarmanager.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;

import com.example.electrocarmanager.R;

/**
 * @author bbg
 * 用于开锁关锁的fragment
 */
public class SwitchFragment extends Fragment {
    ImageView imageView;
    boolean ifOn;
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
        imageView=view.findViewById(R.id.Switch);
        ifOn=false;
        imageView.setOnClickListener(v->{
            if(!ifOn)
            {
                imageView.setImageResource(R.drawable.on);

                //TODO:硬件开锁代码

                Toast.makeText(getContext(),"已开锁",Toast.LENGTH_SHORT);
                ifOn=true;

            }
            else
            {
                imageView.setImageResource(R.drawable.off);

                //TODO:硬件关锁代码

                Toast.makeText(getContext(),"已关锁",Toast.LENGTH_SHORT);
                ifOn=false;
            }
        });
    }
}
