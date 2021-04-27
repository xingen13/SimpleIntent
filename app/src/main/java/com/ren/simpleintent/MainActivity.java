package com.ren.simpleintent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ren.simpleintent.annotion.SimpleIntent;

@SimpleIntent(paramTypes = int.class,flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}