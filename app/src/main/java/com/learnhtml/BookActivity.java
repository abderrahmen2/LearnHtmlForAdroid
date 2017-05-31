package com.learnhtml;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.entity.BookInfo;
import com.utils.StaticData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookActivity extends AppCompatActivity {

    private ListView book_listview;
    private List<BookInfo> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Serializable lg = intent.getSerializableExtra("light");
        if (lg == null) {
            setTheme(R.style.AppTheme_Light_White);
        }
        //点了日间模式
        else if (lg.toString().equals(getString(R.string.mainactivitu_actionbar_white))) {
            setTheme(R.style.AppTheme_Light_Black);
        }
        //点了夜间模式
        else if (lg.toString().equals(getString(R.string.mainactivitu_actionbar_black))) {
            setTheme(R.style.AppTheme_Light_White);
        }

        setContentView(R.layout.activity_book);
        initView();
        setTitle(getString(R.string.bookactivity_title));

        setBookToListView();
    }

    private void initView() {
        book_listview = (ListView) findViewById(R.id.book_listview);

        bookList = new ArrayList<>();
        bookList.add(new BookInfo(1, getString(R.string.bookactivity_book1_name), getString(R.string.bookactivity_book1_details), StaticData.BOOK1_URL));
        bookList.add(new BookInfo(2, getString(R.string.bookactivity_book2_name), getString(R.string.bookactivity_book2_details), StaticData.BOOK2_URL));
        bookList.add(new BookInfo(3, getString(R.string.bookactivity_book3_name), getString(R.string.bookactivity_book3_details), StaticData.BOOK3_URL));
        bookList.add(new BookInfo(4, getString(R.string.bookactivity_book4_name), getString(R.string.bookactivity_book4_details), StaticData.BOOK4_URL));
        bookList.add(new BookInfo(5, getString(R.string.bookactivity_book5_name), getString(R.string.bookactivity_book5_details), StaticData.BOOK5_URL));
        bookList.add(new BookInfo(6, getString(R.string.bookactivity_book6_name), getString(R.string.bookactivity_book6_details), StaticData.BOOK6_URL));
        bookList.add(new BookInfo(7, getString(R.string.bookactivity_book7_name), getString(R.string.bookactivity_book7_details), StaticData.BOOK7_URL));
        bookList.add(new BookInfo(8, getString(R.string.bookactivity_book8_name), getString(R.string.bookactivity_book8_details), StaticData.BOOK8_URL));
        bookList.add(new BookInfo(9, getString(R.string.bookactivity_book9_name), getString(R.string.bookactivity_book9_details), StaticData.BOOK9_URL));

        book_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                detailsDialog(i);
            }
        });
    }

    //填充ListView
    private void setBookToListView() {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (BookInfo bookInfo : bookList) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            switch (bookInfo.getNo()) {
                case 1:
                    listItem.put("img", R.drawable.fengkuanghtml);
                    break;
                case 2:
                    listItem.put("img", R.drawable.rumendaojingtong);
                    break;
                case 3:
                    listItem.put("img", R.drawable.jichuzhishi);
                    break;
                case 4:
                    listItem.put("img", R.drawable.kaifajishu);
                    break;
                case 5:
                    listItem.put("img", R.drawable.wangyezhizuo);
                    break;
                case 6:
                    listItem.put("img", R.drawable.quanweizhinan);
                    break;
                case 7:
                    listItem.put("img", R.drawable.donghuazhizuo);
                    break;
                case 8:
                    listItem.put("img", R.drawable.yidongapp);
                    break;
                case 9:
                    listItem.put("img", R.drawable.youxikaifa);
                    break;
            }

            listItem.put("title", "书名:"+bookInfo.getName());
            listItem.put("text", bookInfo.getDetails());
            listItems.add(listItem);
        }

        //定义特定的适配器
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.listview_bookmain,
                new String[]{"img", "title", "text"},
                new int[]{R.id.listview_bookmain_img, R.id.listview_bookmain_title, R.id.listview_bookmain_text});

        //为ListView添加是配置
        book_listview.setAdapter(simpleAdapter);
    }

    //显示书籍详情弹窗
    private void detailsDialog(final int i) {
        LinearLayout mainView = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_book_details, null);
        TextView text = (TextView) mainView.findViewById(R.id.dialog_bookdetails_text);
        text.setText(bookList.get(i).getDetails());
        new AlertDialog.Builder(this)
                .setTitle(bookList.get(i).getName())
                .setView(mainView)
                .setPositiveButton("去购买", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(bookList.get(i).getBuyUrl());
                        intent.setData(content_url);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create().show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.applogo)
                .setTitle(R.string.mainactivity_dialog_tps)
                .setMessage(R.string.bookactivity_dialog_message)
                .setPositiveButton(R.string.bookactivity_dialog_gonow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                })
                .setNegativeButton(R.string.bookactivity_dialog_goletter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

}
