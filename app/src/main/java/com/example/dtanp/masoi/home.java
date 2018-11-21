package com.example.dtanp.masoi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class home extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.btnchonban).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startmhroom();
                finish();
            }
        });
    }
    public void startmhroom()
    {
        Intent intent = new Intent(this,room.class);
        startActivity(intent);
    }
}
