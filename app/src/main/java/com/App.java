package com;

import android.app.Application;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * 友盟社会化分享时用到这个类
 * Created by HUANG on 2017/6/2.
 */

public class App extends Application {

    {
        //此处的参数是腾讯开发者平台为开发者提供的ID和KEY
        PlatformConfig.setQQZone("1106122455", "oF9VTvqMM2AuBwZe");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        UMShareAPI.get(this);
        Config.DEBUG = true;
    }
}
