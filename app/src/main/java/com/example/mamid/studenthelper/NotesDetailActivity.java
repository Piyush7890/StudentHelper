package com.example.mamid.studenthelper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import android.text.format.DateUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.divyanshu.draw.activity.DrawingActivity;
import com.example.mamid.studenthelper.Model.Attachment;
import com.example.mamid.studenthelper.Model.DaoSession;
import com.example.mamid.studenthelper.Model.Note;
import com.example.mamid.studenthelper.Model.NoteDao;
import com.example.mamid.studenthelper.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesDetailActivity extends AppCompatActivity implements AttachmentTask.AttachmentListener, ColorChooserDialog.ColorCallback {
    private static final int READ_EXTERNAL = 100;
int[] colors;
  @BindView(R.id.title)
    EditText title_et;
  @BindView(R.id.content)
  EditText content_et;
    @BindView(R.id.time_text)
    TextView timetextview;
@BindView(R.id.parent)
    RelativeLayout parent;
@BindView(R.id.toolbar)
    Toolbar toolbar;
Note notetmp=new Note();
String filename;
@BindView(R.id.gridview)
    RecyclerView grid;
@BindView(R.id.attachment)
    ImageView attach;
@BindView(R.id.color)
        ImageView color_btn;
@BindView(R.id.time_edited)
        TextView time_text;
AttachmentAdapter adapter;
    int chosen_colour;
    Note oldnote = new Note();
    boolean isLocked=false;

List<Attachment> attachmentlist = new ArrayList<>();
    private static final int PHOTO = 1;
    private static final int CATEGORY = 5;
    private static final int DETAIL = 6;
    private static final int REQUEST_FILE = 4;
    private Uri attachmentUri;
    private int READ_LOCATION = 101;
    private static final int REQUEST_CODE_DRAW = 102;
    private static final String PREFS = "ADD_NOTE_PREFS";
    NoteDao notedb;
    DaoSession session;
    private boolean isold=false;
    private long id=0;
    private boolean save=false;
    private String password="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        ButterKnife.bind(this);
        session =((StudentHelper)getApplication()).getDaoSession();
        notedb=session.getNoteDao();
        chosen_colour=getResources().getColor(R.color.c1);
        Drawable d =TextDrawable.builder().beginConfig().withBorder(4).endConfig().buildRound("",chosen_colour);
        color_btn.setImageDrawable(d);
        colors = getResources().getIntArray(R.array.note_colors);
        setSupportActionBar(toolbar);
        time_text.setText(String.format("Edited : %s", DateUtils.getRelativeDateTimeString(this, System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS,DateUtils.WEEK_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL)));

        adapter = new AttachmentAdapter();
        grid.setAdapter(adapter);
        grid.setLayoutManager(new GridLayoutManager(this, 2));
        if((Build.VERSION.SDK_INT>=Build.VERSION_CODES.M))
        {
            getWindow().setStatusBarColor(Color.parseColor("#f0f0f0"));

            int flags = toolbar.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            toolbar.setSystemUiVisibility(flags);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View layout = getLayoutInflater()
                        .inflate(R.layout.menu_attachment,null);
                new MaterialDialog
                        .Builder(NotesDetailActivity.this)
                        .customView(layout, false).show();
                TextView camera = layout.findViewById(R.id.camera_attachment_menu);
                TextView files = layout.findViewById(R.id.file_attachment_menu);
                TextView time = layout.findViewById(R.id.time_attachment_menu);
                TextView sketch = layout.findViewById(R.id.sketch_attachment_menu);

                sketch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NotesDetailActivity.this, DrawingActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_DRAW);
                    }
                });

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (!Utils.isAvailable(NotesDetailActivity.this, intent, new String[]{PackageManager.FEATURE_CAMERA})) {
                            Snackbar.make(parent,"Feature not available", Snackbar.LENGTH_LONG).show();

                            return;
                        }File f = Utils.createNewAttachmentFile(NotesDetailActivity.this, Utils.MIME_TYPE_IMAGE_EXT);
                        if (f == null) {
                            Snackbar.make(parent,"Error", Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        attachmentUri = Uri.fromFile(f);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
                        startActivityForResult(intent, PHOTO);
                    }
                });

                files.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(ContextCompat.checkSelfPermission(NotesDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                        {
                            openFilesChooser();
                        }
                        else
                        {
                            ActivityCompat.requestPermissions(NotesDetailActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL);

                        }
                    }});

                time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                        timetextview.setText(dateFormat);
                        timetextview.setVisibility(View.VISIBLE);

                    }
                });

            }
        });

        color_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorChooserDialog.Builder((Context)NotesDetailActivity.this,R.string.color_choose).customColors(colors,null).preselect(getResources().getColor(R.color.c1)).accentMode(false).allowUserColorInputAlpha(false).show(NotesDetailActivity.this);
            }
        });

        Intent i = getIntent();
        if(i.hasExtra("ID")) {
            isold=true;
             id = i.getLongExtra("ID", -1);

            Note note = notedb.load(id);
            if(note.getTitle()!=null)
            title_et.setText(note.getTitle());
            content_et.setText(note.getContent());
            chosen_colour=note.getColor();
            isLocked=note.getIsLocked();
            time_text.setText(String.format("Edited : %s", DateUtils.getRelativeDateTimeString(this, note.getMillis(),DateUtils.MINUTE_IN_MILLIS,DateUtils.WEEK_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL)));
            parent.setBackgroundColor(chosen_colour);
            List<Attachment> list = new Gson().fromJson(note.getAttachmentjson(),new TypeToken<List<Attachment>>() {
            }.getType());
            if(list!=null) {
                adapter.addData(list);
                oldnote=note;
                attachmentlist=list;
            grid.setVisibility(View.VISIBLE);
            }
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.notes_detail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.save:
            {
                save=true;
                onBackPressed();
                return true;
            }

            case android.R.id.home:
            {
                super.onBackPressed();
                return true;
            }

            case R.id.lock:
            {
                if(!isLocked)
                {
                    new MaterialDialog.Builder(NotesDetailActivity.this)
                            .title("Set Password")
                            .inputRangeRes(0, 16, R.color.md_red_500)
                            .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD).input("Password", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            isLocked=true;
                            if(!input.toString().equals(""))
                            {
                                isLocked=true;
                                password = input.toString();

                            }
                        }
                    }).positiveText("OK").negativeText("CANCEL").show();}
                    else
                    {
                        new MaterialDialog.Builder(NotesDetailActivity.this).title("Remove password ?").content("Are you sure you want to remove the password").positiveText("OK").negativeText("CANCEL").onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                isLocked=false;
                            }
                        }).show();
                    }
                }

        }
        return false;
    }

    private void checkIfEmpty() {



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==READ_EXTERNAL)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                openFilesChooser();

            }
            else {
                Snackbar.make(parent,"Permission denied",Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void openFilesChooser() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        fileIntent.setType("*/*");
        startActivityForResult(fileIntent,REQUEST_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Attachment attachment;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PHOTO: {
                    String uri = attachmentUri.toString();
                    attachment = new Attachment(uri, Utils.MIME_TYPE_IMAGE);
                    attachmentlist.add(attachment);
                    adapter.addItem(attachment);
                    grid.setVisibility(View.VISIBLE);
                    break;
                }
                case REQUEST_FILE: {
                    List<Uri> uris = new ArrayList<>();
                    if (data.getClipData() != null)
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            uris.add(data.getClipData().getItemAt(i).getUri());
                        }
                    else {
                        uris.add(data.getData());
                    }
                    for (Uri uri : uris) {
                        filename = Utils.queryName(getContentResolver(), uri);
                        new AttachmentTask(uri, this, NotesDetailActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    break;
                }
                case REQUEST_CODE_DRAW:
                {
                    if(data != null)
                    {
                        byte[] sketchbyte = data.getByteArrayExtra("bitmap");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(sketchbyte, 0, sketchbyte.length);
                        File f = Utils.createNewAttachmentFile(NotesDetailActivity.this, Utils.MIME_TYPE_SKETCH_EXT);
                        if (f == null) {
                            Snackbar.make(parent,"Error",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        attachmentUri = Uri.fromFile(f);
                         try
                         {
                             FileOutputStream out = new FileOutputStream(f);
                             bitmap.compress(Bitmap.CompressFormat.PNG,90,out);
                             out.close();
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
Attachment attachment1 = new Attachment(attachmentUri.toString(),Utils.MIME_TYPE_SKETCH);
                         attachmentlist.add(attachment1);
                         adapter.addItem(attachment1);
                         grid.setVisibility(View.VISIBLE);

                    }
                }
            }
        }

    }

    @Override
    public void onSuccess(Attachment attachment) {
        attachmentlist.add(attachment);
        adapter.addItem(attachment);
        grid.setVisibility(View.VISIBLE);

    }

    @Override
    public void onFailed() {
        Snackbar.make(parent,"Error adding file", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        int colour = chosen_colour==getResources().getColor(R.color.c1)?getResources().getColor(R.color.colorPrimary):chosen_colour;

        if(attachmentlist.isEmpty() && title_et.getText().toString().isEmpty()&&title_et.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Cannot save empty note",Toast.LENGTH_LONG).show();
            super.onBackPressed();

        }
        else
        {

            notetmp.setTitle(title_et.getText().toString().isEmpty()?"":title_et.getText().toString());
            notetmp.setContent(content_et.getText().toString().isEmpty()?"":content_et.getText().toString());
            notetmp.setAttachmentjson(new Gson().toJson(attachmentlist));
            notetmp.setMillis(System.currentTimeMillis());
            notetmp.setIsLocked(isLocked);
            notetmp.setPassword(password);
            notetmp.setColor(chosen_colour);
            if(notetmp.getIsLocked()==oldnote.getIsLocked() && notetmp.getTitle().equals(oldnote.getTitle())
                    && notetmp.getContent().equals(oldnote.getContent())
                    && notetmp.getAttachmentjson().equals(oldnote.getAttachmentjson())
                    && String.valueOf(notetmp.getColor()).equals(String.valueOf(oldnote.getColor())))
            {
                finish();
                return;
            }
if(!save) {
    new MaterialDialog.Builder(NotesDetailActivity.this).title("Save Note ?").content("Do you want to save the note?").positiveColor(colour).negativeColor(colour).positiveText("SAVE").negativeText("CANCEL").onPositive(new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            if (isold)
                notedb.deleteByKey(id);
            notedb.insert(notetmp);
            setResult(RESULT_OK);
            finish();
        }
    }).onNegative(new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            finish();
        }
    }).show();
}
else {
    if (isold)
        notedb.deleteByKey(id);
    notedb.insert(notetmp);
    setResult(RESULT_OK);
    finish();
}
        }
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        Drawable d =TextDrawable.builder().beginConfig().withBorder(4).endConfig().buildRound("",selectedColor);
        color_btn.setImageDrawable(d);
        chosen_colour=selectedColor;
        parent.setBackgroundColor(selectedColor);
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
    }
}