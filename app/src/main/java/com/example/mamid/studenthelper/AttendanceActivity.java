package com.example.mamid.studenthelper;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mamid.studenthelper.Utils.CustomArc;
import com.example.mamid.studenthelper.Utils.FabTransform;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttendanceActivity extends AppCompatActivity implements VerifyLogin.loginfinished {
    //@BindView(R.id.swiperefresh)
   // SwipeRefreshLayout refresh = new SwipeRefreshLayout(this);
    @BindView(R.id.subjects_rview)
    RecyclerView rview;
    @BindView(R.id.parent)
    RelativeLayout parent;
     static final int LOGGEDIN =100 ;
     AttendanceAdapter adapter;
    @BindView(R.id.notloggedin_container)
    LinearLayout container;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.login_button)
    FloatingActionButton login;
    View header;
    String totalpercent;
    String grade;
    String division;
    String id;
    String name;
    public static  final String SHARED_PREF = "SHARED_PREFS";
    SharedPreferences prefs;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attendance_menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        ButterKnife.bind(this);
//        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if(getSharedPreferences(SHARED_PREF,MODE_PRIVATE).getBoolean("isLoggedIn",false)) {
//                    VerifyLogin verifyLogin = new VerifyLogin(getprefs().getString("LoginId",""),getprefs().getString("password",""));
//                    verifyLogin.setListener(AttendanceActivity.this);
//                    refresh.setRefreshing(true);
//
//                }
//
//                }
//        });
        SharedPreferences pref = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
totalpercent = pref.getString("Total","");
grade = pref.getString("grade","");
division = pref.getString("division","");
id = pref.getString("LoginId","");
        adapter = new AttendanceAdapter(getResources().getIntArray(R.array.colors),this,rview);
        rview.setAdapter(adapter);
        rview.setLayoutManager(new LinearLayoutManager(this));

        loadData();


        setSupportActionBar(toolbar);
        login = findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AttendanceActivity.this, LoginActivity.class);
                FabTransform.addExtras(i, ContextCompat.getColor(AttendanceActivity.this,R.color.colorPrimary),R.drawable.ic_add_black_24dp);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AttendanceActivity.this,login,"SharedDialog");
                startActivityForResult(i, LOGGEDIN, options.toBundle());
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        String percent;
        if(totalpercent.equals(""))
            percent=String.valueOf(0);
        else
            percent = String.valueOf(Math.round(Float.valueOf(totalpercent)));
        i.putExtra("attendance",percent);
        i.putExtra("id",getSharedPreferences(SHARED_PREF,MODE_PRIVATE).getString("LoginId",""));
        i.putExtra("name",getSharedPreferences(SHARED_PREF,MODE_PRIVATE).getString("Name","User"));
        setResult(RESULT_OK,i);
        finish();
    }

    private SharedPreferences getprefs() {
        return getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
            {
                updateValues();
                return true;
            }
            case R.id.refresh:
            {
                if(getSharedPreferences(SHARED_PREF,MODE_PRIVATE).getBoolean("isLoggedIn",false)) {
                    VerifyLogin verifyLogin = new VerifyLogin(getprefs().getString("LoginId",""),getprefs().getString("password",""));
                    verifyLogin.setListener(AttendanceActivity.this);
                    verifyLogin.execute();

                }
                return true;
            }
        }
        return false;
    }

    @SuppressLint("RestrictedApi")
    private void updateValues() {
        rview.setVisibility(View.GONE);
        adapter.removeData();
        container.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);
        totalpercent="";
        SharedPreferences preferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putString("LoginId","");
        editor.putString("Name","");
        editor.apply();
    }

    private void inflateHeader() {
        header=getLayoutInflater().inflate(R.layout.attendance_header_item,rview,false);
        TextView studname = header.findViewById(R.id.name_student);
        TextView percent_num = header.findViewById(R.id.percent_num);
        percent_num.setText(totalpercent);
studname.setText(getSharedPreferences(SHARED_PREF,MODE_PRIVATE).getString("Name", "User"));
        CustomArc arc = header.findViewById(R.id.percent);
        float percenttmp = totalpercent.equals("") ?0:Float.valueOf(totalpercent)/100;
        CardView cview = header.findViewById(R.id.header_cview);
        cview.setCardBackgroundColor(percenttmp>0.75?ContextCompat.getColor(this,R.color.md_green_400):ContextCompat.getColor(this, R.color.md_red_400));
        TextView grade_text = header.findViewById(R.id.grade);
        grade_text.setText(grade);
        grade_text = header.findViewById(R.id.division);
        grade_text.setText(String.format("Division %s", division));
        arc.setProgress(percenttmp);
        adapter.addHeader(header);
    }

    @SuppressLint("RestrictedApi")
    private void loadData() {
        prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            inflateHeader();
            container.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            String attendance = prefs.getString("AttendanceArray", null);
            if (attendance != null) {

                List<Subject> subjectList = (List) new Gson().fromJson(attendance, new TypeToken<List<Subject>>() {
                }.getType());
                adapter.addData(subjectList);
                rview.setVisibility(View.VISIBLE);
            }

        }
    }




    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOGGEDIN)
        {
            if(resultCode==RESULT_OK)
            {

                container.setVisibility(View.GONE);
                login.setVisibility(View.GONE);
                SharedPreferences prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                List<Subject> subjectList = (List)new Gson().fromJson(data.getStringExtra("AttendanceArray"), new TypeToken<List<Subject>>() {
                }.getType());

                totalpercent = data.getStringExtra("Total");
                editor.putString("grade",data.getStringExtra("grade"));
                editor.putString("division",data.getStringExtra("division"));
                this.division=data.getStringExtra("division");
                this.grade=data.getStringExtra("grade");
                editor.putString("Total",totalpercent );
                editor.putBoolean("isLoggedIn",true);
                editor.putString("LoginId",data.getStringExtra("ID"));
                editor.putString("password",data.getStringExtra("password"));
                editor.putString("AttendanceArray",data.getStringExtra("AttendanceArray"));
                editor.putString("Name",data.getStringExtra("Name"));
                editor.apply();
                inflateHeader();
                adapter.addData(subjectList);
                rview.setVisibility(View.VISIBLE);

            }
        }
    }

    private float dptopx(float value)
    {
        return getResources().getDisplayMetrics().density*value;
    }

    @Override
    public void loginFailed() {
//        refresh.setRefreshing(false);
      Snackbar.make(parent,"Error updating attendance", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void loginSuccess(String json, String studentname, String totalpercent, String grade, String division) {
      //  refresh.setRefreshing(false);
        Snackbar.make(parent,"Successfully Updated", Snackbar.LENGTH_LONG);
        List<Subject> subjectList = (List)new Gson().fromJson(json, new TypeToken<List<Subject>>() {
        }.getType());
        adapter.clear();
        SharedPreferences.Editor editor= getprefs().edit();
        editor.putString("AttendanceArray",json);
        editor.putString("Total",totalpercent );
        editor.apply();
        inflateHeader();
        adapter.addData(subjectList);

    }
}
