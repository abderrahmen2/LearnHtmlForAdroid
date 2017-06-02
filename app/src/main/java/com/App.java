package com;

import android.app.Application;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by HUANG on 2017/6/2.
 */

public class App extends Application {

    {
        PlatformConfig.setQQZone("1106122455", "oF9VTvqMM2AuBwZe");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        UMShareAPI.get(this);
        Config.DEBUG = true;
    }
}
