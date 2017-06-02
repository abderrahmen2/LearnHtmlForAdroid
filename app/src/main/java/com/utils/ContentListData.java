package com.utils;

import com.entity.ContentInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装ContentInfo参数
 * 以便更好的显示知识模块内容
 * Created by Novice on 2017/5/2.
 */

public class ContentListData {
    /**
     * 只有一个标题和一个文本
     * @param title 标题
     * @param text1 文本1
     * @return
     */
    public static List<ContentInfo> getContentList2(String title, String text1){
        List<ContentInfo> contentList = new ArrayList<>();
        contentList.add(new ContentInfo(title));
        contentList.add(new ContentInfo(text1));

        return contentList;
    }

    /**
     *
     * @param title 标题
     * @param text1 文本1
     * @param html  实例代码
     * @param text2 文本2
     * @return
     */
    public static List<ContentInfo> getContentList4(String title, String text1, String html,String text2){
        List<ContentInfo> contentList = new ArrayList<>();
        contentList.add(new ContentInfo(title));
        contentList.add(new ContentInfo(text1));
        contentList.add(new ContentInfo(html));
        contentList.add(new ContentInfo(text2));

        return contentList;
    }

    /**
     *
     * @param title 标题
     * @param text1 文本1
     * @param html  实例代码
     * @param text2 文本2
     * @param text3 文本3
     * @return
     */
    public static List<ContentInfo> getContentList5(String title, String text1, String html,String text2,String text3){
        List<ContentInfo> contentList = new ArrayList<>();
        contentList.add(new ContentInfo(title));
        contentList.add(new ContentInfo(text1));
        contentList.add(new ContentInfo(html));
        contentList.add(new ContentInfo(text2));
        contentList.add(new ContentInfo(text3));

        return contentList;
    }

    /**
     *知识但闻文本，没有代码
     * @param title 标题
     * @param text1 文本1
     * @param text2 文本2
     * @return
     */
    public static List<ContentInfo> getContentListText3(String title, String text1, String text2){
        List<ContentInfo> contentList = new ArrayList<>();
        contentList.add(new ContentInfo(title));
        contentList.add(new ContentInfo(text1));
        contentList.add(new ContentInfo(text2));

        return contentList;
    }

}
