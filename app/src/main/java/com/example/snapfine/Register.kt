package com.example.snapfine

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {


    private lateinit var progressBar: ProgressBar
    private lateinit var fAuth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        val mEmail = findViewById<EditText>(R.id.email)
        val mPassword = findViewById<EditText>(R.id.password)
        progressBar = findViewById(R.id.progressBar2)
        fAuth = FirebaseAuth.getInstance()
        val mLoginBtn = findViewById<Button>(R.id.loginbtn)
        val mCreateBtn = findViewById<TextView>(R.id.textlogin)

        mLoginBtn.setOnClickListener {

            val email = mEmail.text.toString().trim()
            val password = mPassword.text.toString().trim()

            if (email.isEmpty()) {
                mEmail.error = "Email is Required."
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                mPassword.error = "Password is Required."
                return@setOnClickListener
            }

            if (password.length < 6) {
                mPassword.error = "Password must be >= 6 characters"
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                } else {
                    Toast.makeText(this, "Error! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }


        }
        mCreateBtn.setOnClickListener(){
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
    }
}