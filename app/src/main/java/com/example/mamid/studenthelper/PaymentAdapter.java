package com.example.mamid.studenthelper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.mamid.studenthelper.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentViewHolder> {
    private List<Payment> payments;
    private clickListener listener;

    public PaymentAdapter() {
        payments = new ArrayList<>();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PaymentViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.payments_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PaymentViewHolder viewholder, int i) {
        viewholder.amount.setText(String.format("-₹ %s", payments.get(i).getAmount()));
        Long time = Long.valueOf(payments.get(i).getDate());
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4).bold() /* thickness in px */
                .endConfig()
                .buildRound(String.valueOf(payments.get(i).getCategory().toCharArray()[0]),
                        Utils.getColorForCategory(payments.get(i).getCategory()));
       viewholder.category_image.setImageDrawable(drawable);
        viewholder.date.setText(DateUtils.getRelativeTimeSpanString(viewholder.itemView.getContext(),time));
        viewholder.category.setText(payments.get(i).getCategory());
        viewholder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               listener.onLongClick(payments.get(viewholder.getAdapterPosition()).getId(),viewholder.getAdapterPosition());

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }
    public void removeItem(int pos)
    {
        payments.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addData(List<Payment> list)
    {
        payments.addAll(list);
        notifyDataSetChanged();
    }
    public void addItem(Payment payment)
    {
        payments.add(payment);
        notifyItemInserted(getItemCount()-1);
    }

    public void setListener(clickListener listener)
    {
        this.listener=listener;
    }

    public void removeall() {
        payments.clear();
    }


    public interface clickListener
    {
       void onLongClick(Long id, int pos);
    }
}
class PaymentViewHolder extends RecyclerView.ViewHolder
{
    TextView category;
    TextView date;
    TextView amount;
    ImageView category_image;

    public PaymentViewHolder(@NonNull View itemView) {
        super(itemView);
        category = itemView.findViewById(R.id.category);
        date = itemView.findViewById(R.id.time);
        amount = itemView.findViewById(R.id.money_less);
        category_image = itemView.findViewById(R.id.category_image);
    }
}
