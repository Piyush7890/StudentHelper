package com.example.mamid.studenthelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.mamid.studenthelper.Model.Attachment;
import com.example.mamid.studenthelper.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentViewholder> {

    private static final int FILES=1;
    private static final int IMAGE=2;

    @Override
    public int getItemViewType(int position) {
        if(attachments.get(position).getMIME_TYPE()!=null && attachments.get(position).getMIME_TYPE().equals("file/*"))
        {
            return FILES;
        }
        else return IMAGE;
    }

    public AttachmentAdapter( ) {
        this.attachments =new ArrayList<>();
    }

    List<Attachment> attachments;



    @NonNull
    @Override
    public AttachmentViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AttachmentViewholder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attachment_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AttachmentViewholder viewholder, final int i) {

        if(getItemViewType(i)==IMAGE)
        {
            Uri uri = Uri.parse(attachments.get(i).getUri());
            Glide.with(viewholder.itemView.getContext())
                    .asBitmap()
                    .load(uri)
                    .apply(new RequestOptions()
                            .centerCrop())
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(viewholder.attachment_img);
            viewholder.attachment_name.setVisibility(View.GONE);
        }

        else if(getItemViewType(i)==FILES)
        {

            Glide.with(viewholder.itemView.getContext())
                    .load(ContextCompat
                            .getDrawable(viewholder
                                    .itemView
                                    .getContext(),
                                    R.drawable.ic_storage_black_24dp))
                    .into(viewholder.attachment_img);
            viewholder.attachment_name.setText(attachments.get(i).getName());
            viewholder.attachment_name.setVisibility(View.VISIBLE);

        }
        final Attachment attachment = attachments.get(i);

        viewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                Uri uri = Uri.parse(attachment.getUri());
                Context context = viewholder.itemView.getContext();
                intent.setDataAndType(uri, Utils.getMimeType(context,uri));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent
                        .FLAG_GRANT_WRITE_URI_PERMISSION);
                PackageManager mgr = context.getPackageManager();

                boolean count = mgr.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY).size()>0;
                if(count)
                {
                    context.startActivity(intent);
                }
                else
                    Snackbar.make(viewholder.itemView,"Application not available", Snackbar.LENGTH_LONG).show();

            }
        });

    }

     public void addItem(Attachment attachment)
    {
        this.attachments.add(attachment);
        notifyItemInserted(getItemCount()-1);
    }


    public void addData(List<Attachment> list)
    {
        attachments.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return attachments.size();
    }
}
class AttachmentViewholder extends RecyclerView.ViewHolder {
    ImageView attachment_img;
    TextView attachment_name;
    public AttachmentViewholder(@NonNull View itemView) {
        super(itemView);
        attachment_img = itemView.findViewById(R.id.attachment_image);
        attachment_name = itemView.findViewById(R.id.attachment_title);
    }
}