package com.example.project

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {

    private val channelId = "Notification_Channel"
    private val otp by lazy { generateOTP() }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        createNotificationChannel()
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val email = findViewById<EditText>(R.id.etEmail)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        loginButton.setBackgroundColor(Color.parseColor("#FFC107"))

        loginButton.setOnClickListener {
            val userInput = email.text.toString().trim()

            when {
                userInput.isEmpty() -> {
                    Toast.makeText(this, "Enter Your Phone Number", Toast.LENGTH_SHORT).show()
                }
                userInput.length < 10 -> {
                    Toast.makeText(this, "Enter Valid Phone Number", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Start OTP verification activity
                    val intent = Intent(this, OtpVerification::class.java).apply {
                        putExtra("otp", otp.toString())
                    }
                    startActivity(intent)

                    // Show notification if permissions are granted
                    if (checkNotificationPermission()) {
                        showNotification()
                    }
                    finish()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "VioCam Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "OTP Verification Channel"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required for older versions
        }
    }

    private fun showNotification() {
        val intent = Intent(this, OtpVerification::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_img)
            .setContentTitle("Dear Citizen,")
            .setContentText("$otp is your OTP for VioCam verification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, notification)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showNotification()
                }
            }
        }
    }

    private fun generateOTP(): Int {
        return (100000..999999).random()
    }
}
