package com.example.mamid.studenthelper;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mamid.studenthelper.Model.DaoMaster;
import com.example.mamid.studenthelper.Model.DaoSession;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

public class StudentHelper extends Application {
private DaoSession mDaoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mDaoSession = new DaoMaster(
                new DaoMaster.DevOpenHelper(this, "payments.db").getWritableDb()).newSession();

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext()).load(uri).apply(new RequestOptions().placeholder(placeholder).centerCrop()).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.with(imageView.getContext()).clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                    return new ColorDrawable(Color.parseColor("#212121"));
            }
        });

    }



    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}
