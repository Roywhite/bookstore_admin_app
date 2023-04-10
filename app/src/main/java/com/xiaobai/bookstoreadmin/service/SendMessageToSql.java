package com.xiaobai.bookstoreadmin.service;

import com.xiaobai.bookstoreadmin.util.HttpURLConnectionUtil;
import com.xiaobai.bookstoreadmin.util.PropertiesUtil;

import org.json.JSONObject;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SendMessageToSql {
    HttpURLConnection conn = null;
    URL url = null;
    PropertiesUtil propUtil;
    HttpURLConnectionUtil httpconnect = new HttpURLConnectionUtil();
    List<String> a;
    List<String> aJsonName;

    //登录连接
    public HttpURLConnection LoginConnection(String mName, String mPasswd) {
        a = new ArrayList<>();
        aJsonName = new ArrayList<>();
        a.add(mName);
        a.add(mPasswd);
        aJsonName.add("adminAccount");
        aJsonName.add("adminPassword");
        conn = fix(conn, url, "bookadmin", "loginAdmin", a, aJsonName);
        return conn;
    }

    //获取密码的加密解密秘钥
    public HttpURLConnection getSec() {
        conn = fix(conn, url, "booksec", "getSec");
        return conn;
    }

    //发送数据
    public HttpURLConnection sendAesText(String name,String password,String aesText){
        a = new ArrayList<>();
        aJsonName = new ArrayList<>();
        a.add(name);
        a.add(password);
        a.add(aesText);
        aJsonName.add("name");
        aJsonName.add("password");
        aJsonName.add("aesText");
        conn = fix(conn, url, "bookadmin", "rentAndReturnBookLocal", a, aJsonName);
        return conn;
    }

    /**
     * 解决代码复用
     * @param conn
     * @param url
     * @param serverName
     * @param serverFunc
     * @return
     */
    public HttpURLConnection fix(HttpURLConnection conn, URL url, String serverName, String serverFunc) {
        try {
            url = new URL("http://" + propUtil.getProperties("url") + ":" + propUtil.getProperties("post") + "/" + serverName + "/" + serverFunc);
            conn = httpconnect.httpconnection(conn, url);
            OutputStream os = null;
            //判断有没有连接上服务器，如果连接上继续，没有连接上赋空
            try {
                os = conn.getOutputStream();
            } catch (Exception e) {
                conn = null;
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }


    /**
     * 解决代码复用问题，完成代码封装(有传值到后台的情况)
     *
     * @param conn       连接
     * @param url        地址
     * @param serverName 服务器名
     * @param serverFunc 服务方法
     * @param a          需要传的值
     * @param aJsonName  Json取值标志
     * @return
     */
    public HttpURLConnection fix(HttpURLConnection conn, URL url, String serverName, String serverFunc, List<String> a, List<String> aJsonName) {
        try {
            url = new URL("http://" + propUtil.getProperties("url") + ":" + propUtil.getProperties("post") + "/" + serverName + "/" + serverFunc);
            conn = httpconnect.httpconnection(conn, url);
            OutputStream os = null;
            //判断有没有连接上服务器，如果连接上继续，没有连接上赋空
            try {
                os = conn.getOutputStream();
            } catch (Exception e) {
                conn = null;
                e.printStackTrace();
            }
            if (conn != null) {
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "utf-8"));

                /*封装子对象*/
                JSONObject ClientKey = new JSONObject();
                for (int i = 0; i < a.size(); i++) {
                    ClientKey.put(aJsonName.get(i), a.get(i));
                }

                /*把JSON数据转换成String类型使用输出流向服务器写*/
                String content = String.valueOf(ClientKey);
                pw.print(content);
                pw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
