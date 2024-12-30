package com.example.quickchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickchat.R;
import com.example.quickchat.utils.NotificationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class TwoFactorAuthActivity extends AppCompatActivity {

    private TextInputEditText etCode;
    private MaterialButton btnVerify;
    private ImageButton btnBack;
    private String email, expectedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor_auth);

        etCode = findViewById(R.id.et_2fa_code);
        btnVerify = findViewById(R.id.btn_verify);
        btnBack = findViewById(R.id.btn_back);

        email = getIntent().getStringExtra("email");
        expectedCode = getIntent().getStringExtra("code");

        btnVerify.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();

            if (code.equals(expectedCode)) {
                Toast.makeText(TwoFactorAuthActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();
                NotificationUtils.showNotification(this, "Welcome", "Login successful");
                Intent intent = new Intent(TwoFactorAuthActivity.this, HomeActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                etCode.setError("Incorrect code");
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(TwoFactorAuthActivity.this, LoginActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        });
    }
}
