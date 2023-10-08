package com.jol.cosmicsymphony;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class InformationCenterActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_center);
    }
}