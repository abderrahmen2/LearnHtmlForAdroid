package com.learnhtml;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.entity.ContentInfo;
import com.entity.UserInfo;
import com.entity.WordsInfo;
import com.utils.ResultListData;
import com.utils.ResultSimple;
import com.utils.StaticData;
import com.utils.ValidateUtils;

import java.io.Serializable;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView = null;
    private DrawerLayout drawer = null;
    private Toolbar toolbar = null;

    View headerLayout = null;                       //侧滑界面标题布局
    private TextView nav_text_username = null;      //显示用户名
    private ImageView nav_tes = null;               //显示头像
    private TextView nav_text = null;               //显示性别
    private UserInfo mInfo = null;                  //个人信息

    //content_main
    private TextView content_title;
    private TextView content_text1;
    private TextView content_text2;
    private TextView content_text3;
    private ImageView content_image1;
    private ImageView content_image2;
    private TextView content_html;
    private TextView content_remark;
    private List<ContentInfo> contentList;      //服务器返回的知识list
    private String rowID = "0";
    private ScrollView content_scrollview;

    //留言
    private Button liuyan_btn_submit;
    private Button liuyan_btn_all;
    private EditText liuyan_editext_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("HTML5学习基地");

        findView();
        nav_loginView();
        setListenter();


        //其他Activity传来数据进行处理
        Intent intent = getIntent();
        Serializable data = intent.getSerializableExtra("userInfo");
        if (data != null) {
            mInfo = (UserInfo) data;
            nav_text_username.setText("用户名:" + mInfo.getUserName());
        }
        //设置初始化界面
        setContentViewToNull();
        findContentFromService(StaticData.HTML_JIANJIE_CODE);

    }

    private void nav_loginView() {
        //添加侧滑页面页头并进行监听
        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        nav_tes = (ImageView) headerLayout.findViewById(R.id.nav_header_btn_img);
        nav_tes.setVisibility(View.INVISIBLE);
        nav_text = (TextView) headerLayout.findViewById(R.id.nav_header_text1);
        nav_text.setVisibility(View.INVISIBLE);
        nav_text_username = (TextView) headerLayout.findViewById(R.id.nav_header_username);
        nav_text_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUserInfoOrLogin();
            }
        });
        nav_tes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUserInfoOrLogin();
            }
        });
    }

    private void findView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        content_remark = (TextView) findViewById(R.id.content_remark);
        content_text1 = (TextView) findViewById(R.id.content_text1);
        content_text2 = (TextView) findViewById(R.id.content_text2);
        content_text3 = (TextView) findViewById(R.id.content_text3);
        content_title = (TextView) findViewById(R.id.content_title);
        content_image1 = (ImageView) findViewById(R.id.content_image1);
        content_image2 = (ImageView) findViewById(R.id.content_image2);
        content_html = (TextView) findViewById(R.id.content_html);
        content_scrollview = (ScrollView) findViewById(R.id.content_scrollview);

        liuyan_btn_submit = (Button) findViewById(R.id.content_liuyan_submit);
        liuyan_btn_all = (Button) findViewById(R.id.content_liuyan_all);
        liuyan_editext_text = (EditText) findViewById(R.id.content_liuyan_text);

    }

    private void setListenter() {

        //查看全部留言按钮监听
        liuyan_btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInfo == null) {//处于未登录状态
                    mhandler.obtainMessage(0, "请先登录").sendToTarget();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, WordsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", mInfo);
                bundle.putSerializable("rowid", rowID);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        //发表留言按钮监听
        liuyan_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInfo == null) {//处于未登录状态
                    mhandler.obtainMessage(0, "请先登录").sendToTarget();
                    return;
                }
                String mtext = liuyan_editext_text.getText().toString();

                //调用方法对内容进行验证
                ResultSimple resultSimple= ValidateUtils.msIsLengthRule(mtext,2,2000);
                if(!resultSimple.isBoolean()){
                    mhandler.obtainMessage(0, resultSimple.getMessage()).sendToTarget();
                    return;
                }

                liuyan_btn_submit.setVisibility(View.GONE);
                liuyan_btn_all.setVisibility(View.VISIBLE);
                System.out.println(mtext + "<----留言内容");
                findContentToService(mtext);
            }
        });

        //留言框发生触摸事件
        liuyan_editext_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                liuyan_btn_all.setVisibility(View.GONE);
                liuyan_btn_submit.setVisibility(View.VISIBLE);
                return false;
            }
        });

        //滚动框发生触摸事件
        content_scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                liuyan_btn_submit.setVisibility(View.GONE);
                liuyan_btn_all.setVisibility(View.VISIBLE);
                scrollViewGetFocusable();
                return false;
            }
        });

    }

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //显示服务器返回的知识内容
            if (msg.what == 1) {
                content_title.setText(contentList.get(0).getContent());//标题
                content_text1.setText(contentList.get(1).getContent());//内容1

                //list中有5个内容,表示只有一个标题,一个内容1，一个内容2
                if (contentList.size() == 3) {
                    content_text2.setText(contentList.get(2).getContent());
                }
                //list中有4个内容,表示只有一个标题,一个内容1，一个代码块，一个内容2
                else if (contentList.size() == 4) {
                    content_html.setText(contentList.get(2).getContent());
                    content_text2.setText(contentList.get(3).getContent());
                }
                //list中有5个内容,表示只有一个标题,一个内容1，一个代码块，一个内容2，一个内容3
                else if (contentList.size() == 5) {
                    content_html.setText(contentList.get(2).getContent());
                    content_text2.setText(contentList.get(3).getContent());
                    content_text3.setText(contentList.get(4).getContent());
                }
            }
            //提示
            else if (msg.what == 0) {
                Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
            //留言成功后
            else if (msg.what == 2) {
                liuyan_editext_text.setText("");
                scrollViewGetFocusable();
                checkDialog(StaticData.MAINACTIVITY_WORDS_GO, "留言成功啦，去看看大家都在说什么吧。", "马上去", "稍后再去");
            }
        }
    };

    //加载ActionBar菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.html, menu);
        return true;
    }

    //ActionBar点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem itm) {
        int id = itm.getItemId();
        switch (id) {

            case R.id.menu_light_n:
                String str = itm.getTitle().toString();
                if ("夜间模式".equals(str)) {
                    navigationView.setBackgroundColor(getResources().getColor(R.color.nav_back_color_nig));
                    drawer.setBackgroundColor(getResources().getColor(R.color.MainActivityBackColor_nig));
                    itm.setTitle("日间模式");
                } else if ("日间模式".equals(str)) {
                    drawer.setBackgroundColor(Color.WHITE);
                    navigationView.setBackgroundColor(Color.WHITE);
                    itm.setTitle("夜间模式");
                }
                break;
            case R.id.menu_store:
                break;

            case R.id.menu_about:
                break;
        }


        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String title = item.getTitle().toString();
        setContentViewToNull();

        switch (title) {
            case "HTML简介":
                findContentFromService(StaticData.HTML_JIANJIE_CODE);
                break;

            case "HTML编辑器":
                content_image1.setImageResource(R.mipmap.content_bianjiqi_img2);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML_BIANJIQI_CODE);
                break;

            case "HTML元素":
                findContentFromService(StaticData.HTML_YUANSU_CODE);
                break;

            case "HTML标题":
                findContentFromService(StaticData.HTML_BIAOTI_CODE);
                content_image1.setImageResource(R.mipmap.content_biaoti_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                break;
            case "HTML链接":
                findContentFromService(StaticData.HTML_LIANJIE_CODE);
                break;
            case "HTML头部":
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML_TOUBU_CODE);
                break;
            case "HTML表格":
                content_image1.setImageResource(R.mipmap.content_biaoge_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML_BIAOGE_CODE);
                break;
            case "HTML图片":
                content_image1.setImageResource(R.mipmap.content_tupian_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML_TUPIAN_CODE);
                break;
            case "HTML区块":
                content_image1.setImageResource(R.mipmap.content_qukuai_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML_QUKUAI_CODE);
                break;
            case "HTML布局":
                content_image1.setImageResource(R.mipmap.content_buju_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML_BUJU_CODE);
                break;
            case "HTML表单":
                content_image1.setImageResource(R.mipmap.content_biaodan_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML_BIAODAN_CODE);
                break;
            case "HTML框架":
                content_image1.setImageResource(R.mipmap.content_kuangjia_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML_KUANGJIA_CODE);
                break;
            case "HTML总结":
                findContentFromService(StaticData.HTML_ZONGJIE_CODE);
                break;

            //HTML5模块
            case "HTML5教程":
                findContentFromService(StaticData.HTML5_JIAOCHENG_CODE);
                break;
            case "浏览器支持":
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_LIULANQI_CODE);
                break;
            case "Canvas":
                content_image1.setImageResource(R.mipmap.content_canvas_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_CANVAS_CODE);
                break;
            case "内联SVG":
                content_image1.setImageResource(R.mipmap.content_neiliansvg_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_NEILIANSVG_CODE);
                break;
            case "MathML":
                content_image1.setImageResource(R.mipmap.content_mathml_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                content_image2.setImageResource(R.mipmap.content_mathml_img2);
                content_image2.setVisibility(View.VISIBLE);
                content_text3.setTextSize(13);
                findContentFromService(StaticData.HTML5_MATHML_CODE);
                break;
            case "拖放":
                content_image1.setImageResource(R.mipmap.content_tuofang_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_TUOFANG_CODE);
                break;
            case "地理定位":
                content_image1.setImageResource(R.mipmap.content_dilidingwei_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_image2.setImageResource(R.mipmap.content_dilidingwei_img2);
                content_image2.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_DILIDINGWEI_CODE);
                break;
            case "video":
                content_image1.setImageResource(R.mipmap.content_video_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_VIDEO_CODE);
                break;
            case "audio":
                content_image1.setImageResource(R.mipmap.content_audio_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_AUDIO_CODE);
                break;
            case "input类型":
                findContentFromService(StaticData.HTML5_INPUTLEIXING_CODE);
                break;
            case "表单元素":
                content_image1.setImageResource(R.mipmap.content_biaodanyuansu_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_BIAODANYUANSU_CODE);
                break;
            case "表单属性":
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_BIAODANSHUXING_CODE);
                break;
            case "web存储":
                content_image2.setImageResource(R.mipmap.content_webcunchu_img1);
                content_image2.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                content_text3.setTextSize(13);
                findContentFromService(StaticData.HTML5_WEBCUNCHU_CODE);
                break;
            case "WebSql":
                content_image1.setImageResource(R.mipmap.content_websql_img1);
                content_image1.setVisibility(View.VISIBLE);
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_WEBSQL_CODE);
                break;
            case "WebSocket":
                content_html.setVisibility(View.VISIBLE);
                findContentFromService(StaticData.HTML5_WEBSOKET_CODE);
                break;

        }
        return true;
    }

    /**
     * 从服务器获取内容
     * 同步方式去去获取内容
     */
    public void findContentFromService(int id) {
        contentList = null;
        rowID = Integer.toString(id);
        final String rowId = Integer.toString(id);
        Thread thread = new Thread(new Runnable() {

            //step 1: 同样的需要创建一个OkHttpClick对象
            OkHttpClient okHttpClient = new OkHttpClient();

            //step 2: 创建  FormBody.Builder
            FormBody formBody = new FormBody.Builder()
                    .add("rowId", rowId)
                    .build();

            //step 3: 创建请求
            Request request = new Request.Builder().url(StaticData.MAINACTIVITY_URL_GETCONTENT)
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
                        ResultListData<ContentInfo> resultListData = JSON.parseObject(responsestr, new TypeReference<ResultListData<ContentInfo>>() {
                        });
                        if (resultListData.isSuccess()) {
                            contentList = resultListData.getList();
                            mhandler.obtainMessage(1).sendToTarget();
                        } else {
                            mhandler.obtainMessage(0, resultListData.getMessage()).sendToTarget();
                        }
                    } else {
                        System.out.println("请求失败");
                        mhandler.obtainMessage(0, "服务器出现异常，请联系管理员!").sendToTarget();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mhandler.obtainMessage(0, "服务器异常,请联系管理员!").sendToTarget();
                }
            }
        });
        thread.start();
    }

    /**
     * 发表
     * 同步方式去提交到服务器
     */
    public void findContentToService(final String text) {
        contentList = null;

        Thread thread = new Thread(new Runnable() {

            //step 1: 同样的需要创建一个OkHttpClick对象
            OkHttpClient okHttpClient = new OkHttpClient();

            //step 2: 创建  FormBody.Builder
            FormBody formBody = new FormBody.Builder()
                    .add("rowId", rowID)
                    .add("userName", mInfo.getUserName())
                    .add("wordsContent", text)
                    .add("theState", "1")
                    .add("orderBy", "ASC")
                    .add("pageNumber", "1")
                    .build();

            //step 3: 创建请求
            Request request = new Request.Builder().url(StaticData.MAINACTIVITY_URL_INSERTWORDS)
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
                        ResultListData<WordsInfo> resultListData = JSON.parseObject(responsestr, new TypeReference<ResultListData<WordsInfo>>() {
                        });
                        if (resultListData.isSuccess()) {
                            mhandler.obtainMessage(2, resultListData.getMessage()).sendToTarget();
                        } else {
                            mhandler.obtainMessage(0, resultListData.getMessage()).sendToTarget();
                        }

                    } else {
                        System.out.println("请求失败");
                        mhandler.obtainMessage(0, "服务器出现异常，请联系管理员!").sendToTarget();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mhandler.obtainMessage(0, "服务器出现异常，请联系管理员!").sendToTarget();
                }
            }
        });
        thread.start();
    }


    //滚动框获取焦点，并关闭系统输入法
    private void scrollViewGetFocusable() {

        content_scrollview.setFocusable(true);
        content_scrollview.setFocusableInTouchMode(true);
        content_scrollview.requestFocus();
        //关闭输入法
        InputMethodManager imm = (InputMethodManager) MainActivity
                .this
                .getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(liuyan_editext_text.getWindowToken(), 0);
    }

    /**
     * 设置主界面内容都为空,图片组件不可视
     */
    private void setContentViewToNull() {
        content_title.setText("");
        content_text1.setText("");
        content_html.setText("");
        content_html.setVisibility(View.GONE);
        content_text2.setText("");
        content_image1.setVisibility(View.GONE);
        content_text3.setText("");
        content_text3.setTextSize(18);
        content_image2.setVisibility(View.GONE);
    }

    /**
     * 登录或者查看个人信息
     */
    private void goUserInfoOrLogin() {
        if (mInfo != null) {
            //去修改个人信息
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("userInfo", mInfo);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            //去登录
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            checkDialog(StaticData.MAINACTIVITY_CLOSE_GO, "真的要退出吗？", "狠心离开", "再学会儿");
        }
    }

    //提示是否退出
    private void checkDialog(final int state, String message, String positiveBtnText, String negativeBtnText) {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (state == StaticData.MAINACTIVITY_WORDS_GO) {
                            if (mInfo == null) {//处于未登录状态
                                Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(MainActivity.this, WordsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("userInfo", mInfo);
                            bundle.putSerializable("rowid", rowID);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            finish();
                        }
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
