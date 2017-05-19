package com.learnhtml;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.entity.UserInfo;
import com.utils.ResultData;
import com.utils.ResultSimple;
import com.utils.StaticData;
import com.utils.ValidateUtils;

import java.io.Serializable;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RegisterActivity extends AppCompatActivity {

    Button btnExist;                    //退出登录按钮
    EditText userName;                  //用户名输入框
    EditText pw1;                       //输入密码框
    EditText pw2;                       //确认密码框
    EditText phone;                     //输入手机号码框
    Button btnRegister;                 //注册按钮
    TextView textView_pw2;              //输入密码提示文字，即“请输入密码：”，方便修改资料时的显示与隐藏

    ResultData<UserInfo> resultData;     //服务器端返回注册或者修改的用户的最新资料
    UserInfo mInfo = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //注册成功
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                //传参
                bundle.putSerializable("userInfo", mInfo);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            } else if (msg.what == 2) {
                Toast.makeText(RegisterActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findView();

        Intent intent = getIntent();
        Serializable data = intent.getSerializableExtra("userInfo");
        if (data != null) {
            setTitle("查看个人信息");

            mInfo = (UserInfo) data;
            pw2.setVisibility(View.GONE);   //隐藏密码框1
            textView_pw2.setVisibility(View.GONE);
            btnExist.setVisibility(View.VISIBLE);

            userName.setText(mInfo.getUserName());
            phone.setText(mInfo.getPhone());
            pw2.setText(mInfo.getPassWord());
            btnRegister.setText("修改");
            btnRegister.setBackgroundColor(Color.GREEN);

            //不可编辑
            pw1.setEnabled(false);
            userName.setEnabled(false);
            phone.setEnabled(false);
        }
        setTitle("注册用户");
    }

    private void findView() {
        btnExist = (Button) findViewById(R.id.register_exist);
        btnExist.setVisibility(View.GONE);
        userName = (EditText) findViewById(R.id.register_username);
        pw1 = (EditText) findViewById(R.id.register_password1);
        pw2 = (EditText) findViewById(R.id.register_password2);
        phone = (EditText) findViewById(R.id.register_phone);
        btnRegister = (Button) findViewById(R.id.register_button);
        textView_pw2 = (TextView) findViewById(R.id.register_textview_pw2);

        btnExist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfo = null;
                mHandler.obtainMessage(1).sendToTarget();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("注册".equals(btnRegister.getText())) {
                    insertUser();

                } else if ("修改".equals(btnRegister.getText())) {
                    setTitle("修改个人信息");
                    updateUser();

                } else if ("保存".equals(btnRegister.getText())) {
                    save();
                }
            }
        });
    }

    //点击确定按钮
    private void save() {
        if (validateInfo()) {
            postHttpUser(StaticData.REGISTER_URL_UPDATEUSER);
        }

    }

    //点击修改按钮
    private void updateUser() {
        //不可编辑
        pw2.setEnabled(true);
        phone.setEnabled(true);
        btnExist.setVisibility(View.GONE);
        btnRegister.setText("保存");
        btnRegister.setBackgroundColor(Color.RED);
    }

    //点了注册按钮
    private void insertUser() {
        if (!pw1.getText().toString().equals(pw2.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "两个密码不一致!", Toast.LENGTH_LONG).show();
            return;
        }
        if (validateInfo()) {
            postHttpUser(StaticData.REGISTER_URL_INSERTUSER);
        }
    }

    //验证信息是否符合要求
    private boolean validateInfo() {

        //调用通用类方法，验证用户名是否符合规则,传入字符串，最小长度，最大长度。
        ResultSimple vUserName = ValidateUtils.msIsStrRule(userName.getText().toString().trim(), 6, 16);
        if(!vUserName.isBoolean()){
            mHandler.obtainMessage(2, "用户名"+vUserName.getMessage()).sendToTarget();
            return false;
        }
        //调用通用类方法，验证密码是否符合规则,传入字符串，最小长度，最大长度。
        ResultSimple vPassWord = ValidateUtils.msIsNumberOrLetter(pw2.getText().toString().trim(), 6, 16);
        if(!vPassWord.isBoolean()){
            mHandler.obtainMessage(2, "密码"+vPassWord.getMessage()).sendToTarget();
            return false;
        }

        //调用通用类方法，验证手机号码是否输入正确
        ResultSimple resultSimple = ValidateUtils.msIsOphone(phone.getText().toString().trim());
        //手机号码不符合要求
        if (!resultSimple.isBoolean()) {
            mHandler.obtainMessage(2, resultSimple.getMessage()).sendToTarget();
            return false;
        }
        return true;
    }

    //同步方式去注册
    private void postHttpUser(final String url) {
        resultData = null;
        final String uName = userName.getText().toString();
        final String pwText = pw2.getText().toString();
        final String mPhone = phone.getText().toString();

        Thread thread = new Thread(new Runnable() {

            //step 1: 同样的需要创建一个OkHttpClick对象
            OkHttpClient okHttpClient = new OkHttpClient();

            //step 2: 创建  FormBody.Builder
            FormBody formBody = new FormBody.Builder()
                    .add("userName", uName)
                    .add("passWord", pwText)
                    .add("phone", mPhone)
                    .build();

            //step 3: 创建请求
            Request request = new Request.Builder().url(url)
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
                        System.out.println(responsestr + "<--返回内容");
                        resultData = JSON.parseObject(responsestr, new TypeReference<ResultData<UserInfo>>() {
                        });

                        //请求成功
                        if (resultData.isSuccess()) {
                            mInfo = resultData.getData();
                            mHandler.obtainMessage(1).sendToTarget();

                        } else {//否则显示失败原因
                            mHandler.obtainMessage(2, resultData.getMessage()).sendToTarget();
                        }

                    } else {
                        mHandler.obtainMessage(2, "服务器内部错误!").sendToTarget();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(2, "请求服务器失败，请联系管理员！").sendToTarget();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onBackPressed() {
        if(pw2.getVisibility()!=View.GONE || pw2.getVisibility()!=View.INVISIBLE) {
            exitDialog("离开此页面会丢失您所填写的内容。","舍弃","再想想");
        }
    }

    //提示是否退出
    private void exitDialog(String message,String positiveBtnText,String negativeBtnText) {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("userInfo", mInfo);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

}