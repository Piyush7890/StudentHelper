package com.example.mamid.studenthelper;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerifyLogin extends AsyncTask<Void,Void,Void> {
    private String studentname="";
    private String json="";
    private List<Subject> subjectArrayList;
    String original;
    String totalpercent="";
    private static double sum;
   private boolean exception=false;
    String id;
    private loginfinished listener;
    String password;
    private  final String url="http://pict.ethdigitalcampus.com:80/DCWeb/authenticate.do";
    private String grade;
    private String division;

    public VerifyLogin(String id, String password) {
        this.id = id;
        this.password = password;
        subjectArrayList = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Exception e = new Exception();
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        try {
            makeRequest("http://pict.ethdigitalcampus.com:80/DCWeb/authenticate.do");
            Document doc = Jsoup.parse(makeRequest("http://pict.ethdigitalcampus.com/DCWeb/form/jsp_sms/StudentsPersonalFolder_pict.jsp?dashboard=1"));
            Elements rows = ( doc.select("table[id=table1]").get(0)).select("tr");
            int count = 0;
            for (int i = 2; i < rows.size() - 1; i++) {
                Elements cols = ( rows.get(i)).select("td");
                Subject current = new Subject();
                current.setSubjectname((cols.get(0)).text().toLowerCase());
                current.setTotal(Integer.parseInt((cols.get(1)).text().trim()));
                current.setPresent(Integer.parseInt(( cols.get(2)).text().trim()));
                if (current.getTotal() != 0 || current.getPresent() != 0) {
                    double percentile = ((double) Math.round(100.0d * ((Double.valueOf((double) current.getPresent()).doubleValue() / ((double) current.getTotal())) * 100.0d))) / 100.0d;
                    current.setAttendance(percentile);
                        sum += percentile;
                        count++;

                    this.subjectArrayList.add(current);
                }
            }
            totalpercent = Double.toString(((double) Math.round((sum / ((double) count)) * 100.0d)) / 100.0d);

            studentname = ( ( ( doc.select("table[id=table5]").get(0)).select("tr").get(4)).select("td").get(1)).text();
           grade = ( ( ( doc.select("table[id=table5]").get(0)).select("tr").get(8)).select("td").get(1)).text();
            division = ( ( ( doc.select("table[id=table5]").get(0)).select("tr").get(8)).select("td").get(3)).text();
            String image = ( ( ( doc.select("table[id=table5]").get(0)).select("tr").get(4)).select("td").get(1)).text();

            this.original = ( rows.get(rows.size() - 1)).select("td").text();
            Collections.sort(this.subjectArrayList);
            Type type = new TypeToken<List<Subject>>() {}.getType();
            json = new Gson().toJson(subjectArrayList,type);
        } catch (IOException | IndexOutOfBoundsException e2) {
            e = e2;
            this.exception = true;

        }
        e.printStackTrace();
        return null;
    }

    private String makeRequest(String paramString) throws IOException {
        this.exception = false;
        sum = 0.0d;
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(paramString).openConnection();
        httpURLConnection.setRequestMethod("POST");
        String localObject = "loginid=" + id + "&password=" + password+ "&dbConnVar=PICT&service_id=";
        httpURLConnection.setDoOutput(true);
        new DataOutputStream(httpURLConnection.getOutputStream()).writeBytes(localObject);
        BufferedReader localObject1 = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String test = new String();
        while (true) {
            String str = localObject1.readLine();
            if (str == null) {
                return test;
            }
            test = test.concat("\n" + str.trim());
        }
    }




    @Override
    protected void onPostExecute(Void s) {

        if(listener!=null)
        {
            if(exception)
                listener.loginFailed();
            else listener.loginSuccess(json,studentname,totalpercent,grade,division);
        }

    }

    void setListener(loginfinished listener)
    {
        this.listener = listener;
    }

    interface loginfinished
    {
        void loginFailed();
        void loginSuccess(String json,String studentname, String totalpercent, String grade, String division);
    }
}
