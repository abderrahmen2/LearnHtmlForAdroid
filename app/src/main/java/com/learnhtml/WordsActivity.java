package com.learnhtml;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.entity.ContentInfo;
import com.entity.UserInfo;
import com.entity.WordsInfo;
import com.utils.ResultListData;
import com.utils.ResultSimple;
import com.utils.ResultWordsData;
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


public class WordsActivity extends AppCompatActivity {

    private LinearLayout words_main_layout;

    private int listPosition = 1;               //listview位置
    private UserInfo mInfo = null;              //个人信息
    private List<WordsInfo> wordsList = null;   //某个rowId的评论list
    private String rowID;                       //内容组号
    private String orderBy = "ASC";             //留言升降序
    private int pageNumber = 1;                 //总页码
    private String nowpageNumber = "1";         //当前页码
    private String answerText = null;           //留言内容
    private String deleteID = null;             //要删除留言的ID
    private int theState = 0;                   //0表示处于查看全部留言状态，1表示只看与我相关的留言

    private ListView listView;                      //列表
    private Button btn_lookmyself;                  //只看自己
    private Button btn_looktoend;                   //倒序查看
    private Button btn_lookpre;                     //上一页
    private Button btn_looknext;                    //下一页
    private Button btn_gopage;                      //跳到第几页
    private TextView textView_totlepage;            //显示总页数
    private EditText editText_nowpagenumber;        //当前页号，或者要跳到第几页
    private TextView textView_tips;                 //留言为空时提示内容
    private LinearLayout words_layout_none;         //留言为空时，显示的面板
    private Button btn_words_none;                  //留言为空时显示面板里的按钮


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Serializable lg= intent.getSerializableExtra("light");
        if (lg == null) {
            setTheme(R.style.AppTheme_Light_White);
        }
        //点了日间模式
        else if (lg.toString().equals(getString(R.string.mainactivitu_actionbar_white))){
            setTheme(R.style.AppTheme_Light_Black);
        }
        //点了夜间模式
        else if (lg.toString().equals(getString(R.string.mainactivitu_actionbar_black))){
            setTheme(R.style.AppTheme_Light_White);
        }

        setContentView(R.layout.activity_words);
        findView();             //找到界面组件
        setLineter();           //设置监听
        setFocusToListView();   //初始化光标位置


