package com.example.documents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.rjesture.startupkit.utils.AppTools.showToast;

public class CreatePasswordActivity extends AppCompatActivity {

    EditText et_entrPswrd, et_cnfrmPswrd;
    Button btn_enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
        setIds();
        setListeners();
    }

    private void setListeners() {
        btn_enter.setOnClickListener(v -> {
            setPassword();
        });
    }

    private void setIds() {
        et_cnfrmPswrd = findViewById(R.id.et_entrPswrd);
        et_cnfrmPswrd = findViewById(R.id.et_cnfrmPswrd);
        btn_enter = findViewById(R.id.btn_enter);
    }
    private void setPassword(){
        String entrPsrwd = et_entrPswrd.getText().toString();
        String cnfrmPswrd = et_cnfrmPswrd.getText().toString();
        if(entrPsrwd.equals("") || cnfrmPswrd.equals("")) {
            //no password entered
            //showToast(this,"");
            Toast.makeText(this,"Some field is not entered",Toast.LENGTH_LONG);
        }
        else
        if(entrPsrwd.equals(cnfrmPswrd)) {
            SharedPreferences settings = getSharedPreferences("PREFS",0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("password",entrPsrwd);
            editor.commit();

            Intent intent = new Intent(this,SecondActivity.class);
        }
        else {
            showToast(CreatePasswordActivity.this,"The entered passwords does not match!");
        }
    }
}