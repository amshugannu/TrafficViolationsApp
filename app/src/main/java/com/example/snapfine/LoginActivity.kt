package com.example.snapfine

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val mFullName = findViewById<EditText>(R.id.fullName)
        val mEmail = findViewById<EditText>(R.id.email)
        val mPassword = findViewById<EditText>(R.id.password)
        val mPhone = findViewById<EditText>(R.id.phonenumber)
        val mRegisterBtn = findViewById<Button>(R.id.registerbtn)
        val mLoginBtn = findViewById<TextView>(R.id.textlogin)

        fAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)
        fStore = FirebaseFirestore.getInstance()

        if (fAuth.currentUser != null) {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }

        mRegisterBtn.setOnClickListener {
            val email = mEmail.text.toString().trim()
            val password = mPassword.text.toString().trim()
            val fullname = mFullName.text.toString().trim()
            val phone = mPhone.text.toString().trim()

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

            fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "User Created.", Toast.LENGTH_SHORT).show()
                        val userID = fAuth.currentUser!!.uid
                        val documentReference = fStore.collection("users").document(userID)
                        val user = hashMapOf<String, Any>()
                        user["Name"] = fullname
                        user["email"] = email
                        user["phone"] = phone

                        documentReference.set(user)
                            .addOnSuccessListener {
                                Log.d(TAG, "onSuccess: user Profile is created for $userID")
                            }
                        startActivity(Intent(applicationContext, HomeActivity::class.java))
                    } else {
                        Toast.makeText(this, "Error! " + task.exception?.message, Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }
        }

        mLoginBtn.setOnClickListener(){
            startActivity(Intent(applicationContext, Register::class.java))
        }
    }
}