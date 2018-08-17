package com.example.mamid.studenthelper;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.mamid.studenthelper.Model.Attachment;
import com.example.mamid.studenthelper.Utils.Utils;

public class AttachmentTask extends AsyncTask<Void,Void,Attachment> {
    private Uri uri;
    AttachmentListener mlistener;
    Context context;

    public AttachmentTask(Uri uri, AttachmentListener mlistener, Context context) {
        this.uri = uri;
        this.mlistener = mlistener;
        this.context = context;
    }

    public void detach()
    {
        mlistener=null;
    }
    @Override
    protected Attachment doInBackground(Void... voids) {
        return Utils.createAttachmentFromUri(context, uri);
    }

    @Override
    protected void onPostExecute(Attachment attachment) {
        if(mlistener!=null )
        {
            if(attachment!=null)
            mlistener.onSuccess(attachment);
            else
                mlistener.onFailed();
        }

    }

    public interface AttachmentListener{
        void onSuccess(Attachment attachment);
        void onFailed();
    }
}
