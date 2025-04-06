package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class OtpVerification : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp_verification)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        val receivedValue = intent.getStringExtra("otp")
        val verifyButton = findViewById<Button>(R.id.btnVerify)
        val otpEditText = findViewById<EditText>(R.id.otpEditText)
        verifyButton.setBackgroundColor(Color.parseColor("#FFC107"))
        verifyButton.setOnClickListener {
            if(otpEditText.text.toString()==receivedValue){
                startActivity(Intent(this, HomeActivity::class.java))
            }
            else{
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
        val backButton = findViewById<MaterialButton>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}