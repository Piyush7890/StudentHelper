package com.example.mamid.studenthelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.mamid.studenthelper.Model.Attachment;
import com.example.mamid.studenthelper.Model.Note;
import com.example.mamid.studenthelper.Utils.SquareImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int LINEAR=1;
    private static final int GRID=2;
    boolean grid = true;
    List<Note> notes;
    private clickListener listener;

    public NotesAdapter() {
        notes=new ArrayList<>();
    }

    public void addItem(Note note)
    {
        this.notes.add(note);
        notifyDataSetChanged();
    }

    public void setGrid( )
    {
        grid=!grid;
        notifyDataSetChanged();
    }

    public void addData(List<Note> list)
    {
        notes.addAll(list);
        notifyDataSetChanged();
    }
public void deleteItem(int pos)
{
    notes.remove(pos);
    notifyItemRemoved(pos);
}
    public void deleteAll()
    {
        notes.clear();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(grid)
        return new GridViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_grid_item,viewGroup,false));
    else return new NotesViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder notesViewHolder, int i) {
        if(notesViewHolder instanceof NotesViewHolder)
            bindNoteItem(((NotesViewHolder)notesViewHolder),i);
        else
            bindGridNoteItem(((GridViewHolder)notesViewHolder),i);
    }

    private void bindGridNoteItem(final GridViewHolder viewHolder, int i)
    {
        if(notes.get(i).getIsLocked())
        {
            viewHolder.lock.setVisibility(View.VISIBLE);
        }
        viewHolder.cview.setCardBackgroundColor(notes.get(i).getColor());
        viewHolder.title.setText(notes.get(i).getTitle());
        viewHolder.content.setText(notes.get(i).getContent());
        viewHolder.time.setText(DateUtils.getRelativeTimeSpanString(notes.get(i).getMillis()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(notes.get(viewHolder.getAdapterPosition()).getId(),viewHolder.getAdapterPosition());
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(notes.get(viewHolder.getAdapterPosition()).getId(),viewHolder.getAdapterPosition());
                return true;
            }
        });
        List<Attachment> list = new Gson().fromJson(notes.get(i).getAttachmentjson(),new TypeToken<List<Attachment>>() {
        }.getType());
        if(!list.isEmpty())
        {
                loadimage(viewHolder.img1,viewHolder.itemView.getContext(),list,0);

            if(list.size()>=2) {
                loadimage(viewHolder.img2, viewHolder.itemView.getContext(), list, 1);
            }
        }
        else {
            viewHolder.img1.setVisibility(View.GONE);
            viewHolder.img2.setVisibility(View.GONE);
        }

    }

    private void loadimage(SquareImageView img2, Context context,List<Attachment> list, int pos) {
        if(list.get(pos).getMIME_TYPE().equals("file/*"))
            img2.setBackground(context.getDrawable(R.drawable.ic_storage_black_24dp));
        else
        Glide.with(context)
                .asBitmap()
                .load(list.get(pos).getUri()).apply(new RequestOptions().centerCrop())
                .into(img2);

    }

    private void bindNoteItem(final NotesViewHolder NotesViewHolder,int i)
    {
        if(notes.get(i).getIsLocked())
        {
            NotesViewHolder.lock.setVisibility(View.VISIBLE);
        }
        NotesViewHolder.title.setText(notes.get(i).getTitle());
        NotesViewHolder.date.setText(DateUtils.getRelativeTimeSpanString(notes.get(i).getMillis()));
        NotesViewHolder.background.setCardBackgroundColor(notes.get(i).getColor());
        NotesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(notes.get(NotesViewHolder.getAdapterPosition()).getId(),NotesViewHolder.getAdapterPosition());
            }
        });
        NotesViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(notes.get(NotesViewHolder.getAdapterPosition()).getId(),NotesViewHolder.getAdapterPosition());
                return true;
            }
        });
    }

    public void setListener(clickListener listener)
    {
        this.listener=listener;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public interface clickListener
    {
        void onClick(Long id, int pos);
        void onLongClick(Long id, int pos);

    }
}
class NotesViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView date;
    CardView background;
    ImageView lock;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        title=itemView.findViewById(R.id.title);
        date=itemView.findViewById(R.id.time);
        background = itemView.findViewById(R.id.background);
        lock=itemView.findViewById(R.id.lock);
    }
}

class GridViewHolder extends RecyclerView.ViewHolder{
    TextView title;
    TextView content;
    SquareImageView img1,img2;
    TextView time;
    CardView cview;
ImageView lock;
    public GridViewHolder(@NonNull View itemView) {
        super(itemView);
        img1=itemView.findViewById(R.id.image_1);
        img2=itemView.findViewById(R.id.image_2);
        time=itemView.findViewById(R.id.time_edited);
        title=itemView.findViewById(R.id.title);
        content=itemView.findViewById(R.id.content);
        cview=itemView.findViewById(R.id.cview);
        ViewOutlineProvider outlineProvider= new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0,0,view.getWidth(),view.getHeight(),8);

            }
        };
        img1.setOutlineProvider(outlineProvider);
        img1.setClipToOutline(true);
        img2.setOutlineProvider(outlineProvider);
        img2.setClipToOutline(true);
        lock=itemView.findViewById(R.id.lock);


    }
}