        //其他Activity传来数据进行处理
        Serializable data = intent.getSerializableExtra("userInfo");
        Serializable id = intent.getSerializableExtra("rowid");
        if (data != null) {
            mInfo = (UserInfo) data;
            System.out.println("用户名:" + mInfo.getUserName());
        }
        System.out.println("要查看的组别--->" + id.toString());
        if (id != null && !"0".equals(id.toString())) {
            rowID = id.toString();
            findContentToService(StaticData.WORDS_URL_SELECTWORDSBYROWID);
        }
    }

    private void findView() {
        words_main_layout = (LinearLayout) findViewById(R.id.words_main_layout);
        words_layout_none = (LinearLayout) findViewById(R.id.words_layout_none);

        listView = (ListView) findViewById(R.id.words_listview);
        btn_lookmyself = (Button) findViewById(R.id.words_btn_lookmyself);
        btn_looktoend = (Button) findViewById(R.id.words_btn_looktoend);
        btn_lookpre = (Button) findViewById(R.id.words_btn_lookpre);
        btn_looknext = (Button) findViewById(R.id.words_btn_looknext);
        btn_gopage = (Button) findViewById(R.id.words_btn_gopage);
        textView_totlepage = (TextView) findViewById(R.id.wrods_textview_totlepage);
        editText_nowpagenumber = (EditText) findViewById(R.id.wrods_edittext_pagenumber);
        textView_tips = (TextView) findViewById(R.id.words_textView_tags);
        btn_words_none = (Button) findViewById(R.id.words_btn_answer);
    }

    //设置监听器
    private void setLineter() {

        //为listView中的列表选中事件绑定事件监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setFocusToListView();
                listPosition = position;
                System.out.println("点击了ListView中的行" + position);
                deleteID = wordsList.get(position).getId();
                ckecgeActionDialog();
            }
        });

        //跳到指定页
        btn_gopage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String page = editText_nowpagenumber.getText().toString().trim();
                deleteID = null;
                answerText = null;

                ResultSimple resultSimple = ValidateUtils.msIsNum(page);
                if(!resultSimple.isBoolean()){
                    mhandler.obtainMessage(0, getString(R.string.wrodsactivity_tips_valipage)+resultSimple.getMessage()).sendToTarget();
                    return;
                }

                if (Integer.parseInt(page) > pageNumber) {
                    editText_nowpagenumber.setFocusable(true);
                    editText_nowpagenumber.setFocusableInTouchMode(true);
                    mhandler.obtainMessage(0, getString(R.string.wordsactivity_tips_alltips1) + pageNumber + getString(R.string.wrodsactivity_tips_valitips2)).sendToTarget();
                    return;
                } else {
                    nowpageNumber = page;
                    findContentToService(StaticData.WORDS_URL_SELECTWORDSBYROWID);
                }
            }
        });

        //下一页
        btn_looknext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_lookpre.setEnabled(true);
                setFocusToListView();
                deleteID = null;
                answerText = null;

                //最后一页了
                if (Integer.parseInt(nowpageNumber) >= pageNumber) {
                    btn_looknext.setEnabled(false);
                    mhandler.obtainMessage(0, getString(R.string.wrodsactvity_tips_lastpage)).sendToTarget();
                    return;
                } else {
                    nowpageNumber = Integer.toString(Integer.parseInt(nowpageNumber) + 1);
                    findContentToService(StaticData.WORDS_URL_SELECTWORDSBYROWID);
                }
            }
        });

        //上一页
        btn_lookpre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_looknext.setEnabled(true);
                setFocusToListView();
                deleteID = null;
                answerText = null;

                //第一页了
                if (Integer.parseInt(nowpageNumber) <= 1) {
                    btn_lookpre.setEnabled(false);
                    mhandler.obtainMessage(0, getString(R.string.wordsactivity_tips_firstpage)).sendToTarget();
                    return;

                } else {
                    nowpageNumber = Integer.toString(Integer.parseInt(nowpageNumber) - 1);
                    findContentToService(StaticData.WORDS_URL_SELECTWORDSBYROWID);
                }
            }
        });

        //只看与自己相关的留言
        btn_lookmyself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusToListView();
                btn_lookpre.setEnabled(true);
                btn_looknext.setEnabled(true);
                deleteID = null;
                answerText = null;
                nowpageNumber = "1";//自动跳转到第一页

                //与我相关的留言
                if (getString(R.string.wrodsactivity_btn_ilookme).equals(btn_lookmyself.getText().toString())) {
                    btn_lookmyself.setText(R.string.wrodsactivity_btn_lookall);
                    theState = 1;
                    findContentToService(StaticData.WORDS_URL_SELECTWORDSFORUSER);
                } else {
                    btn_lookmyself.setText(getString(R.string.wrodsactivity_btn_ilookme));
                    theState = 0;
                    findContentToService(StaticData.WORDS_URL_SELECTWORDSBYROWID);
                }
            }
        });
        //倒序查看---
        btn_looktoend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusToListView();
                btn_lookpre.setEnabled(true);
                btn_looknext.setEnabled(true);
                deleteID = null;
                answerText = null;
                nowpageNumber = "1";//自动跳转到第一页

                //倒序查看
                if (getString(R.string.wrodsactivity_btn_lookendto).equals(btn_looktoend.getText())) {
                    orderBy = "DESC";
                    findContentToService(StaticData.WORDS_URL_SELECTWORDSBYROWID);
                    btn_looktoend.setText(R.string.wrodsactivity_btn_looktofirst);
                }
                //正常查看
                else {
                    orderBy = "ASC";
                    findContentToService(StaticData.WORDS_URL_SELECTWORDSBYROWID);
                    btn_looktoend.setText(R.string.wrodsactivity_btn_lookendto);
                }
            }
        });

        //留言为空时，点击”留言“字样的按钮事件
        btn_words_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    Handler mhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            //提示
            if (msg.what == 0) {
                Toast.makeText(WordsActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
            }
            //查找到留言内容了
            else if (msg.what == 1) {
                btn_lookpre.setEnabled(true);
                btn_looknext.setEnabled(true);
                editText_nowpagenumber.setText("");

                if (Integer.parseInt(nowpageNumber) > pageNumber) {
                    nowpageNumber = Integer.toString(pageNumber);
                    editText_nowpagenumber.setText(Integer.toString(pageNumber));
                } else {
                    editText_nowpagenumber.setText(nowpageNumber);
                }

                textView_totlepage.setText(getString(R.string.wrodsactivity_tips_all1) + Integer.toString(pageNumber) + getString(R.string.wordsactivity_tips_all2));
                setListView();
            }
        }
    };

    /**
     * 查找留言，翻页等
     * 同步方式去提交到服务器
     */
    public void findContentToService(final String url) {
        final FormBody formBody = setDataToService();

        Thread thread = new Thread(new Runnable() {

            //step 1: 同样的需要创建一个OkHttpClick对象
            OkHttpClient okHttpClient = new OkHttpClient();

            //step 2: 创建  FormBody.Builder


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

                        //对服务器返回的List进行JSON
                        ResultWordsData<WordsInfo> resultListData = JSON.parseObject(responsestr, new TypeReference<ResultWordsData<WordsInfo>>() {
                        });
                        if (resultListData.isSuccess()) {
                            if (resultListData.getList() != null) {
                                wordsList = resultListData.getList();
                            }
                            //获取数据
                            pageNumber = resultListData.getPageNumber();    //获取总页数
                            mhandler.obtainMessage(1).sendToTarget();

                        } else {//服务器端出现异常
                            mhandler.obtainMessage(0, resultListData.getMessage()).sendToTarget();
                        }
                    } else {
                        System.out.println("请求服务器失败");
                        mhandler.obtainMessage(0, getString(R.string.wrodsactivity_tips_service1)).sendToTarget();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mhandler.obtainMessage(0, getString(R.string.wordsactivity_tips_service2)).sendToTarget();
                }
            }
        });
        thread.start();
    }


    //焦点事件处理,并关闭输入法
    private void setFocusToListView() {
        words_main_layout.setFocusable(true);
        words_main_layout.setFocusableInTouchMode(true);
        words_main_layout.requestFocus();

        //关闭输入法
        InputMethodManager imm = (InputMethodManager) WordsActivity
                .this
                .getSystemService(WordsActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText_nowpagenumber.getWindowToken(), 0);
    }

    //显示内容在listView中
    private void setListView() {
        if (wordsList.size() == 0) {
            listView.setVisibility(View.GONE);
            viewController(View.INVISIBLE);
            words_layout_none.setVisibility(View.VISIBLE);
        } else {
            words_layout_none.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            viewController(View.VISIBLE);

            //封装数据
            List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
            for (WordsInfo wordsInfo : wordsList) {
                Map<String, Object> listItem = new HashMap<String, Object>();
                if (wordsInfo.getAnswerUserName() != null) {
                    if (mInfo.getUserName().equals(wordsInfo.getUserName())) {
                        listItem.put("name", "我 回复 " + wordsInfo.getAnswerUserName() + ": ");
                    } else if (mInfo.getUserName().equals(wordsInfo.getAnswerUserName())) {
                        listItem.put("name", wordsInfo.getUserName() + " 回复 " + "我 : ");
                    } else {
                        listItem.put("name", wordsInfo.getUserName() + " 回复 " + wordsInfo.getAnswerUserName() + ": ");
                    }

                } else if (wordsInfo.getAnswerUserName() == null && mInfo.getUserName().equals(wordsInfo.getUserName())) {
                    listItem.put("name", "我  说 : ");
                } else {
                    listItem.put("name", wordsInfo.getUserName() + " 说 : ");
                }

                int rowmum = (Integer.valueOf(nowpageNumber) - 1) * 10 + Integer.valueOf(wordsInfo.getRowNum());
                if (theState == 1) {
                    listItem.put("rownum", "");
                } else {
                    listItem.put("rownum", rowmum + "楼");
                }
                listItem.put("content", wordsInfo.getWordsContent());
                listItem.put("time", wordsInfo.getWordsTime());
                listItems.add(listItem);
            }
            //定义特定的适配器
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.listview_main,
                    new String[]{"name", "content", "time", "rownum"},
                    new int[]{R.id.wrods_listview_name, R.id.words_listview_content, R.id.words_listview_time, R.id.words_listview_rowNum
                    });

            //为ListView添加是配置
            listView.setAdapter(simpleAdapter);
        }
    }

    //点击ListView后弹出框
    private void ckecgeActionDialog() {

        if (mInfo.getUserName().equals(wordsList.get(listPosition).getUserName())) {
            String initem[] = {getString(R.string.wordsactivity_str_delete)};
            new AlertDialog.Builder(this)
                    .setItems(initem, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mySureDeleteWordsDialog();
                        }
                    })
                    .create()
                    .show();
        } else {
            String initem[] = {getString(R.string.wrordsactivity_str_answer)};
            new AlertDialog.Builder(this)
                    .setItems(initem, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myAnswerDialog();
                        }
                    })
                    .create()
                    .show();
        }
    }

    //回复，弹窗
    private void myAnswerDialog() {
        final LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_liuyan_main, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //设置标题
        dialog.setTitle(getString(R.string.wrodsactivity_tips_answer) + wordsList.get(listPosition).getUserName())
                //设置界面
                .setView(linearLayout)
                //设置确定按钮
                .setPositiveButton(R.string.wrodsactivity_btn_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) linearLayout.findViewById(R.id.dialog_liuyan_text);
                        String text = editText.getText().toString().trim();
                        if (text.length() < 2 || "".equals(text) || text == null) {

                            keepDialogOpen((AlertDialog) dialog);           //利用反射使得对话框不关闭
                            mhandler.obtainMessage(0, getString(R.string.wrodsactivity_tips_str1)).sendToTarget();
                        } else {
                            answerText = text;
                            deleteID = null;
                            nowpageNumber = editText_nowpagenumber.getText().toString().trim();

                            closeDialog((AlertDialog) dialog);
                            findContentToService(StaticData.WORDS_URL_INSERTWORDS);
                        }
                    }
                })
                //设置取消按钮
                .setNegativeButton(R.string.wrodsactivity_btn_quxiao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //利用反射关闭对话框
                        closeDialog((AlertDialog) dialog);
                    }
                });
        dialog.create();
        dialog.show();
    }

    //保持dialog不关闭的方法
    private void keepDialogOpen(AlertDialog dialog) {
        try {
            java.lang.reflect.Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭dialog的方法
    private void closeDialog(AlertDialog dialog) {
        try {
            java.lang.reflect.Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //删除自己留言确认对话框
    private void mySureDeleteWordsDialog() {
        new AlertDialog.Builder(this)
                //设置标题
                .setTitle(getString(R.string.wrodsactivity_tips_answer) + wordsList.get(listPosition).getUserName())
                //设置界面
                .setIcon(R.mipmap.applogo)
                .setTitle(R.string.wrodsactivity_dialog_tips)
                .setMessage(R.string.wrodsactivity_dialog_suredelete)
                //设置确定按钮
                .setPositiveButton(getString(R.string.wrodsactivity_btn_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        answerText = null;
                        findContentToService(StaticData.WORDS_URL_DELETEWORDS);
                    }
                })
                //设置取消按钮
                .setNegativeButton(R.string.wrodsactivity_dialog_quxiao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //不做任何处理
                    }
                })
                .create()
                .show();
    }

    //传到服务器的参数，有多个，传什么
    private FormBody setDataToService() {
        FormBody formBody;
        //回复留言
        if (answerText != null) {
            System.out.println("回复留言!");
            FormBody formBody2 = new FormBody.Builder()
                    .add("rowId", rowID)
                    .add("theState", Integer.toString(theState))
                    .add("orderBy", orderBy)
                    .add("pageNumber", nowpageNumber)
                    .add("userName", mInfo.getUserName())
                    .add("answerUserName", wordsList.get(listPosition).getUserName())
                    .add("wordsContent", answerText)
                    .build();
            return formBody2;
        }
        //删除留言
        if (deleteID != null) {
            System.out.println("删除留言!");
            FormBody formBody2 = new FormBody.Builder()
                    .add("rowId", rowID)
                    .add("id", deleteID)
                    .add("theState", Integer.toString(theState))
                    .add("orderBy", orderBy)
                    .add("pageNumber", nowpageNumber)
                    .add("userName", mInfo.getUserName())
                    .build();
            return formBody2;
        }

        //其他查询请求参数
        System.out.println("查询留言!");
        formBody = new FormBody.Builder()
                .add("rowId", rowID)
                .add("orderBy", orderBy)
                .add("theState", Integer.toString(theState))
                .add("pageNumber", nowpageNumber)
                .add("userName", mInfo.getUserName())
                .build();
        return formBody;
    }

    //组件的显隐藏控制
    private void viewController(int t) {
        btn_lookmyself.setVisibility(t);
        btn_looktoend.setVisibility(t);
        words_main_layout.setVisibility(t);
        textView_totlepage.setVisibility(t);
        editText_nowpagenumber.setVisibility(t);
    }

    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(WordsActivity.this, MainActivity.class);
        startActivity(intent);*/
        finish();
    }
}
