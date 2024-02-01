package com.github.tvbox.osc.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.concurrent.Executors;

public class DocumentUtil {

    private static final String URL = "https://hoheiya9527.github.io/";

    public static void getTvboxUrl(CallBack callBack) {
        getUrl("tvbox", callBack);
    }

    public static void getAppUpdateUrl(CallBack callBack) {
        getUrl("appUpdate", callBack);
    }

    public static void getUrl(String tag, CallBack callBack) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup
                            .connect(URL)
                            .timeout(10 * 1000)
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:49.0) Gecko/20100101 Firefox/49.0")
                            .header("Connection", "close")
                            .get();
//                  System.out.println(document);
                    Elements elements = document.select("div#" + tag);
                    if (elements.size() == 0) {
                        System.out.println(tag + " url is Empty");
                        callBack.over("");
                    } else {
                        String text = elements.get(0).text();
                        System.out.println("==getUrl:" + text);
                        callBack.over(text);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface CallBack {
        void over(String result);
    }

}
