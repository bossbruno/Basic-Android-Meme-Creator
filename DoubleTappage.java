package com.example.doubletap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DoubleTappage extends AppCompatActivity implements TopsectionFragment.Topseclistener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_tappage);
    }

    @Override
    public void creatememe(String Top, String Bottom) {
        botompicfrag botomfrag = (botompicfrag)getSupportFragmentManager().findFragmentById(R.id.fragment4);
        botomfrag.setmeme(Top, Bottom);
    }
}