package com.example.project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ReportViolationActivity : AppCompatActivity() {

    private lateinit var btnUpload: Button
    private lateinit var etDescription: EditText
    private lateinit var btnSubmit: Button
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_violation)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        btnUpload = findViewById(R.id.btnUploadImage)
        etDescription = findViewById(R.id.etDescription)
        btnSubmit = findViewById(R.id.btnSubmitReport)

        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/* video/*"
            startActivityForResult(intent, 100)
        }

        btnSubmit.setOnClickListener {
            if (selectedFileUri != null && etDescription.text.toString().isNotEmpty()) {
                Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please upload evidence and enter details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
            Toast.makeText(this, "File Selected", Toast.LENGTH_SHORT).show()
        }
    }
}
