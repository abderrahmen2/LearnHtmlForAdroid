package com.learnhtml;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

    //右上角组件
    View contentView;                              //右上角菜单选项
    private ImageView actionBar;                    //右上角ActionBar
    private Button light;
    private Button store;
    private Button happy;
    private Button about;

    private DrawerLayout drawer = null;
    private Toolbar toolbar = null;
    private List<ContentInfo> contentList;          //服务器返回的知识list
    private String rowID = "0";                     //知识内容组号
    private UserInfo mInfo = null;                  //个人信息
    private String appLight = "日间模式"; //亮度模式

    //侧滑页头
    private NavigationView navigationView = null;   //侧滑界面
    View headerLayout = null;                       //侧滑界面标题布局
    private TextView nav_text_username = null;      //显示用户名
    private ImageView nav_tes = null;               //显示头像
    private TextView nav_text = null;               //显示性别

    //content_main
    private TextView content_title;
    private TextView content_text1;
    private TextView content_text2;
    private TextView content_text3;
    private ImageView content_image1;
    private ImageView content_image2;
    private TextView content_html;
    private TextView content_remark;
    private ScrollView content_scrollview;

    //留言
    private Button liuyan_btn_submit;           //发表留言按钮
    private Button liuyan_btn_all;              //查看全部留言按钮
    private EditText liuyan_editext_text;       //留言内容输入框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        setTitle(R.string.mainactivity_tip_title);

        findView();
        nav_loginView();
        setListenter();
        findActionBarView();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //其他Activity传来数据进行处理
        Intent intent = getIntent();
        Serializable lg = intent.getSerializableExtra("light");
        if (lg == null) {
            setMainLight(null);
        } else {
            setMainLight(lg.toString());
        }
        Serializable data = intent.getSerializableExtra("userInfo");
        if (data != null) {
            mInfo = (UserInfo) data;
            nav_text_username.setText(getString(R.string.mainactivity_tips_username) + mInfo.getUserName());
        }
        //设置初始化界面
        setContentViewToNull();
        findContentFromService(StaticData.HTML_JIANJIE_CODE);

    }

    //侧滑页面组件
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

    //ActionBar右上角组件
    private void findActionBarView() {

        contentView = getLayoutInflater().inflate(R.layout.actionbar_main, null);
        actionBar = (ImageView) findViewById(R.id.actionbar);

        happy = (Button) contentView.findViewById(R.id.action_btn_happy);
        store = (Button) contentView.findViewById(R.id.action_btn_store);
        light = (Button) contentView.findViewById(R.id.action_btn_light);
        about = (Button) contentView.findViewById(R.id.action_btn_about);

        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = light.getText().toString();
                setMainLight(str);
            }
        });
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("light", appLight);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        actionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBarMenu();
            }
        });

    }

    //获取主界面组件
    private void findView() {

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
                    mhandler.obtainMessage(0, getString(R.string.mainactivity_tips_gotologin)).sendToTarget();
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
                    mhandler.obtainMessage(0, getString(R.string.mainactivity_tips_gotologin)).sendToTarget();
                    return;
                }
                String mtext = liuyan_editext_text.getText().toString();

                //调用方法对内容进行验证
                ResultSimple resultSimple = ValidateUtils.msIsLengthRule(mtext, 2, 2000);
                if (!resultSimple.isBoolean()) {
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
                content_remark.setText(R.string.content_bottom);
                content_image1.setVisibility(View.VISIBLE);
                content_image2.setVisibility(View.VISIBLE);

                //list中有5个内容,表示只有一个标题,一个内容1，一个内容2
                if (contentList.size() == 3) {
                    content_text2.setText(contentList.get(2).getContent());
                }
                //list中有4个内容,表示只有一个标题,一个内容1，一个代码块，一个内容2
                else if (contentList.size() == 4) {
                    content_html.setVisibility(View.VISIBLE);
                    content_html.setText(contentList.get(2).getContent());
                    content_text2.setText(contentList.get(3).getContent());
                }
                //list中有5个内容,表示只有一个标题,一个内容1，一个代码块，一个内容2，一个内容3
                else if (contentList.size() == 5) {
                    content_html.setVisibility(View.VISIBLE);
                    content_html.setText(contentList.get(2).getContent());
                    content_text2.setText(contentList.get(3).getContent());
                    content_text3.setText(contentList.get(4).getContent());
                }
            }
            //提示
            else if (msg.what == 0) {
                Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
            }
            //留言成功后
            else if (msg.what == 2) {
                liuyan_editext_text.setText("");
                scrollViewGetFocusable();
                checkDialog(StaticData.MAINACTIVITY_WORDS_GO, getString(R.string.mainactivity_dialog_wordssuccess)
                        , getString(R.string.mainactivity_dialog_gonow), getString(R.string.mainactivity_dialog_goletter));
            }
            //提示
            else if (msg.what == 4) {
                content_remark.setText(msg.obj.toString());
            }
        }
    };

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
                findContentFromService(StaticData.HTML_BIANJIQI_CODE);
                content_image1.setImageResource(R.mipmap.content_bianjiqi_img2);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "HTML元素":
                findContentFromService(StaticData.HTML_YUANSU_CODE);
                break;

            case "HTML标题":
                findContentFromService(StaticData.HTML_BIAOTI_CODE);
                content_image1.setImageResource(R.mipmap.content_biaoti_img1);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "HTML链接":
                findContentFromService(StaticData.HTML_LIANJIE_CODE);
                break;

            case "HTML头部":
                findContentFromService(StaticData.HTML_TOUBU_CODE);
                // content_html.setVisibility(View.VISIBLE);
                break;

            case "HTML表格":
                findContentFromService(StaticData.HTML_BIAOGE_CODE);
                content_image1.setImageResource(R.mipmap.content_biaoge_img1);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "HTML图片":
                findContentFromService(StaticData.HTML_TUPIAN_CODE);
                content_image1.setImageResource(R.mipmap.content_tupian_img1);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "HTML区块":
                findContentFromService(StaticData.HTML_QUKUAI_CODE);
                content_image1.setImageResource(R.mipmap.content_qukuai_img1);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "HTML布局":
                findContentFromService(StaticData.HTML_BUJU_CODE);
                content_image1.setImageResource(R.mipmap.content_buju_img1);
                //content_image1.setVisibility(View.VISIBLE);
                // content_html.setVisibility(View.VISIBLE);
                break;

            case "HTML表单":
                findContentFromService(StaticData.HTML_BIAODAN_CODE);
                content_image1.setImageResource(R.mipmap.content_biaodan_img1);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "HTML框架":
                findContentFromService(StaticData.HTML_KUANGJIA_CODE);
                content_image1.setImageResource(R.mipmap.content_kuangjia_img1);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "HTML总结":
                findContentFromService(StaticData.HTML_ZONGJIE_CODE);
                break;

            //HTML5模块
            case "HTML5教程":
                findContentFromService(StaticData.HTML5_JIAOCHENG_CODE);
                break;

            case "浏览器支持":
                findContentFromService(StaticData.HTML5_LIULANQI_CODE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "Canvas":
                findContentFromService(StaticData.HTML5_CANVAS_CODE);
                content_image1.setImageResource(R.mipmap.content_canvas_img1);
                // content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "内联SVG":
                findContentFromService(StaticData.HTML5_NEILIANSVG_CODE);
                content_image1.setImageResource(R.mipmap.content_neiliansvg_img1);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "MathML":
                findContentFromService(StaticData.HTML5_MATHML_CODE);
                content_image1.setImageResource(R.mipmap.content_mathml_img1);
                // content_image1.setVisibility(View.VISIBLE);
                // content_html.setVisibility(View.VISIBLE);
                content_image2.setImageResource(R.mipmap.content_mathml_img2);
                // content_image2.setVisibility(View.VISIBLE);
                content_text3.setTextSize(13);

                break;

            case "拖放":
                findContentFromService(StaticData.HTML5_TUOFANG_CODE);
                content_image1.setImageResource(R.mipmap.content_tuofang_img1);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "地理定位":
                findContentFromService(StaticData.HTML5_DILIDINGWEI_CODE);
                content_image1.setImageResource(R.mipmap.content_dilidingwei_img1);
                //content_image1.setVisibility(View.VISIBLE);
                content_image2.setImageResource(R.mipmap.content_dilidingwei_img2);
                //content_image2.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "video":
                findContentFromService(StaticData.HTML5_VIDEO_CODE);
                content_image1.setImageResource(R.mipmap.content_video_img1);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "audio":
                findContentFromService(StaticData.HTML5_AUDIO_CODE);
                content_image1.setImageResource(R.mipmap.content_audio_img1);
                //content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "input类型":
                findContentFromService(StaticData.HTML5_INPUTLEIXING_CODE);
                break;

            case "表单元素":
                findContentFromService(StaticData.HTML5_BIAODANYUANSU_CODE);
                content_image1.setImageResource(R.mipmap.content_biaodanyuansu_img1);
                // content_image1.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "表单属性":
                findContentFromService(StaticData.HTML5_BIAODANSHUXING_CODE);
                //content_html.setVisibility(View.VISIBLE);
                break;

            case "web存储":
                findContentFromService(StaticData.HTML5_WEBCUNCHU_CODE);
                content_image2.setImageResource(R.mipmap.content_webcunchu_img1);
                //content_image2.setVisibility(View.VISIBLE);
                //content_html.setVisibility(View.VISIBLE);
                content_text3.setTextSize(13);
                break;

            case "WebSql":
                findContentFromService(StaticData.HTML5_WEBSQL_CODE);
                content_image1.setImageResource(R.mipmap.content_websql_img1);
                break;

            case "WebSocket":
                findContentFromService(StaticData.HTML5_WEBSOKET_CODE);
                //content_html.setVisibility(View.VISIBLE);
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
                            System.out.println("list输出：" + contentList);
                            mhandler.obtainMessage(1).sendToTarget();
                        } else {
                            mhandler.obtainMessage(4, resultListData.getMessage()).sendToTarget();
                        }
                    } else {
                        System.out.println("请求失败");
                        mhandler.obtainMessage(4, getString(R.string.mainactivity_tips_serviceexcetion)).sendToTarget();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mhandler.obtainMessage(4, getString(R.string.mainactivity_tips_serviceexcetion2)).sendToTarget();
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
                        mhandler.obtainMessage(0, getString(R.string.mainactivity_tips_serviceexcetion)).sendToTarget();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mhandler.obtainMessage(0, getString(R.string.mainactivity_tips_serviceexcetion2)).sendToTarget();
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

        content_text3.setText("");
        content_text3.setTextSize(18);
        content_image1.setVisibility(View.GONE);
        content_image2.setVisibility(View.GONE);
        content_image1.setBackground(null);
        content_image2.setBackground(null);
        content_remark.setText(R.string.content_remark_ining);
    }

    //界面光亮设置
    private void setMainLight(String str) {
        if (str == null) {
            drawer.setBackgroundColor(Color.WHITE);
            navigationView.setBackgroundColor(Color.WHITE);
            return;
        }
        appLight = str;

        if (getString(R.string.mainactivitu_actionbar_white).equals(str)) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.nav_back_color_nig));
            drawer.setBackgroundColor(getResources().getColor(R.color.MainActivityBackColor_nig));
            light.setText(getString(R.string.mainactivitu_actionbar_black));
        } else if (getString(R.string.mainactivitu_actionbar_black).equals(str)) {
            drawer.setBackgroundColor(Color.WHITE);
            navigationView.setBackgroundColor(Color.WHITE);
            light.setText(getString(R.string.mainactivitu_actionbar_white));
        }
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
            bundle.putSerializable("light", appLight);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            //去登录
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("light", appLight);
            intent.putExtras(bundle);
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
            checkDialog(StaticData.MAINACTIVITY_CLOSE_GO, getString(R.string.mainactivity_dialog_exit)
                    , getString(R.string.mainactivity_dialog_exitnow), getString(R.string.mainactivity_dialog_exitletter));
        }
    }

    //提示是否退出
    private void checkDialog(final int state, String message, String positiveBtnText, String negativeBtnText) {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.applogo)
                .setTitle(R.string.mainactivity_dialog_tps)
                .setMessage(message)
                .setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (state == StaticData.MAINACTIVITY_WORDS_GO) {
                            if (mInfo == null) {//处于未登录状态
                                mhandler.obtainMessage(0, getString(R.string.mainactivity_tips_gotologin)).sendToTarget();
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

    private void actionBarMenu() {

        PopupWindow popWnd = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置背景图片
        popWnd.setBackgroundDrawable(getResources().getDrawable(R.mipmap.actionbar_img));
        // 需要设置一下此参数，点击外边可消失
        popWnd.setOutsideTouchable(true);

        //指定popup窗口位于相对某组件的位置
        popWnd.showAsDropDown(actionBar, -200, 20);
        //显示在某控件的下方
        //popWnd.showAsDropDown(actionBar);
        //如果窗口存在，则更新
        // popWnd.update();
    }

}
