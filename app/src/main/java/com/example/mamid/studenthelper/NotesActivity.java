package com.example.mamid.studenthelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.mamid.studenthelper.Model.DaoSession;
import com.example.mamid.studenthelper.Model.Note;
import com.example.mamid.studenthelper.Model.NoteDao;
import com.example.mamid.studenthelper.Utils.GravityArcMotion;
import com.example.mamid.studenthelper.Utils.ItemOffsetDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesActivity extends AppCompatActivity implements NotesAdapter.clickListener {
    NotesAdapter adapter;
    private static final int NOTES_ADD = 1000;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
@BindView(R.id.add_btn)
    FloatingActionButton fab;
    @BindView(R.id.fab_expand)
    FrameLayout fabExpand;
    @BindView(R.id.container)
    LinearLayout no_notes;
    @BindView(R.id.notelist)
    RecyclerView notelist;
    DaoSession session;
    NoteDao notedb;
    @BindView(R.id.parent)
    CoordinatorLayout parent;
    private boolean toggle=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        ButterKnife.bind(this);
        session =((StudentHelper)getApplication()).getDaoSession();
        notedb=session.getNoteDao();
        setSupportActionBar(toolbar);
adapter = new NotesAdapter();
        adapter.setListener(this);
        notelist.setAdapter(adapter);
        notelist.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(NotesActivity.this, R.dimen.item_offset);
        notelist.addItemDecoration(itemDecoration);
List<Note> list = notedb.loadAll();
if(list!=null && !list.isEmpty())
{
    adapter.addData(list);
    no_notes.setVisibility(View.GONE);
    notelist.setVisibility(View.VISIBLE);
}

        if((Build.VERSION.SDK_INT>=Build.VERSION_CODES.M))
        {
            getWindow().setStatusBarColor(Color.parseColor("#f0f0f0"));

            int flags = toolbar.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            toolbar.setSystemUiVisibility(flags);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doFabExpand();
                ActivityOptions op = ActivityOptions.makeSceneTransitionAnimation(NotesActivity.this);
                startActivityForResult(new Intent(NotesActivity.this,NotesDetailActivity.class),NOTES_ADD,op.toBundle());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
getMenuInflater().inflate(R.menu.note_menu,menu);
    return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.layout:
            {
                toggleIcon(item);
                adapter.setGrid();

                notelist.setAdapter(adapter);

                return true;
            }
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }


        }
        return false;
    }

    private void toggleIcon(MenuItem item) {
        if(toggle) {

            notelist.setLayoutManager(new LinearLayoutManager(this));

            item.setIcon(R.drawable.ic_dashboard_black_24dp);
        }
            else {
            notelist.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));

            item.setIcon(R.drawable.ic_view_stream_black_24dp);

        }
        toggle = !toggle;

    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setAlpha(1f);
        fabExpand.setVisibility(View.INVISIBLE);

    }

    private void doFabExpand() {
        int fabCenterX = (fab.getLeft() + fab.getRight()) / 2;
        int fabCenterY = ((fab.getTop() + fab.getBottom()) / 2) - fabExpand.getTop();
        int translateX = fabCenterX - (fabExpand.getWidth() / 2);
        int translateY = fabCenterY - (fabExpand.getHeight() / 2);
        fabExpand.setTranslationX(translateX);
        fabExpand.setTranslationY(translateY);

        fabExpand.setVisibility(View.VISIBLE);
        Animator reveal = ViewAnimationUtils.createCircularReveal(
                fabExpand,
                fabExpand.getWidth() / 2,
                fabExpand.getHeight() / 2,
                fab.getWidth() / 2,
                (int) Math.hypot(fabExpand.getWidth() / 2, fabExpand.getHeight() / 2))
                .setDuration(360);

        GravityArcMotion arcMotion = new GravityArcMotion();
        arcMotion.setMinimumVerticalAngle(70f);
        Path motionPath = arcMotion.getPath(translateX, translateY, 0, 0);
        Animator position = ObjectAnimator.ofFloat(fabExpand, View.TRANSLATION_X, View
                .TRANSLATION_Y, motionPath)
                .setDuration(360);

        Animator background = ObjectAnimator.ofArgb(fabExpand,
                "backgroundColor",
                Color.parseColor("#33b5ff"),
                Color.TRANSPARENT)
                .setDuration(360);
//
        Animator fadeOutFab = ObjectAnimator.ofFloat(fab, View.ALPHA, 0f)
                .setDuration(60);

        AnimatorSet show = new AnimatorSet();
        show.setInterpolator(new FastOutSlowInInterpolator());
        show.playTogether(reveal, background, position, fadeOutFab);
        show.start();
        show.addListener(new AnimatorListenerAdapter() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onAnimationEnd(Animator animation) {
                fab.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==NOTES_ADD)
        {
            if(resultCode==RESULT_OK)
            {
                adapter.deleteAll();
                adapter.addData(notedb.loadAll());
                no_notes.setVisibility(View.GONE);
                notelist.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(final Long id, int pos) {

        final Note note = notedb.load(id);

        if(note.getIsLocked())
        {
            new MaterialDialog.Builder(NotesActivity.this)
                    .title("Enter Password")
                    .inputRangeRes(0, 16, R.color.md_red_500).negativeText("CANCEL")
                    .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD).input("Pasword", "", new MaterialDialog.InputCallback() {
                @Override
                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                    if(input.toString().equals(note.getPassword()))
                    {
                        Intent i = new Intent(NotesActivity.this, NotesDetailActivity.class);
                        i.putExtra("ID", id);

                        startActivityForResult(i, NOTES_ADD);

                    }
                    else {
                        Log.d("Password",note.getPassword());

                        Snackbar.make(parent,"Incorrect password",Snackbar.LENGTH_LONG).show();
                    }
                }
            }).show();
        }
        else {

            Intent i = new Intent(NotesActivity.this, NotesDetailActivity.class);
            i.putExtra("ID", id);
            startActivityForResult(i, NOTES_ADD);

        }
        }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        Log.d("COUNTNOTES",String.valueOf(notedb.count()));
        i.putExtra("count" , notedb.count());
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    public void onLongClick(final Long id, final int pos) {
        int color = getResources().getColor(R.color.colorPrimary);
        new MaterialDialog.Builder(NotesActivity.this).title("Are you sure?").content("Are you sure you want to delete this note?").positiveText("DELETE").negativeText("CANCEL").onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                notedb.deleteByKey(id);
                adapter.deleteItem(pos);
                if(notedb.loadAll().isEmpty())
                {
                    notelist.setVisibility(View.GONE);
                    no_notes.setVisibility(View.VISIBLE);
                }
            }
        }).positiveColor(color).negativeColor(color).show();
    }
}
