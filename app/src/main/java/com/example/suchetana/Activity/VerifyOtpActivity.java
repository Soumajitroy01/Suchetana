package com.example.suchetana.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityVerifyOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class VerifyOtpActivity extends AppCompatActivity {

    //view binding
    private ActivityVerifyOtpBinding binding;

    //verification code
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.yellow01));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //set mobile text to the number given by user
        binding.mobileTv.setText(String.format(
                "+91-%s", getIntent().getStringExtra("mobile")
        ));

        //otp sent by firebase
        verificationId = getIntent().getStringExtra("verificationId");

        //setup otp inputs
        setupOTPInputs();

        //handle click, verify otp
        binding.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOTP();
            }
        });

        //set resendOtpTv to enabled false for 60 secs
        binding.resendOtpTv.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                binding.timerTv.setText(f.format(min) + ":" + f.format(sec));
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                binding.timerTv.setText("00:00");
                binding.resendOtpTv.setEnabled(true);
            }
        }.start();

        //handle click, resend otp
        binding.resendOtpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOTP();
            }
        });
    }

    //resend otp
    private void resendOTP() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.verifyBtn.setVisibility(View.INVISIBLE);

        //send otp by phone auth provider
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + getIntent().getStringExtra("mobile"),
                60L,
                TimeUnit.SECONDS,
                VerifyOtpActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        //auto verify otp
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        binding.verifyBtn.setVisibility(View.VISIBLE);

                                        if (task.isSuccessful()) {
                                            //check new user
                                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();

                                            //if new user go to register activity
                                            //else go to main activity
                                            Intent intent;
                                            if (isNew) {
                                                intent = new Intent(VerifyOtpActivity.this, RegisterActivity.class);
                                                intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                                            } else {
                                                intent = new Intent(VerifyOtpActivity.this, MainActivity.class);
                                            }
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(VerifyOtpActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifyOtpActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.verifyBtn.setVisibility(View.VISIBLE);

                        verificationId = newVerificationId;
                        Toast.makeText(VerifyOtpActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();

                        //set resendOtpTv to enabled false for 60 secs
                        binding.resendOtpTv.setEnabled(false);
                        new CountDownTimer(60000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                // Used for formatting digit to be in 2 digits only
                                NumberFormat f = new DecimalFormat("00");
                                long min = (millisUntilFinished / 60000) % 60;
                                long sec = (millisUntilFinished / 1000) % 60;
                                binding.timerTv.setText(f.format(min) + ":" + f.format(sec));
                            }
                            // When the task is over it will print 00:00:00 there
                            public void onFinish() {
                                binding.timerTv.setText("00:00");
                                binding.resendOtpTv.setEnabled(true);
                            }
                        }.start();
                    }
                });
    }

    //verify otp
    private void verifyOTP() {
        //check valid input
        if (binding.inputCode1.getText().toString().trim().isEmpty()
                || binding.inputCode2.getText().toString().trim().isEmpty()
                || binding.inputCode3.getText().toString().trim().isEmpty()
                || binding.inputCode4.getText().toString().trim().isEmpty()
                || binding.inputCode5.getText().toString().trim().isEmpty()
                || binding.inputCode6.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter valid code", Toast.LENGTH_SHORT).show();
            return;
        }

        //code entered by user
        String code = binding.inputCode1.getText().toString()
                + binding.inputCode2.getText().toString()
                + binding.inputCode3.getText().toString()
                + binding.inputCode4.getText().toString()
                + binding.inputCode5.getText().toString()
                + binding.inputCode6.getText().toString();

        if (verificationId != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.verifyBtn.setVisibility(View.INVISIBLE);

            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                    verificationId, code
            );
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.verifyBtn.setVisibility(View.VISIBLE);

                            if (task.isSuccessful()) {
                                //check new user
                                boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();

                                //if new user go to register activity
                                //else go to main activity
                                Intent intent;
                                if (isNew) {
                                    intent = new Intent(VerifyOtpActivity.this, RegisterActivity.class);
                                    intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                                } else {
                                    intent = new Intent(VerifyOtpActivity.this, MainActivity.class);
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(VerifyOtpActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void setupOTPInputs() {
        //add textChangedListeners to all the inputCode1, inputCode2, etc. to set their behaviour of automatically going to next
        binding.inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode3.requestFocus();
                } else {
                    binding.inputCode1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode4.requestFocus();
                } else {
                    binding.inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode5.requestFocus();
                } else {
                    binding.inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode6.requestFocus();
                } else {
                    binding.inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.inputCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().isEmpty()) {
                    binding.inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}