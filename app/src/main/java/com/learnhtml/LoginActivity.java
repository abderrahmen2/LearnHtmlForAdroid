package com.learnhtml;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.entity.UserInfo;
import com.entity.WordsInfo;
import com.utils.MyOkHttp;
import com.utils.ResultData;
import com.utils.ResultSimple;
import com.utils.ResultWordsData;
import com.utils.StaticData;
import com.utils.ValidateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends AppCompatActivity {

    private EditText userName;          //用户名输入框
    private EditText passWord;          //密码输入框
    private Button btnRegister;         //去注册按钮
    private Button btnLogin;            //登录按钮
    private Button forgetPassWord;      //忘记密码按钮

    UserInfo mInfo;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                loginSuccess();

            } else if (msg.what == 2) {
                Toast.makeText(LoginActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        setTitle(getString(R.string.loginactivity_tips_title2));
        findView();
        setListener();

        Intent intent = getIntent();
        Serializable data = intent.getSerializableExtra("userInfo");
        if (data != null) {
            setTitle(getString(R.string.loginactivity_tips_title));
            mInfo = (UserInfo) data;
        }
    }

    private void findView() {
        btnRegister = (Button) findViewById(R.id.login1_btn_register);
        btnLogin = (Button) findViewById(R.id.login1_button);
        userName = (EditText) findViewById(R.id.login1_username);
        passWord = (EditText) findViewById(R.id.login1_password1);
        forgetPassWord=(Button)findViewById(R.id.login1_forget_password);


    }
    //设置监听
    private void setListener(){

        forgetPassWord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateInfo()) {
                    String uText = userName.getText().toString().trim();
                    String pwText = passWord.getText().toString().trim();
                    postHttpUser(uText, pwText);
                }
            }
        });

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到注册页面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", mInfo);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }


    //同步方式去登录
    public void postHttpUser(final String userName, final String passWord) {

        Thread thread = new Thread(new Runnable() {

            //step 1: 同样的需要创建一个OkHttpClick对象
            OkHttpClient okHttpClient = new OkHttpClient();

            //step 2: 创建  FormBody.Builder
            FormBody formBody = new FormBody.Builder()
                    .add("userName", userName)
                    .add("passWord", passWord)
                    .build();

            //step 3: 创建请求
            Request request = new Request.Builder().url(StaticData.LOGIN_URL_LOGIN)
                    .post(formBody)
                    .build();

            @Override
            public void run() {
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        System.out.println("请求成功");
                        ResponseBody s = response.body();
                        String responsestr = s.string();

                        //对服务器返回的List进行JSON
                        ResultData<UserInfo> resultData = JSON.parseObject(responsestr, new TypeReference<ResultData<UserInfo>>() {
                        });
                        //登录成功
                        if (resultData.isSuccess()) {
                            mInfo = resultData.getData();
                            System.out.println(mInfo.getUserName() + "<---返回来的用户名-----" + mInfo);
                            mHandler.obtainMessage(1).sendToTarget();
                        }
                        //服务器异常或者登录失败
                        else {
                            mHandler.obtainMessage(2, resultData.getMessage()).sendToTarget();
                        }

                    } else {
                        mHandler.obtainMessage(2, getString(R.string.loginactivity_tips_dataecxcetion)).sendToTarget();
                        System.out.println("数据解析出现异常！");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(2, getString(R.string.loginactivity_tips_goserviceexcetion)).sendToTarget();
                }
            }
        });
        thread.start();
    }

    //验证信息是否符合要求
    private boolean validateInfo() {

        //调用通用类方法，验证用户名是否符合规则,传入字符串，最小长度，最大长度。
        ResultSimple vUserName = ValidateUtils.msIsStrRule(userName.getText().toString().trim(), 6, 16);
        if (!vUserName.isBoolean()) {
            mHandler.obtainMessage(2, getString(R.string.loginactivity_tips_validatename) + vUserName.getMessage()).sendToTarget();
            return false;
        }
        //调用通用类方法，验证密码是否符合规则,传入字符串，最小长度，最大长度。
        ResultSimple vPassWord = ValidateUtils.msIsNumberOrLetter(passWord.getText().toString().trim(), 6, 16);
        if (!vPassWord.isBoolean()) {
            mHandler.obtainMessage(2, getString(R.string.loginactivity_tips_password) + vPassWord.getMessage()).sendToTarget();
            return false;
        }
        return true;
    }

    //登录成功后跳转页面
    public void loginSuccess() {
        //登录成功
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        //传参
        bundle.putSerializable("userInfo", mInfo);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        exitDialog();

    }

    //提示是否退出
    private void exitDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.loginactivity_dialog_tips)
                .setMessage(R.string.loginactivity_dialog_tipsgo)
                .setPositiveButton(R.string.loginactivity_dialog_gonow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        //传参
                        bundle.putSerializable("userInfo", mInfo);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.loginactivity_dialog_goletter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }
}

