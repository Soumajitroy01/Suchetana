package com.example.suchetana.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    //view binding
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.yellow01));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //handle click, get otp open verify otp
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateNumber();
            }
        });
    }

    private void validateNumber() {
        //if mobile field is empty
        if (binding.mobileEt.getText().toString().trim().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Enter Mobile no.", Toast.LENGTH_SHORT).show();
            return;
        }
        //if mobile is invalid
        if (binding.mobileEt.getText().toString().trim().length() != 10) {
            Toast.makeText(this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        //make progress bar visible and button invisible
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.loginBtn.setVisibility(View.INVISIBLE);

        //send otp by phone auth provider
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + binding.mobileEt.getText().toString().trim(),
                0,
                TimeUnit.SECONDS,
                LoginActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.loginBtn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.loginBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.loginBtn.setVisibility(View.VISIBLE);

                        //after checking everything send otp
                        Intent intent = new Intent(LoginActivity.this, VerifyOtpActivity.class);
                        intent.putExtra("mobile", binding.mobileEt.getText().toString().trim());
                        intent.putExtra("verificationId", verificationId);
                        startActivity(intent);
                    }
                }
        );
    }
}