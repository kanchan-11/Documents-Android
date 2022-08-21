package com.example.documents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {
    private final Context context;

    public FingerPrintHandler(Context context) {
        this.context = context;
    }

    public void startAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errorString) {
        this.update("There was an authentication errror. " + errorString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Authentication failed. ", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("You can now access the app. ", true);

        Intent intent = new Intent(context, SecondActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private void update(String s, boolean b) {
        TextView instruction = ((Activity) context).findViewById(R.id.instruction_id);
        ImageView imageView = ((Activity) context).findViewById(R.id.finger_image);
        instruction.setText(s);
        if (b == false) {
            instruction.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {
            instruction.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            imageView.setImageResource(R.mipmap.done_image);


        }
    }

}

