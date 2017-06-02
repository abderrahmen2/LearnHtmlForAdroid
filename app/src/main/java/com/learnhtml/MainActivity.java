package com.learnhtml;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.entity.ContentInfo;
import com.entity.SysMenu;
import com.entity.UserInfo;
import com.entity.WordsInfo;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.utils.ContentListData;
import com.utils.ResultListData;
import com.utils.ResultSimple;
import com.utils.StaticData;
import com.utils.ValidateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //右上角组件
    View contentView;                               //右上角菜单选项
    private ImageView actionBar;                    //右上角ActionBar
    private Button light;                           //夜间模式
    private Button store;                           //书籍推荐商城
    private Button happy;                           //开心一刻
    private Button about;                           //关于
    private Button share;                           //分享
    PopupWindow popWnd;

    private DrawerLayout drawer = null;
    private Toolbar toolbar = null;
    private List<SysMenu> menutList;
    private List<ContentInfo> contentList;          //服务器返回的知识list
    private String rowID = "0";                     //知识内容组号
    private UserInfo mInfo = null;                  //个人信息
    private String appLight = "日间模式";            //亮度模式常量
    private int networkState = 1;                   //网络状态，以便知道是联网版还是单机版,0表示单机版

    //侧滑页头
    private NavigationView navigationView = null;   //侧滑界面
    View headerLayout = null;                       //侧滑界面标题布局
    private TextView nav_text_username = null;      //显示用户名
    private ImageView nav_tes = null;               //显示头像
    private TextView nav_text = null;               //显示性别
    private ListView nav_listview;                  //联网版时侧滑页面的菜单

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
    LinearLayout layout_liuyan;                 //主界面的留言功能界面布局

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

        contentList = new ArrayList<>();
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
            System.out.println(mInfo.getUserName() + "已经登录的用户名");
            nav_text.setText("手机号码:" + mInfo.getPhone());
            nav_text_username.setText(getString(R.string.mainactivity_tips_username) + mInfo.getUserName());
        }
        //设置初始化界面
        setContentViewToNull();

        //单机版时需要设置提示信息
       // content_remark.setText("欢迎来到HTML5学习基地！");

        //联网版需要加载菜单
        findMenu();
        //联网版需要加载初始化知识界面
        findContentFromService(StaticData.HTML_JIANJIE_CODE);

    }

    //侧滑页面组件
    private void nav_loginView() {

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //添加固定的菜单
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        //单机版时隐藏侧滑页面页头
        // headerLayout.setVisibility(View.GONE);
        nav_tes = (ImageView) headerLayout.findViewById(R.id.nav_header_btn_img);
        nav_tes.setVisibility(View.INVISIBLE);
        nav_text = (TextView) headerLayout.findViewById(R.id.nav_header_text1);
        nav_text_username = (TextView) headerLayout.findViewById(R.id.nav_header_username);
        nav_text_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("点击了去登录");
                goUserInfoOrLogin();
            }
        });
        nav_tes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("点击了去登录");
                goUserInfoOrLogin();
            }
        });

        nav_listview = (ListView) findViewById(R.id.nav_listview);
        nav_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //初始化界面
                setContentViewToNull();
                setActionToNavMenu(menutList.get(i).getMenuName());
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
        share = (Button) contentView.findViewById(R.id.action_btn_share);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWnd.dismiss();
                shareApp();
            }
        });

        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = light.getText().toString();
                setMainLight(str);
                popWnd.dismiss();
            }
        });
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWnd.dismiss();
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
                popWnd.dismiss();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://www.qiushibaike.com/");
                intent.setData(content_url);
                startActivity(intent);

            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWnd.dismiss();
                detailsDialog();
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
        layout_liuyan = (LinearLayout) findViewById(R.id.main_liuyan_layout);
        //单机版时需要隐藏此布局
        //layout_liuyan.setVisibility(View.GONE);

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
                bundle.putSerializable("light", appLight);
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
                    mhandler.obtainMessage(0, "留言内容" + resultSimple.getMessage()).sendToTarget();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        setContentViewToNull();
        setActionToNavMenu(item.getTitle().toString());

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
     * 从服务器获取菜单
     * 同步方式去获取侧滑菜单选项
     */
    public void findMenu() {

        Thread thread = new Thread(new Runnable() {

            //step 1: 同样的需要创建一个OkHttpClick对象
            OkHttpClient okHttpClient = new OkHttpClient();

            //step 2: 创建  FormBody.Builder
            FormBody formBody = new FormBody.Builder()
                    .build();

            //step 2: 创建请求
            Request request = new Request.Builder().url(StaticData.MAINACTIVITY_URL_MENU)
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
                        ResultListData<SysMenu> resultListData = JSON.parseObject(responsestr, new TypeReference<ResultListData<SysMenu>>() {
                        });
                        if (resultListData.isSuccess()) {
                            menutList = resultListData.getList();
                            System.out.println("menutList输出：" + menutList);
                            mhandler.obtainMessage(5).sendToTarget();
                        } else {
                            mhandler.obtainMessage(6, resultListData.getMessage()).sendToTarget();
                        }
                    } else {
                        System.out.println("请求失败");
                        mhandler.obtainMessage(6, getString(R.string.mainactivity_tips_serviceexcetion)).sendToTarget();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mhandler.obtainMessage(6, getString(R.string.mainactivity_tips_serviceexcetion2)).sendToTarget();
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


    //侧滑菜单点击事件
    private void setActionToNavMenu(String title) {
        switch (title) {
            case "HTML简介":
                if (networkState == 0) {
                    contentList = ContentListData.getContentList2(getString(R.string.content_jiaocheng_title), getString(R.string.content_jiaocheng_text1));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_JIANJIE_CODE);
                }
                break;

            case "HTML编辑器":
                content_image1.setBackgroundResource(R.mipmap.content_bianjiqi_img2);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_bianjiqi_title), getString(R.string.content_bianjiqi_text1)
                            , getString(R.string.content_bianjiqi_html), getString(R.string.content_bianjiqi_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_BIANJIQI_CODE);
                }
                break;

            case "HTML元素":
                if (networkState == 0) {
                    contentList = ContentListData.getContentListText3(getString(R.string.content_yuansu_title), getString(R.string.content_yuansu_text1), getString(R.string.content_yuansu_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_YUANSU_CODE);
                }
                break;

            case "HTML标题":
                content_image1.setBackgroundResource(R.mipmap.content_biaoti_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_biaoti_title), getString(R.string.content_biaoti_text1)
                            , getString(R.string.content_biaoti_html), getString(R.string.content_biaoti_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_BIAOTI_CODE);
                }
                break;

            case "HTML链接":
                if (networkState == 0) {
                    contentList = ContentListData.getContentListText3(getString(R.string.content_lianjie_title), getString(R.string.content_lianjie_text1), getString(R.string.content_lianjie_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_LIANJIE_CODE);
                }
                break;

            case "HTML头部":
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_toubu_title), getString(R.string.content_toubu_text1)
                            , getString(R.string.content_toubu_html), getString(R.string.content_toubu_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_TOUBU_CODE);
                }
                break;

            case "HTML表格":
                content_image1.setBackgroundResource(R.mipmap.content_biaoge_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList5(getString(R.string.content_biaoge_title), getString(R.string.content_biaoge_text1),
                            getString(R.string.content_biaoge_html), getString(R.string.content_biaoge_text2), getString(R.string.content_biaoge_text3));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_BIAOGE_CODE);
                }
                break;

            case "HTML图片":
                content_image1.setBackgroundResource(R.mipmap.content_tupian_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_tupian_title), getString(R.string.content_tupian_text1)
                            , getString(R.string.content_tupian_html), getString(R.string.content_tupian_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_TUPIAN_CODE);
                }
                break;

            case "HTML区块":
                content_image1.setBackgroundResource(R.mipmap.content_qukuai_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_qukuai_title), getString(R.string.content_qukuai_text1)
                            , getString(R.string.content_qukuai_html), getString(R.string.content_qukuai_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_QUKUAI_CODE);
                }
                break;

            case "HTML布局":
                content_image1.setBackgroundResource(R.mipmap.content_buju_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_buju_title), getString(R.string.content_buju_text1)
                            , getString(R.string.content_buju_html), getString(R.string.content_buju_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_BUJU_CODE);
                }
                break;

            case "HTML表单":
                content_image1.setBackgroundResource(R.mipmap.content_biaodan_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_biaodan_title), getString(R.string.content_biaodan_text1)
                            , getString(R.string.content_biaodan_html), getString(R.string.content_biaodan_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_BIAODAN_CODE);
                }
                break;

            case "HTML框架":
                content_image1.setBackgroundResource(R.mipmap.content_kuangjia_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_kuangjia_title), getString(R.string.content_kuangjia_text1)
                            , getString(R.string.content_kuangjia_html), getString(R.string.content_kuangjia_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_KUANGJIA_CODE);
                }
                break;

            case "HTML总结":
                if (networkState == 0) {
                    contentList = ContentListData.getContentListText3(getString(R.string.content_zongjie_title), getString(R.string.content_zongjie_text1)
                            , getString(R.string.content_zongjie_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML_ZONGJIE_CODE);
                }
                break;

            //HTML5模块
            case "HTML5教程":
                if (networkState == 0) {
                    contentList = ContentListData.getContentListText3(getString(R.string.content_html5jiaocheng_title), getString(R.string.content_html5jiaocheng_text1)
                            , getString(R.string.content_html5jiaocheng_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_JIAOCHENG_CODE);
                }
                break;

            case "浏览器支持":
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_html5liulanqi_title), getString(R.string.content_html5liulanqi_text1)
                            , getString(R.string.content_html5liulanqi_html), getString(R.string.content_html5liulanqi_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_LIULANQI_CODE);
                }
                break;

            case "Canvas":
            case "CANVAS":
                content_image1.setBackgroundResource(R.mipmap.content_canvas_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_canvas_title), getString(R.string.content_canvas_text1)
                            , getString(R.string.content_canvas_html), getString(R.string.content_canvas_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_CANVAS_CODE);
                }
                break;

            case "内联SVG":
                content_image1.setBackgroundResource(R.mipmap.content_neiliansvg_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_neiliansvg_title), getString(R.string.content_neiliansvg_text1)
                            , getString(R.string.content_neiliansvg_html), getString(R.string.content_neiliansvg_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_NEILIANSVG_CODE);
                }
                break;

            case "MathML":
            case "MATHML":
                content_image1.setBackgroundResource(R.mipmap.content_mathml_img1);
                content_image2.setBackgroundResource(R.mipmap.content_mathml_img2);
                content_text3.setTextSize(13);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList5(getString(R.string.content_mathml_title), getString(R.string.content_mathml_text1)
                            , getString(R.string.content_mathml_html), getString(R.string.content_mathml_text2), getString(R.string.content_mathml_text3));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_MATHML_CODE);
                }
                break;

            case "拖放":
                content_image1.setBackgroundResource(R.mipmap.content_tuofang_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList5(getString(R.string.content_tuofang_title), getString(R.string.content_tuofang_text1)
                            , getString(R.string.content_tuofang_html), getString(R.string.content_tuofang_text2), getString(R.string.content_tuofang_text3));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_TUOFANG_CODE);
                }
                break;

            case "地理定位":
                content_image1.setBackgroundResource(R.mipmap.content_dilidingwei_img1);
                content_image2.setBackgroundResource(R.mipmap.content_dilidingwei_img2);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList5(getString(R.string.content_dilidingwei_title), getString(R.string.content_dilidingwei_text1)
                            , getString(R.string.content_dilidingwei_html), getString(R.string.content_dilidingwei_text2), getString(R.string.content_dilidingwei_text3));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_DILIDINGWEI_CODE);
                }
                break;

            case "video":
            case "VIDEO":
                content_image1.setBackgroundResource(R.mipmap.content_video_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_video_title), getString(R.string.content_video_text1)
                            , getString(R.string.content_video_html), getString(R.string.content_video_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_VIDEO_CODE);
                }
                break;

            case "AUDIO":
            case "audio":
                content_image1.setBackgroundResource(R.mipmap.content_audio_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_audio_title), getString(R.string.content_audio_text1)
                            , getString(R.string.content_audio_html), getString(R.string.content_audio_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_AUDIO_CODE);
                }
                break;

            case "input类型":
            case "INPUT类型":
                if (networkState == 0) {
                    contentList = ContentListData.getContentListText3(getString(R.string.content_inputleixing_title), getString(R.string.content_inputleixing_text1)
                            , getString(R.string.content_inputleixing_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_INPUTLEIXING_CODE);
                }
                break;

            case "表单元素":
                content_image1.setBackgroundResource(R.mipmap.content_biaodanyuansu_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_biaodanyuansu_title), getString(R.string.content_biaodanyuansu_text1)
                            , getString(R.string.content_biaodanyuansu_html), getString(R.string.content_biaodanyuansu_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_BIAODANYUANSU_CODE);
                }
                break;

            case "表单属性":
                if (networkState == 0) {
                    contentList = ContentListData.getContentList5(getString(R.string.content_biaodanshuxing_title), getString(R.string.content_biaodanshuxing_text1)
                            , getString(R.string.content_biaodanshuxing_html), getString(R.string.content_biaodanshuxing_text2), getString(R.string.content_biaodanshuxing_text3));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_BIAODANSHUXING_CODE);
                }
                break;

            case "web存储":
            case "WEB存储":
                content_image2.setBackgroundResource(R.mipmap.content_webcunchu_img1);
                content_text3.setTextSize(13);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList5(getString(R.string.content_webcunchu_title), getString(R.string.content_webcunchu_text1)
                            , getString(R.string.content_webcunchu_html), getString(R.string.content_webcunchu_text2), getString(R.string.content_webcunchu_text3));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_WEBCUNCHU_CODE);
                }
                break;

            case "WebSql":
            case "WEBSQL":
                content_image1.setBackgroundResource(R.mipmap.content_websql_img1);
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_websql_title), getString(R.string.content_websql_text1)
                            , getString(R.string.content_websql_html), getString(R.string.content_websql_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_WEBSQL_CODE);
                }
                break;

            case "WebSocket":
            case "WEBSOCKET":
                if (networkState == 0) {
                    contentList = ContentListData.getContentList4(getString(R.string.content_websocket_title), getString(R.string.content_websocket_text1)
                            , getString(R.string.content_websocket_html), getString(R.string.content_websocket_text2));
                    mhandler.obtainMessage(1).sendToTarget();
                } else {
                    findContentFromService(StaticData.HTML5_WEBSOKET_CODE);
                }
                break;
        }
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
        content_image2.setBackgroundResource(0);
        content_image1.setBackgroundResource(0);
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
            nav_listview.setBackgroundColor(getResources().getColor(R.color.nav_back_color_nig));
            navigationView.setBackgroundColor(getResources().getColor(R.color.nav_back_color_nig));
            drawer.setBackgroundColor(getResources().getColor(R.color.MainActivityBackColor_nig));
            light.setText(getString(R.string.mainactivitu_actionbar_black));
        } else if (getString(R.string.mainactivitu_actionbar_black).equals(str)) {
            drawer.setBackgroundColor(Color.WHITE);
            navigationView.setBackgroundColor(Color.WHITE);
            nav_listview.setBackgroundColor(Color.WHITE);
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

    //右上角弹出菜单
    private void actionBarMenu() {

        popWnd = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
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

    //填充侧滑页面菜单
    private void setMenuToListView() {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (SysMenu wordsInfo : menutList) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("menu", wordsInfo.getMenuName());
            listItems.add(listItem);
        }

        //定义特定的适配器
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.listview_menu,
                new String[]{"menu"},
                new int[]{R.id.menu_listview_btn});

        //为ListView添加是配置
        nav_listview.setAdapter(simpleAdapter);
    }

    private static final String TAG = "TAG";

    //关于
    private void detailsDialog() {
        LinearLayout mainView = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_book_details, null);
        TextView text = (TextView) mainView.findViewById(R.id.dialog_bookdetails_text);
        text.setText(R.string.about);
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.applogo)
                .setTitle(getString(R.string.menu_about))
                .setView(mainView)
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create().show();
    }

    //社会化分享
    private void shareApp() {
        UMWeb web = new UMWeb("https://mr.baidu.com/26nxsqe");
        web.setTitle("App名称:HTML5学习基地");//标题
        web.setDescription("一款专门为HTML5初学者设计的App;从HTML到HTML5都有细致的讲解，里面各个知识模块均有实例；初学者可以根据软件提供的流程对内容进行学习，便可有良好的学习效果。");//描述

        new ShareAction(MainActivity.this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        Log.e(TAG, "onStart: ");
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Log.e(TAG, "onResult: ");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        Log.e(TAG, "onCancel: ");
                    }
                }).open();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

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
            //获取侧滑菜单成功
            else if (msg.what == 5) {
                navigationView.getMenu().clear();
                nav_listview.setVisibility(View.VISIBLE);
                setMenuToListView();
            }
            //获取侧滑菜单失败
            else if (msg.what == 6) {
                nav_listview.setVisibility(View.GONE);
                navigationView.inflateMenu(R.menu.activity_main_drawer);
                Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };
}
