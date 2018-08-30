package com.example.rungame10.floatballdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.rungame10.floatballdemo.Presenter.FloatActionController;
import com.example.rungame10.floatballdemo.Presenter.FloatPermissionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn1)
    Button button1;

    @BindView(R.id.btn2)
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        FloatPermissionManager.getInstance().applyFloatWindow(MainActivity.this);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatActionController.floatFlag = true;
                FloatActionController.getInstance().startServer(MainActivity.this);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatActionController.getInstance().stopServer(MainActivity.this);
                FloatActionController.floatFlag = false;
            }
        });

    }
}
