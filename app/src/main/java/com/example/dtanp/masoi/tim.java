package com.example.dtanp.masoi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageButton;

public class tim  extends Activity{
    ImageButton imguser1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim);
        imguser1=findViewById(R.id.bguser1);
    }
}
