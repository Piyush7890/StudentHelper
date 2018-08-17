package com.example.mamid.studenthelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mamid.studenthelper.Utils.FabTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements VerifyLogin.loginfinished{
private static final String SHARED_PREFS = "SHARED_PREFS";
  @BindView(R.id.dialog_title)
    TextView title;
    @BindView(R.id.container)
    LinearLayout container;
    VerifyLogin verifier;
    @BindView(R.id.login)
Button login;
    @BindView(R.id.username_float_label)
TextInputLayout id_float_label;
    @BindView(R.id.password_float_label)
    TextInputLayout password_float_label;
    @BindView(R.id.login_container)
    FrameLayout parent;
    @BindView(R.id.username)
    AutoCompleteTextView username;
    @BindView(R.id.loading)
    ProgressBar progress;
    @BindView(R.id.password)
EditText password;

    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        username.setText(prefs.getString("login",""));
        password.setText(prefs.getString("password",""));
        FabTransform.setup(this,container);

    }



    public void doLogin(View view)
    {
        if(username.getText().toString().isEmpty()||password.getText().toString().isEmpty()) {

            if (username.getText().toString().isEmpty()) {
                id_float_label.setError("Id cannot be empty");
            }
            if (password.getText().toString().isEmpty()) {
                password_float_label.setError("password cannot be empty");
            }
            return;
        }
        else
        {
            verifier=new VerifyLogin(username.getText().toString(),password.getText().toString());
            verifier.setListener(this);
            verifier.execute();
            AutoTransition transition = new AutoTransition();
            transition.setDuration(350L);
            TransitionManager.beginDelayedTransition(container, transition);
            id_float_label.setVisibility(View.GONE);
            password_float_label.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);


        }



    }

    @Override
    public void loginFailed() {
        AutoTransition transition = new AutoTransition();
        transition.setDuration(350L);
        TransitionManager.beginDelayedTransition(container, transition);
        id_float_label.setVisibility(View.VISIBLE);
        password_float_label.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        Snackbar.make(parent, "Server is down or the credentials are invalid", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void loginSuccess(String json, String name, String totalpercent, String grade, String division) {
        AutoTransition transition = new AutoTransition();
        transition.setDuration(350L);
        TransitionManager.beginDelayedTransition(container, transition);
        progress.setVisibility(View.GONE);
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("login", username.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.apply();
        Intent i = new Intent();
        i.putExtra("AttendanceArray", json);
        i.putExtra("Name", name);
        i.putExtra("ID",username.getText().toString());
        i.putExtra("Total", totalpercent);
        i.putExtra("password",password.getText().toString());
        i.putExtra("grade",grade);
        i.putExtra("division", division);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(verifier!=null)
        verifier.cancel(true) ;
    }
}
