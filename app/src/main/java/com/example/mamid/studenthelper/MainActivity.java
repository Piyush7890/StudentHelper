package com.example.mamid.studenthelper;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
private static final int ATTENDANCE = 1001;
private static final int BALANCE = 1002;
    private static final int NOTES = 1003;

@BindView(R.id.remaining_balance)
TextView rem_balance;
@BindView(R.id.spent_amount)
TextView spent_bal;
@BindView(R.id.attendance)
TextView attendance;
@BindView(R.id.time_image)
ImageView time;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.greet)
    TextView greet;
    @BindView(R.id.sub_message)
    TextView sub_message;
    @BindView(R.id.apply)
    Button apply;
    @BindView(R.id.cview_intro)
    CardView intro;
    @BindView(R.id.username_float_label)
    TextInputLayout layout;
    @BindView(R.id.username)
    EditText edit_name;
    @BindView(R.id.male_cbox)
    CheckBox mcbox;
    @BindView(R.id.female_cbox)
    CheckBox fcbox;
    @BindView(R.id.attendance_cview)
    CardView cview;
    @BindView(R.id.notes_count)
    TextView notes_count;
@BindView(R.id.container)
FrameLayout container;
String day;
@BindView(R.id.toolbar)
Toolbar toolbar;
Boolean ismale=true;
FastOutSlowInInterpolator interpolator;
AccountHeader header;
Drawer drawer;
String id, name;
private static final String SHARED_PREF = "PERSONAL_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        interpolator = new FastOutSlowInInterpolator();
        SharedPreferences preferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        id=preferences.getString("LoginId","");
        name=preferences.getString("Name","");
        String namee = preferences.getString("name","User");
        rem_balance.setText(preferences.getString("rem_bal","0"));
        notes_count.setText(preferences.getString("notes_count","0"));
        spent_bal.setText(preferences.getString("spent_bal","0"));
        attendance.setText(String.format("%s %%", preferences.getString("attendance", "0")));
        edit_name.setText(namee);
        greet.setText(String.format("%s %s !", getTimeOfTheDay(), namee));
        setAvatar(preferences.getBoolean("ismale",true));
        if(preferences.getBoolean("ismale",true))
            mcbox.setChecked(true);
        else fcbox.setChecked(true);
        setSupportActionBar(toolbar);

        initDrawer();
        header.addProfiles(new ProfileDrawerItem().withName(name).withIcon("http://pict.ethdigitalcampus.com/container/school_data/PICT/photo/Student/"+id+".jpg").withIdentifier(100));
        intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator translate = ObjectAnimator.ofFloat(avatar,"translationX",intro.getWidth()/2-dptopx(32));
                translate.setDuration(450L);
                translate.setInterpolator(interpolator);
                translate.setStartDelay(300L);
                translate.start();
                TransitionManager.beginDelayedTransition(container,new AutoTransition());
                intro.setElevation(dptopx(6));
                greet.setVisibility(View.INVISIBLE);
                sub_message.setVisibility(View.INVISIBLE);
                time.setVisibility(View.INVISIBLE);
                layout.setVisibility(View.VISIBLE);
                mcbox.setVisibility(View.VISIBLE);
                fcbox.setVisibility(View.VISIBLE);
                apply.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initDrawer() {
        header = new AccountHeaderBuilder().withActivity(this).withHeaderBackground(R.drawable.placeholder).build();
Glide.with(this).load(day).apply(new RequestOptions().centerCrop().placeholder(R.drawable.placeholder)).transition(DrawableTransitionOptions.withCrossFade()).into(header.getHeaderBackgroundView());
        drawer= new DrawerBuilder().withActivity(this).withToolbar(toolbar).withSelectedItem(-1).withAccountHeader(header).addDrawerItems(new PrimaryDrawerItem().withName("Attendance").withIdentifier(1),new PrimaryDrawerItem().withName("Notes").withIdentifier(2), new PrimaryDrawerItem().withName("Expenses").withIdentifier(3), new PrimaryDrawerItem().withName("About").withIdentifier(4)).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if(drawerItem.getIdentifier()==1) {
                        Intent i = new Intent(MainActivity.this, AttendanceActivity.class);
                        startActivityForResult(i, ATTENDANCE);

                    }
                    else if(drawerItem.getIdentifier()==2) {
                    startActivity(new Intent(MainActivity.this, NotesActivity.class));
                }

                else if(drawerItem.getIdentifier()==3)
                {
                        Intent i = new Intent(MainActivity.this, BalanceActivity.class);
                        startActivity(i);
                    }
                    else if(drawerItem.getIdentifier()==4)
                {
                    startActivity(new Intent(MainActivity.this, AboutActivity.class),ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                }

                return false;
            }
        }).build();


    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREF,MODE_PRIVATE).edit();
        editor.putString("rem_bal",rem_balance.getText().toString());
        editor.putString("spent_bal",spent_bal.getText().toString());
        editor.apply();
        super.onBackPressed();
    }

    private void setAvatar(boolean isMale) {
        if(isMale)
            avatar.setBackgroundResource(R.drawable.ic_person_flat);
        else
            avatar.setBackgroundResource(R.drawable.person_girl_flat);
    }

    public void onAttendanceClick(View view)
    {
       // ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,cview,"background");
        Intent i = new Intent(MainActivity.this,AttendanceActivity.class);
        startActivityForResult(i,ATTENDANCE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==ATTENDANCE)
        {
            if(resultCode==RESULT_OK)
            {
                if(data.getStringExtra("attendance")!=null)
                {
                    String namme=data.getStringExtra("name");
                    String text=data.getStringExtra("attendance") ;
                attendance.setText(String.format("%s %%", text));
                SharedPreferences prefs = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
                SharedPreferences.Editor eidotr  = prefs.edit();
                eidotr.putString("LoginId",data.getStringExtra("id"));
                eidotr.putString("Name",namme);
                header.removeProfileByIdentifier(100);
                header.addProfiles(new ProfileDrawerItem()
                        .withName(namme.equals("") ?"User":namme)
                        .withIcon("http://pict.ethdigitalcampus.com/container/school_data/PICT/photo/Student/"+data.getStringExtra("id")+".jpg")
                        .withIdentifier(100));

                eidotr.putString("attendance",text);
                eidotr.apply();
            }
        }}
        if(requestCode==BALANCE && resultCode==RESULT_OK)
        {
            rem_balance.setText(String.format("₹ %s", String.valueOf(data.getFloatExtra("total", 0) - data.getFloatExtra("spent", 0))));
            spent_bal.setText(String.format("₹ %s", String.valueOf(data.getFloatExtra("spent", 0))));
        }
        if(requestCode==NOTES && resultCode==RESULT_OK)
        {
            String count = String.valueOf(data.getLongExtra("count",0));
            Log.d("COUNTMAIN",count);

            notes_count.setText(count);
            getSharedPreferences(SHARED_PREF,MODE_PRIVATE).edit().putString("notes_count",count).apply();
        }
    }

    public void onBalanceClick(View view)
    {
        Intent i = new Intent(MainActivity.this,BalanceActivity.class);
        startActivityForResult(i,BALANCE);
    }

    public void onNotesClick(View view)
    {
        startActivityForResult(new Intent(MainActivity.this, NotesActivity.class),NOTES);
    }

    private String getTimeOfTheDay() {
        String message = "Good day";
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        String[] images = new String[]{};
        if (timeOfDay >= 0 && timeOfDay < 12) {
            message = "Good morning";
            time.setBackgroundResource(R.drawable.clear_day);
            images = getResources().getStringArray(R.array.morning);
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            time.setBackgroundResource(R.drawable.mostly_cloudy);

            message = "Good Afternoon";
            images = getResources().getStringArray(R.array.after_noon);
        } else if (timeOfDay >= 16 && timeOfDay < 20) {
            time.setBackgroundResource(R.drawable.partly_cloudy_night);

            message = "Good Evening";
            images = getResources().getStringArray(R.array.evening);
        } else if (timeOfDay >= 20 && timeOfDay < 24) {
            time.setBackgroundResource(R.drawable.clear_night);

            message = "Good Night";
            images = getResources().getStringArray(R.array.night);
        }
        String day = images[new Random().nextInt(images.length)];
        loadTimeImage(day);
        return message;
    }
    private float dptopx(float value)
    {
        return getResources().getDisplayMetrics().density*value;
    }

    public void mcheck(View view)
    {
        fcbox.setChecked(false);
        avatar.setBackgroundResource(R.drawable.ic_person_flat);

        ismale=true;

    }

    public  void fcheck(View view)
    {
        avatar.setBackgroundResource(R.drawable.person_girl_flat);

        mcbox.setChecked(false);
        ismale=false;
    }

    public void applyChanges(View view)
    {
        String pname = edit_name.getText().toString();
        greet.setText(String.format("%s %s !", getTimeOfTheDay(), name));
       setAvatar(ismale);
        SharedPreferences preferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("ismale",ismale);
        editor.putString("name",pname);
        editor.apply();

        ObjectAnimator translate = ObjectAnimator.ofFloat(avatar,"translationX",0);
        translate.setDuration(450L);
        translate.setInterpolator(interpolator);
        translate.setStartDelay(300L);
        translate.start();
        TransitionManager.beginDelayedTransition(container,new AutoTransition());
        intro.setElevation(dptopx(2));
        greet.setVisibility(View.VISIBLE);
        sub_message.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        mcbox.setVisibility(View.GONE);
        fcbox.setVisibility(View.GONE);
        apply.setVisibility(View.GONE);
    }

    private void loadTimeImage(String day) {
        this.day=day;
    }
}
