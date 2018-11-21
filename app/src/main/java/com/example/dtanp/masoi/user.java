package com.example.dtanp.masoi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class user extends Activity {

    TextView txtuser ,txtlevel,txtid  ,txtloss,txtwin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        txtid= findViewById(R.id.txtid);
        txtuser=findViewById(R.id.txtuser);
        txtlevel=findViewById(R.id.txtlevel);
        txtloss=findViewById(R.id.txtloss);
        txtwin=findViewById(R.id.txtwwin);


    }
}
