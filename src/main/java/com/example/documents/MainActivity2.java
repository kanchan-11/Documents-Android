package com.example.documents;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import static android.text.TextUtils.isEmpty;

public class MainActivity2 extends AppCompatActivity {
    EditText et_text1,et_text2,et_text3,et_text4;
    TextView tv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setId();
        setListener();
    }

    private void setListener() {
        et_text1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isEmpty(et_text1.getText()))
                tv_text.setText(et_text1.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_text2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isEmpty(et_text2.getText()))
                    tv_text.setText(et_text2.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_text3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isEmpty(et_text3.getText()))
                    tv_text.setText(et_text3.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_text4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isEmpty(et_text4.getText()))
                    tv_text.setText(et_text4.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setId() {
        tv_text = findViewById(R.id.tv_text);
        et_text1 = findViewById(R.id.et_text1);
        et_text2 = findViewById(R.id.et_text2);
        et_text3 = findViewById(R.id.et_text3);
        et_text4 = findViewById(R.id.et_text4);
    }

}