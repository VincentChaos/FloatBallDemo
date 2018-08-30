package com.example.rungame10.floatballdemo.View;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.example.rungame10.floatballdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotifyDialog extends AlertDialog{

    private Context context;
    @BindView(R.id.btn_confirm)
    TextView confirmBtn;
    @BindView(R.id.text)
    TextView contentText;
    private String content;

    public NotifyDialog(Context context, String content){
        super(context, R.style.dialog);
        this.context = context;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_notify,null);
        setContentView(view);
        ButterKnife.bind(this);

        contentText.setText(content);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });


        //设置宽高
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();     //获取屏幕宽高
        lp.width = (int) (d.widthPixels*0.8);
        lp.height = (int) (d.heightPixels*0.3);
        dialogWindow.setAttributes(lp);
    }
}
