package com.utils;


import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.entity.UserInfo;
import com.google.gson.Gson;
import com.learnhtml.LoginActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by HUANG on 2017/5/6.
 */

public class MyOkHttp {
    UserInfo result;

    //同步
    public UserInfo postHttpUser(final String userName, final String passWord) {

        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                String url = "http://192.168.23.1:1101/learn-html/learnhtml/user/learnhtml!login.action";

                //step 1: 同样的需要创建一个OkHttpClick对象
                OkHttpClient okHttpClient = new OkHttpClient();

                //step 2: 创建  FormBody.Builder
                FormBody formBody = new FormBody.Builder()
                        .add("userName", userName)
                        .add("passWord", passWord)
                        .build();

                //step 3: 创建请求
                Request request = new Request.Builder().url(url)
                        .post(formBody)
                        .build();

                try {

                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        System.out.println("请求成功");
                        ResponseBody s=response.body();
                        System.out.println(s+"返回结果");
                        String responsestr = s.string();
                        System.out.println(responsestr+"<--返回内容");

                        if (responsestr == null || responsestr=="" || responsestr.length()==0 ){

                        } else {
                            result = JSON.parseObject(responsestr, UserInfo.class);
                            System.out.println(result.getUserName() + "<---返回来的用户名-----" + result);
                        }
                    } else {
                        System.out.println("请求失败");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
      });
        thread.start();

        /*public void run() throws Exception {
            RequestBody formBody = new FormEncodingBuilder()
                    .add("search", "Jurassic Park")
                    .build();
            Request request = new Request.Builder()
                    .url("https://en.wikipedia.org/w/index.php")
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        }*/

        return result;
    }

    //异步post
    public UserInfo postAsynHttpUser(String userName, String password) {

        String url = "http://192.168.23.1:1101/learn-html/learnhtml/user/learnhtml!login.action";

        //step 1: 同样的需要创建一个OkHttpClick对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //step 2: 创建  FormBody.Builder
        FormBody formBody = new FormBody.Builder()
                .add("userName", userName)
                .add("passWord", password)
                .build();

        //step 3: 创建请求
        Request request = new Request.Builder().url(url)
                .post(formBody)
                .build();

        //step 4： 建立联系 创建Call对象
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // TODO: 17-5-4  请求失败
                e.printStackTrace();
                System.out.println("请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // TODO: 17-5-4 请求成功
                System.out.println("请求成功");
                ResponseBody s=response.body();
                System.out.println(s+"返回结果");
                String responsestr = s.string();
                System.out.println(responsestr+"<--返回内容");

                if (responsestr == null || responsestr=="" || responsestr.length()==0 ){
                    return;
                }
                result  = JSON.parseObject(responsestr, UserInfo.class);
                System.out.println(result.getUserName()+"<---返回来的用户名-----"+result);

            }
        });

        return result;
    }

    //异步get
    public void getAsynHttp(String userName,String passWord,String url){
        // step 1: 创建 OkHttpClient 对象
        OkHttpClient okHttpClient = new OkHttpClient();

        // step 2： 创建一个请求，不指定请求方法时默认是GET。
        Request.Builder requestBuilder = new Request.Builder().url("http://www.baidu.com");
        //可以省略，默认是GET请求
        requestBuilder.method("GET",null);

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // TODO: 17-5-4  请求失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // TODO: 17-5-4 请求成功
                //获得返回体
                ResponseBody body = response.body();
                System.out.println("963258741"+body.toString());
            }
        });
    }
}
