package com.example.mamid.studenthelper;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.mamid.studenthelper.Model.DaoSession;
import com.example.mamid.studenthelper.Utils.FabTransform;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BalanceActivity extends AppCompatActivity implements PaymentAdapter.clickListener {
    private final static String PREFS="BALANCE_PREFS";

    String[] categories;
    int[] colors;
    List<Integer> colors_tmp;
    float balance_minus=0;
    @BindView(R.id.delete_all)
    Button delete_all;
    @BindView(R.id.decrease)
    TextView percent_decrease;
    @BindView(R.id.add_btn)
    FloatingActionButton add_btn;
    @BindView(R.id.viewstub)
    LinearLayout temp;
private static final int ADDPAYMENT = 1001;
@BindView(R.id.balance)
    TextView balance;
@BindView(R.id.payments_list)
    RecyclerView rview;
@BindView(R.id.edit_btn)
    ImageView edit_btn;
float total_balance;
PaymentAdapter adapter;
PaymentDao paymentdb;
DaoSession session;
    List<Payment> listt;
    @BindView(R.id.piechart)
    PieChart mChart;

int decrease;
    @Override
    public void onBackPressed() {
        SharedPreferences prefs = getSharedPreferences(PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("balance_minus",balance_minus);
        editor.putFloat("total_balance",total_balance);

        editor.putInt("percent_decrease",decrease);
        editor.apply();
        Intent i = new Intent();
        i.putExtra("total",total_balance);
        i.putExtra("spent",balance_minus);
        setResult(RESULT_OK,i);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ButterKnife.bind(this);
        colors_tmp = new ArrayList<>();
        categories=getResources().getStringArray(R.array.category);
       colors = getResources().getIntArray(R.array.category_colors);
       SharedPreferences prefs = getSharedPreferences(PREFS,MODE_PRIVATE);
        balance_minus=prefs.getFloat("balance_minus",0);
        decrease=prefs.getInt("percent_decrease",0);
        total_balance=prefs.getFloat("total_balance",0);
        balance.setText(String.valueOf(total_balance));
        String per = "-" + decrease + "%";
        percent_decrease.setText(per);
        session =((StudentHelper)getApplication()).getDaoSession();
        adapter = new PaymentAdapter();
        adapter.setListener(this);
        rview.setAdapter(adapter);
        rview.setLayoutManager(new LinearLayoutManager(this));
        paymentdb =session.getPaymentDao();
         listt = paymentdb.loadAll();
        mChart.highlightValues(null);
        mChart.setCenterTextColor(Color.parseColor("#3e3e3e"));
        if(listt!=null )
        {
            if(!listt.isEmpty())
            {
            temp.setVisibility(View.GONE);
            adapter.addData(listt);
            rview.setVisibility(View.VISIBLE);
            delete_all.setVisibility(View.VISIBLE);


            initChart();
        }}

        if((Build.VERSION.SDK_INT>=Build.VERSION_CODES.M))
        {
            getWindow().setStatusBarColor(Color.parseColor("#f0f0f0"));

            int flags = add_btn.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            add_btn.setSystemUiVisibility(flags);
        }

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(BalanceActivity.this)
                        .title("Adjust Balance")
                        .inputRangeRes(0, 16, R.color.md_red_500)
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input("New account balance","", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                             if(!input.toString().equals("") && !input.toString().equals("0") ) {
                                 total_balance=Float.valueOf(input.toString());
                                 balance.setText(input);
                                 decrease=(int)balance_minus/Integer.valueOf(input.toString());
                                 percent_decrease.setText("-"+decrease+"%");
                             }
                                 else {
                                 total_balance=0;
                                 balance.setText("0");
                                 decrease=0;
                                percent_decrease.setText("0%");
                             }
                            }
                        }).negativeText("CANCEL").show();
            }
        });

        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(BalanceActivity.this)
                        .title("Are you sure?")
                        .content("Are you sure you want to delete all the records?")
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        adapter.removeall();
                        balance_minus=0;
                        listt.clear();
                        delete_all.setVisibility(View.GONE);
                        rview.setVisibility(View.GONE);
                        initChart();
                        paymentdb.deleteAll();
                            temp.setVisibility(View.VISIBLE);
                    }
                }).show();
            }
        });

    }

    private void initChart() {
        colors_tmp=new ArrayList<>();
        int[] amt = new int[categories.length];
int bal=0;
        for(Payment payment : listt)
        {
            switch (payment.getCategory())
            {
                case "Others":amt[0]+=Integer.valueOf(payment.getAmount());
                    break;
                case "Shopping":amt[1]+=Integer.valueOf(payment.getAmount());
                    break;
                case "Housing":amt[2]+=Integer.valueOf(payment.getAmount());
                    break;
                case "Transportation":amt[3]+=Integer.valueOf(payment.getAmount());
                    break;

                case "Vehicle":amt[4]+=Integer.valueOf(payment.getAmount());
                    break;

                case "Life and Entertainment":amt[5]+=Integer.valueOf(payment.getAmount());
                    break;

                case "Communication, PC":amt[6]+=Integer.valueOf(payment.getAmount());
                    break;

                case "Financial expenses":amt[7]+=Integer.valueOf(payment.getAmount());
                    break;

                case "Investments":amt[8]+=Integer.valueOf(payment.getAmount());
                    break;

                case "Income":amt[9]+=Integer.valueOf(payment.getAmount());
                    break;

                case "Food and Drinks":amt[10]+=Integer.valueOf(payment.getAmount());
                    break;

            }
        }
        ArrayList<PieEntry> entries = new ArrayList<>();
        for(int i =0;i<amt.length;i++)
        {
            if(amt[i]!=0)
            {
                colors_tmp.add(colors[i]);
                entries.add(new PieEntry(amt[i]/balance_minus*100,categories[i]));
            }
        }
        PieDataSet set = new PieDataSet(entries,"");

        set.setColors(colors_tmp);
        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

mChart.setCenterText("₹ "+balance_minus+"\n spent");
        mChart.invalidate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==ADDPAYMENT)
        {
            if(resultCode==RESULT_OK)
            {
                Payment payment = new Payment();
                payment.setAmount(data.getStringExtra("amount"));
                balance_minus+=Float.valueOf(data.getStringExtra("amount"));
                payment.setCategory(data.getStringExtra("category"));
                Long time = Calendar.getInstance().getTimeInMillis();
                payment.setDate(String.valueOf(time));
                payment.setTitle(data.getStringExtra("title"));
                paymentdb.insert(payment);
                adapter.addItem(payment);
                listt.add(payment);
                initChart();
                rview.setVisibility(View.VISIBLE);
                delete_all.setVisibility(View.VISIBLE);
                temp.setVisibility(View.GONE);
            }
        }
    }

    public void addPayment(View v)
    {
        Intent i = new Intent(BalanceActivity.this,AddBalance.class);
        FabTransform.addExtras(i, ContextCompat.getColor(BalanceActivity.this,R.color.colorPrimary),R.drawable.ic_add_black_24dp);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(BalanceActivity.this,add_btn,"SharedDialog");
        startActivityForResult(i, ADDPAYMENT, options.toBundle());
    }

    @Override
    public void onLongClick(final Long id, final int pos) {
        new MaterialDialog.Builder(BalanceActivity.this).title("Confirm Delete..")
                .content("Do you want to delete this entry?").negativeText("CANCEL")
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                adapter.removeItem(pos);
                balance_minus-=Float.valueOf(listt.get(pos).getAmount());
                        listt.remove(pos);
                        initChart();
                paymentdb.deleteByKey(id);
                if(listt.isEmpty())
                    temp.setVisibility(View.VISIBLE);

            }
        }).build().show();
    }
}
