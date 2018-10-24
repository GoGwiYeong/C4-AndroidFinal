package com.example.ljh.mymqtt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button check;
    EditText wifi;
    String Id="root";
    String Pwd="root";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final EditText eid = (EditText) findViewById(R.id.userid);
        final EditText epw = (EditText)findViewById(R.id.userpassword);
        wifi = (EditText)findViewById(R.id.wifi);
        check = (Button)findViewById(R.id.check);
        check.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(eid.getText().toString().equals(Id) && epw.getText().toString().equals(Pwd)) {
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    intent.putExtra("wifi", wifi.getText().toString());
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"일치하지않음",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}