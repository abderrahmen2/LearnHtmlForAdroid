package com.learnhtml;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.utils.StaticData;

import java.io.Serializable;

public class BookActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_book);
        setTitle(getString(R.string.bookactivity_title));
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
