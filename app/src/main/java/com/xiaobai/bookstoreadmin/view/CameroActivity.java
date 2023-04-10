package com.xiaobai.bookstoreadmin.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.xiaobai.bookstoreadmin.R;
import com.xiaobai.bookstoreadmin.service.SendMessageToSql;
import com.xiaobai.bookstoreadmin.util.ResponseUtil;
import com.xiaobai.bookstoreadmin.util.ToastUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;

public class CameroActivity extends AppCompatActivity {
    private SendMessageToSql sendMessageToSql = new SendMessageToSql();
    private SharedPreferences sp;// 保存数据:键值对
    private Thread threadSendAesTextToOra;
    private String response;
    private boolean stopThreadSendAes;
    private String aesTextFromIntent;

    private Runnable runSendAesText = new Runnable() {
        @Override
        public void run() {
            stopThreadSendAes = false;
            while(!stopThreadSendAes){
                HttpURLConnection connBoolRentOrReturn = null;
                InputStream is = null;
                try {
                    String name = sp.getString("name", "");
                    String aesPassword = sp.getString("passwd", "");
                    connBoolRentOrReturn = sendMessageToSql.sendAesText(name,aesPassword,aesTextFromIntent);
                    //判断有没有连接服务器
                    if (connBoolRentOrReturn == null) {
                        ToastUtil.showToast(CameroActivity.this, "连接服务器失败，请检查网络连接状况");
                    } else {
                        is = connBoolRentOrReturn.getInputStream();
                        response = ResponseUtil.getResponse(is);
                        //false无租借，true有租借
                        if("true".equals(response)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    successDialog();
                                }
                            });
                        }else if("false".equals(response)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    falseDialog();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageDialog(response);
                                }
                            });
                        }
                    }
                    stopThreadSendAes = true;
                    Thread.sleep(60000);
                }catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connBoolRentOrReturn != null) {
                        connBoolRentOrReturn.disconnect();
                    }
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    } ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camero);
        initView();
        initData();
    }

    private void initData() {
        sp = getSharedPreferences("sp_file", MODE_PRIVATE);
        Intent intent = getIntent();
        aesTextFromIntent=intent.getStringExtra("data");
        threadSendAesTextToOra = new Thread(runSendAesText);
        threadSendAesTextToOra.start();
    }


    private void initView() {
    }

    /**
     * 成功归还/租借时弹出的提示框
     */
    private void successDialog() {
        AlertDialog dialog = new AlertDialog.Builder(CameroActivity.this).create();//创建对话框
        dialog.setTitle("提示");//设置对话框标题
        dialog.setMessage("操作成功！");//设置文字显示内容
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CameroActivity.this, ReturnAndRentActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();//显示对话框
    }

    /**
     * 失败时弹出的提示框
     */
    private void falseDialog() {
        AlertDialog dialog = new AlertDialog.Builder(CameroActivity.this).create();//创建对话框
        dialog.setTitle("提示");//设置对话框标题
        dialog.setMessage("操作失败！");//设置文字显示内容
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CameroActivity.this, ReturnAndRentActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();//显示对话框
    }

    /**
     * 信息的提示框
     */
    private void messageDialog(String s) {
        AlertDialog dialog = new AlertDialog.Builder(CameroActivity.this).create();//创建对话框
        dialog.setTitle("提示");//设置对话框标题
        dialog.setMessage(s);//设置文字显示内容
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CameroActivity.this, ReturnAndRentActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();//显示对话框
    }

    @Override
    protected void onDestroy() {
        stopThreadSendAes = true;
        super.onDestroy();
    }
}
