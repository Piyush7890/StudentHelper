package com.example.mamid.studenthelper.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.mamid.studenthelper.Model.Attachment;
import com.example.mamid.studenthelper.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static String MIME_TYPE_IMAGE = "image/jpeg";

    public static  String MIME_TYPE_SKETCH = "image/png";
    public static  String MIME_TYPE_FILES = "file/*";

    public static String MIME_TYPE_IMAGE_EXT = ".jpeg";

    public static String MIME_TYPE_SKETCH_EXT = ".png";
    static String DATE_FORMAT_SORTABLE = "yyyyMMdd_HHmmss_SSS";

    public static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    public static boolean checkStorage() {
        boolean mExternalStorageAvailable;
        boolean mExternalStorageWriteable;
        String state = Environment.getExternalStorageState();

        switch (state) {
            case Environment.MEDIA_MOUNTED:
                mExternalStorageAvailable = mExternalStorageWriteable = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
                break;
            default:
                mExternalStorageAvailable = mExternalStorageWriteable = false;
                break;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable;
    }
    public static String getFileExtension(String fileName) {
        if (TextUtils.isEmpty(fileName)) return "";
        String extension = "";
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            extension = fileName.substring(index, fileName.length());
        }
        return extension;
    }

    public static String getMimeType(Context mContext, Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        String mimeType = cR.getType(uri);
        if (mimeType == null) {
            mimeType = getMimeType(uri.toString());
        }
        return mimeType;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static Attachment createAttachmentFromUri(Context mContext, Uri uri) {
        String name = queryName(mContext.getContentResolver(), uri);
        String extension = getFileExtension(name).toLowerCase(
                Locale.getDefault());
        File f;

            f = createExternalStoragePrivateFile(mContext, uri, extension);

        Attachment mAttachment = null;
        if (f != null) {
            mAttachment = new Attachment(Uri.fromFile(f).toString(), getMimeTypeInternal(mContext, uri));
            mAttachment.setName(name);
            mAttachment.setSize(f.length());
        }
        return mAttachment;
    }
    public static String getMimeTypeInternal(Context mContext, Uri uri) {
        String mimeType = getMimeType(mContext, uri);
        mimeType = getMimeTypeInternal(mContext, mimeType);
        return mimeType;
    }

    public static String getMimeTypeInternal(Context mContext, String mimeType) {
        if (mimeType != null) {
            if (mimeType.contains("image/")) {
                mimeType = "image/jpeg";
            } else if (mimeType.contains("audio/")) {
                mimeType = "audio/amr";
            } else if (mimeType.contains("video/")) {
                mimeType = "video/mp4";
            } else {
                mimeType = "file/*";
            }
        }
        return mimeType;
    }


    public static File createExternalStoragePrivateFile(Context mContext, Uri uri, String extension) {

        if (!checkStorage()) {
            Toast.makeText(mContext,"Storage not available", Toast.LENGTH_SHORT).show();
            return null;
        }
        File file = createNewAttachmentFile(mContext, extension);

        InputStream contentResolverInputStream = null;
        OutputStream contentResolverOutputStream = null;
        try {
            contentResolverInputStream = mContext.getContentResolver().openInputStream(uri);
            contentResolverOutputStream = new FileOutputStream(file);
            copyFile(contentResolverInputStream, contentResolverOutputStream);
        } catch (IOException e) {
            try {
                FileUtils.copyFile(new File(FileHelper.getPath(mContext, uri)), file);
                // It's a path!!
            } catch (NullPointerException e1) {
                try {
                    FileUtils.copyFile(new File(uri.getPath()), file);
                } catch (IOException e2) {
                    Log.e("Error", "Error writing " + file, e2);
                    file = null;
                }
            } catch (IOException e2) {
                Log.e("Error", "Error writing " + file, e2);
                file = null;
            }
        } finally {
            try {
                if (contentResolverInputStream != null) {
                    contentResolverInputStream.close();
                }
                if (contentResolverOutputStream != null) {
                    contentResolverOutputStream.close();
                }
            } catch (IOException e) {
                Log.e("Error", "Error closing streams", e);
            }

        }
        return file;
    }

    public static boolean copyFile(InputStream is, OutputStream os) {
        boolean res = false;
        byte[] data = new byte[1024];
        int len;
        try {
            while ((len = is.read(data)) > 0) {
                os.write(data, 0, len);
            }
            is.close();
            os.close();
            res = true;
        } catch (IOException e) {
            Log.e("Error", "Error copying file", e);
        }
        return res;
    }

    public static boolean isAvailable(Context ctx, Intent intent, String[] features) {
        boolean res = getCompatiblePackages(ctx, intent).size() > 0;

        if (features != null) {
            for (String feature : features) {
                res = res && ctx.getPackageManager().hasSystemFeature(feature);
            }
        }
        return res;
    }
    public static File createNewAttachmentFile(Context mContext, String extension) {
        File f = null;
        if (checkStorage()) {
            f = new File(mContext.getExternalFilesDir(null), createNewAttachmentName(extension));
        }
        return f;
    }

    public static synchronized String createNewAttachmentName(String extension) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_SORTABLE);
        String name = sdf.format(now.getTime());
        name += extension != null ? extension : "";
        return name;
    }


    private static List<ResolveInfo> getCompatiblePackages(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        return mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    public  static int getColorForCategory(String category)
    {


        switch (category)
        {
            case "Food and Drinks":
                return Color.parseColor("#ff6737");
            case "Shopping":
                return Color.parseColor("#75d0f9");

            case "Housing":
                return Color.parseColor("#ffba55");

            case "Transportation":
                return Color.parseColor("#95a8b1");

            case "Vehicle":
                return Color.parseColor("#bc37ff");

            case "Life and Entertainment":
                return Color.parseColor("#85e449");

            case "Communication, PC":
                return Color.parseColor("#788cf3");

            case "Financial expenses":
                return Color.parseColor("#37cdb8");

            case "Investments":
                return Color.parseColor("#ff699c");

            case "Income":
                return Color.parseColor("#fcce5a");

            case "Others":
                return Color.parseColor("#b3b3b3");
            default:
                return Color.parseColor("#b3b3b3");

        }


    }
}
