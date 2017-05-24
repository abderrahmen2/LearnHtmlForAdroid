package com.utils;

/**
 * 静态常量
 * Created by HUANG on 2017/5/7.
 */

public class StaticData {
    //服务器地址
    private final static String SERVICE_URL = "http://192.168.23.1:1101";

    //请求获取知识内容URL
    public final static String MAINACTIVITY_URL_GETCONTENT = SERVICE_URL+"/learn-html/learnhtml/content/learnhtml!findContent.action";
    //主界面发表留言URL
    public final static String MAINACTIVITY_URL_INSERTWORDS = SERVICE_URL+"/learn-html/learnhtml/words/learnhtml!insertWords.action";
    //登记用户URL
    public final static String REGISTER_URL_INSERTUSER = SERVICE_URL+"/learn-html/learnhtml/user/learnhtml!insert.action";
    //更新用户资料URL
    public final static String REGISTER_URL_UPDATEUSER = SERVICE_URL+"/learn-html/learnhtml/user/learnhtml!update.action";
    //登录的URL
    public final static String LOGIN_URL_LOGIN = SERVICE_URL+"/learn-html/learnhtml/user/learnhtml!login.action";
    //按组号查看留言内容URL
    public final static String WORDS_URL_SELECTWORDSBYROWID = SERVICE_URL+"/learn-html/learnhtml/words/learnhtml!selectWordsForUser.action";
    //回复留言URL
    public final static String WORDS_URL_INSERTWORDS = SERVICE_URL+"/learn-html/learnhtml/words/learnhtml!insertWords.action";
    //删除留言内容URL
    public final static String WORDS_URL_DELETEWORDS = SERVICE_URL+"/learn-html/learnhtml/words/learnhtml!deleteWords.action";
    //按组号和用户名查找留言URL
    public final static String WORDS_URL_SELECTWORDSFORUSER = SERVICE_URL+"/learn-html/learnhtml/words/learnhtml!selectWordsForUser.action";


    //内容请求组号
    public final static int HTML_JIAOCHENG_CODE = 20100;        //HTML教程
    public final static int HTML_JIANJIE_CODE = 20200;          //HTML简介
    public final static int HTML_BIANJIQI_CODE = 20300;         //HTML编辑器
    public final static int HTML_JICHU_CODE = 20400;            //HTML基础
    public final static int HTML_YUANSU_CODE = 20500;           //HTML元素
    public final static int HTML_BIAOTI_CODE = 20600;           //HTML标题
    public final static int HTML_DUANLUO_CODE = 20700;          //HTML段落
    public final static int HTML_LIANJIE_CODE = 20800;          //HTML链接
    public final static int HTML_TOUBU_CODE = 20900;            //HTML头部
    public final static int HTML_BIAOGE_CODE = 21000;           //HTML表格
    public final static int HTML_TUPIAN_CODE = 21100;           //HTML图片
    public final static int HTML_QUKUAI_CODE = 21200;           //HTML区块
    public final static int HTML_BUJU_CODE = 21300;             //HTML布局
    public final static int HTML_BIAODAN_CODE = 21400;          //HTML表单
    public final static int HTML_KUANGJIA_CODE = 21500;         //HTML框架
    public final static int HTML_ZONGJIE_CODE = 21600;          //HTML总结

    public final static int HTML5_JIAOCHENG_CODE = 21700;       //HTML5教程
    public final static int HTML5_LIULANQI_CODE = 21800;        //HTML5浏览器
    public final static int HTML5_XINYUANSU_CODE = 21900;       //HTML5新元素
    public final static int HTML5_CANVAS_CODE = 22000;          //HTML5 CANVAS
    public final static int HTML5_NEILIANSVG_CODE = 22100;      //HTML5内联SVG
    public final static int HTML5_MATHML_CODE = 22200;          //HTML5 MATHML
    public final static int HTML5_TUOFANG_CODE = 22300;         //HTML5拖放
    public final static int HTML5_DILIDINGWEI_CODE = 22400;     //HTML5地理定位
    public final static int HTML5_VIDEO_CODE = 22500;           //HTML5 VIDEO
    public final static int HTML5_AUDIO_CODE = 22600;           //HTML5 AUDIO
    public final static int HTML5_INPUTLEIXING_CODE = 22700;    //HTML5 INPUT类型
    public final static int HTML5_BIAODANYUANSU_CODE = 22800;   //HTML5表单元素
    public final static int HTML5_BIAODANSHUXING_CODE = 22900;  //HTML5表单属性
    public final static int HTML5_WEBCUNCHU_CODE = 23000;       //HTML5 Web存储
    public final static int HTML5_WEBSQL_CODE = 23100;          //HTML5 webSql
    public final static int HTML5_WEBSOKET_CODE = 23200;        //HTML5 Web Soket


    public final static int REGISTER_RESULT_CODE = 1101;     //注册成功返回编号
    public final static int LOGIN_RESULT_CODE = 1102;        //登录成功返回编号


    public final static int REGISTER_ACTIVITY_CODE = 110101; //注册页面编号
    public final static int LOGIN_ACTIVITY_CODE = 110102;    //登录页面编号

    public final static int MAINACTIVITY_WORDS_GO = 1;      //主界面留言成功标示
    public final static int MAINACTIVITY_CLOSE_GO = 2;      //退出程序标示
}
