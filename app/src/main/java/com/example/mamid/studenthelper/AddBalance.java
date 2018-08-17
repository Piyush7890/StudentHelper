package com.example.mamid.studenthelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.mamid.studenthelper.Utils.FabTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddBalance extends AppCompatActivity {
    @BindView(R.id.amount)
    com.rengwuxian.materialedittext.MaterialEditText amount;
    @BindView(R.id.description)
    com.rengwuxian.materialedittext.MaterialEditText description;
    @BindView(R.id.title)
    com.rengwuxian.materialedittext.MaterialEditText title;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.add_btn)
    Button add_btn;
    @BindView(R.id.cancel)
    Button cancel_btn;
    @BindView(R.id.category_spinner)
    Spinner category_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);
        ButterKnife.bind(this);
        FabTransform.setup(this, container);
        ArrayAdapter<CharSequence> quality_adapter = ArrayAdapter.createFromResource(this, R.array.category, R.layout.spinner_item);
        quality_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        category_spinner.setAdapter(quality_adapter);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }

        });
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (amount.getText().toString().isEmpty()) {
                    amount.setError("Amount cannot be empty");
                    return;
                }

                if (title.getText().toString().isEmpty()) {
                    title.setError("Title cannot be empty");
                    return;
                }


                Intent i = new Intent();
                i.putExtra("title",title.getText().toString());
                i.putExtra("category", category_spinner.getSelectedItem().toString());
                i.putExtra("description", description.getText().toString());
                i.putExtra("amount",amount.getText().toString());
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }

}