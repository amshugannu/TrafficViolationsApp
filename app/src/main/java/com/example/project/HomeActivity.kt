package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        val btnReport = findViewById<Button>(R.id.btnReportViolation)
        val btnViewCases = findViewById<Button>(R.id.btnViewCases)

        btnReport.setOnClickListener {
            startActivity(Intent(this, ReportViolationActivity::class.java))
        }

        btnViewCases.setOnClickListener {
            // Navigate to cases screen (to be implemented)
        }
    }
}
