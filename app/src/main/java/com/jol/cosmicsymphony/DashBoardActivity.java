package com.jol.cosmicsymphony;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jol.cosmicsymphony.databinding.ActivityDashBoardBinding;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class DashBoardActivity extends AppCompatActivity {

    ActivityDashBoardBinding binding;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashBoardBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        binding.infoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DashBoardActivity.this, InformationCenterActivity.class);
                startActivity(intent);

            }
        });
        binding.mapviewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DashBoardActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });


    }
}