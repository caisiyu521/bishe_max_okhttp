package com.example.caisiyu.bishe_max;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    private Button button1;
    private TextView textView_username;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        button1 = (Button)findViewById(R.id.login);
        textView_username = (TextView)findViewById(R.id.username);


//
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity_Two.class);
                username = textView_username.getText().toString();
                intent.putExtra("User", username);
                startActivity(intent);
                finish();
            }
        });
    }
}
