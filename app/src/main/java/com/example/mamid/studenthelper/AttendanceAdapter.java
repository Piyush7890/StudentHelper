package com.example.mamid.studenthelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.mamid.studenthelper.Utils.TransitionUtils;
import com.example.mamid.studenthelper.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int[] array;
    private List<Subject> items;
    private float elevation = 0;
private float origelevation = 0;
AutoTransition auto;
    private RecyclerView rview;
    private View header;

    @Override
    public int getItemViewType(int position) {
        if(position==0)
            return R.layout.attendance_header_item;
        else return R.layout.attendance_item;
    }

    public AttendanceAdapter(int[] array, Context context, final RecyclerView rview ) {
        auto = new AutoTransition();
        auto.setDuration(250L);
        auto.setInterpolator(new FastOutSlowInInterpolator());
                elevation = context.getResources().getDisplayMetrics().density * 4;
        origelevation = context.getResources().getDisplayMetrics().density*1;
        this.array = array;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==R.layout.attendance_header_item)
        {
            return new SimpleViewHolder(header);
        }
        return new AttendanceViewholder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attendance_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
if(getItemViewType(i)==R.layout.attendance_item) {


    final Subject subject = items.get(i-1);
    TextDrawable drawable = TextDrawable.builder().buildRound(String.valueOf(subject.getSubjectname().toCharArray()[0]), array[i-1]);

    ((AttendanceViewholder) (viewHolder)).container.setProgress((float) (subject.getAttendance() / 100), array[i-1]);
    ((AttendanceViewholder) viewHolder).percentage.setText(String.format("%s %%", subject.getAttendance()));
    ((AttendanceViewholder) viewHolder).subjectname.setText(String.valueOf(subject.getSubjectname()));
    ((AttendanceViewholder) viewHolder).category.setText(String.valueOf(subject.getCategory()));
    ((AttendanceViewholder) viewHolder).drawable.setImageDrawable(drawable);
    ((AttendanceViewholder) viewHolder).attended.setText(String.format("Attended Lectures : %s", subject.getPresent()));
    ((AttendanceViewholder) viewHolder).skipped.setText(String.format("Total Lectures : %s", subject.getTotal()));

    ((AttendanceViewholder) viewHolder).container.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!subject.isvisible) {
                subject.isvisible = true;
                TransitionManager.beginDelayedTransition((ViewGroup) viewHolder.itemView.getRootView(), auto);
                ((AttendanceViewholder) viewHolder).container.setElevation(elevation);
                ((AttendanceViewholder) viewHolder).skipped.setVisibility(View.VISIBLE);
                ((AttendanceViewholder) viewHolder).attended.setVisibility(View.VISIBLE);
            } else {
                subject.isvisible = false;
                TransitionManager.beginDelayedTransition((ViewGroup) viewHolder.itemView.getRootView(), auto);
                ((AttendanceViewholder) viewHolder).container.setElevation(origelevation);
                ((AttendanceViewholder) viewHolder).skipped.setVisibility(View.GONE);
                ((AttendanceViewholder) viewHolder).attended.setVisibility(View.GONE);
            }
        }
    });
}
    }
    public  void removeData()
    {
        items = null;
        items = new ArrayList<>();
    }

    public void clear()
    {
        items.clear();
    }

    @Override
    public int getItemCount() {
        return items.size()+1;
    }

    public void addHeader(View header)
    {
this.header = header;
    }

    public   void addData(List<Subject> data)
    {
        items.addAll(data);
        notifyDataSetChanged();

    }
}

class AttendanceViewholder extends RecyclerView.ViewHolder{
    RelativeLayout viewgroup;
    CustomSubjectView container;
TextView subjectname;
TextView category;
ImageView drawable;
TextView percentage;
TextView attended;
TextView skipped;

    public AttendanceViewholder(@NonNull View itemView)
    {
        super(itemView);
        viewgroup = itemView.findViewById(R.id.viewgroup);
        subjectname = itemView.findViewById(R.id.subject_name);
        drawable = itemView.findViewById(R.id.subject_image);
        percentage = itemView.findViewById(R.id.percentage);
        container = itemView.findViewById(R.id.container);
    category = itemView.findViewById(R.id.category_name);
    attended = itemView.findViewById(R.id.attended_text);
    skipped = itemView.findViewById(R.id.skipped_text);


    }
}

class SimpleViewHolder extends RecyclerView.ViewHolder
{

    public SimpleViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
