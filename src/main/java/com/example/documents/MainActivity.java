package com.example.documents;

import android.Manifest;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.core.content.ContextCompat;

import static com.rjesture.startupkit.utils.AppTools.showToast;

public class MainActivity extends AppCompatActivity {

    private TextView instructions;
    private ImageView fingerprint;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private TextView tv_use_password;

    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load password
        SharedPreferences settings = getSharedPreferences("PREFS",0);
        password = settings.getString("password","");

        setIds();
        setListeners();

        //1:android version should be greater or equal to marshmello
        //2:device has fingerprint scanner
        //3:have persmission to use fingerprint
        //4:lock screen is secured with atleast one type of lock
        //atleast one finger is registered in the app

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (!fingerprintManager.isHardwareDetected()) {
                instructions.setText("Fingerprint Scanner not detected in your Device");
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                instructions.setText("Permission not granted to use fingerprint scannner");
            } else if (!keyguardManager.isKeyguardSecure()) {
                instructions.setText("Add lock to your homescreen in Settings");
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                instructions.setText("You should add atleast one fingerprint to your phone to use this feature.");
            } else {
                instructions.setText("Place your finger on scanner to access the app.");
                FingerPrintHandler fingerprintHandler = new FingerPrintHandler(MainActivity.this);
                fingerprintHandler.startAuthentication(fingerprintManager, null);

            }
        }
    }

    private void setListeners() {
        tv_use_password.setOnClickListener(v -> {
            openUsingPassword();
        });
    }

    private void setIds() {
        instructions = findViewById(R.id.instruction_id);
        fingerprint = findViewById(R.id.finger_image);
        tv_use_password = findViewById(R.id.tv_use_password);
    }

    private void openUsingPassword() {
        setContentView(R.layout.use_password);

        CheckBox btn_showHide;
        EditText et_password;
        TextView tv_createPassword;
        Button btn_enter;

        et_password = findViewById(R.id.et_password);
        btn_showHide = findViewById(R.id.btn_showHide);
        tv_createPassword = findViewById(R.id.tv_createPassword);
        btn_enter = findViewById(R.id.btn_enter);

        btn_showHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        tv_createPassword.setOnClickListener(v -> {
            if(password.equals(""))
            {
                //if there is no password
                final Dialog dialog= new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialogueboxchoose);
                dialog.show();
//                Intent intent = new Intent(this,CreatePasswordActivity.class);
//                startActivity(intent);
//                finish();

                EditText et_entrPswrd = dialog.findViewById(R.id.et_entrPswrd);
                EditText et_cnfrmPswrd = dialog.findViewById(R.id.et_cnfrmPswrd);
                Button btn_entr = dialog.findViewById(R.id.btn_enter);
                btn_entr.setOnClickListener(v1 -> {
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
                        showToast(this,"The entered passwords does not match!");
                    }
                });
            }
            else
            {
                //if there is a password
                showToast(MainActivity.this,"Password already exists");
            }
        });

//        et_password.setInputType(InputType.TYPE_CLASS_NUMBER);
        btn_enter.setOnClickListener(v -> {
            enterPassword(et_password.getText().toString());
        });
    }

    private void enterPassword(String pswrd) {
        if(password.equals(""))
        {
            showToast(this,"Kindly set a password to login ");
        }
        else
        if(pswrd.equals(password))
        {
            //emter the app
            Intent intent = new Intent(this,SecondActivity.class);
            startActivity(intent);
            finish();
        }
        else
            showToast(this,"Enter a correct password");
    }
}