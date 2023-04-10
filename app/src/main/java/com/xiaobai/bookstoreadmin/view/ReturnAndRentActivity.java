package com.xiaobai.bookstoreadmin.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaobai.bookstoreadmin.R;
import com.xiaobai.bookstoreadmin.util.ToastNoLooperUtil;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.Timer;
import java.util.TimerTask;

public class ReturnAndRentActivity extends AppCompatActivity {

    private ImageView mCamero;
    private Button mLoginOut;
    private Intent intent;
    private SharedPreferences sp;// 保存数据:键值对

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //继承activity时使用
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //继承AppCompatActivity时使用
        setContentView(R.layout.activity_return_and_rent);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清除自动登录状态
                sp.edit().putBoolean("isLogined", false).commit();
                intent = new Intent(ReturnAndRentActivity.this,LoginMainActivity.class);
                startActivity(intent);
                ToastNoLooperUtil.showToast(ReturnAndRentActivity.this,"退出成功");
                finish();
            }
        });

        mCamero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //申请相机权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale(ReturnAndRentActivity.this,Manifest.permission.CAMERA)){
                    ActivityCompat.requestPermissions(ReturnAndRentActivity.this, new String[]{Manifest.permission.CAMERA},0);
                }
                Intent intent = new Intent(ReturnAndRentActivity.this, CaptureActivity.class);
                startActivityForResult(intent,Constant.REQUEST_IMAGE);
            }
        });
    }

    private void initData() {
        sp = getSharedPreferences("sp_file", MODE_PRIVATE);
    }

    private void initView() {
        mCamero = findViewById(R.id.activity_returnrent_imageButton);
        mLoginOut = findViewById(R.id.activity_returnrent_button);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Intent intent = new Intent(ReturnAndRentActivity.this,CameroActivity.class);
                intent.putExtra("data",content);
                this.startActivity(intent);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(); // 调用双击退出函数
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }
}
