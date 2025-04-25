package com.example.snapfine

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import android.graphics.Color
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var buttonDrawerToggle: ImageButton
    private lateinit var listView: ListView
    private lateinit var logoutButton: Button
    private lateinit var headerView: View
    private lateinit var userNameTextView: TextView
    private lateinit var plus_img: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val cameraCardView = findViewById<CardView>(R.id.card_camera)
        val galleryCardView = findViewById<CardView>(R.id.card_gallery)
        val myComplaintsCardView = findViewById<CardView>(R.id.mycomplaints)
        val myViolationsCardView = findViewById<CardView>(R.id.myviolations)
        plus_img = findViewById(R.id.plus_img)

        plus_img.setOnClickListener(){
            startActivity(Intent(this,CameraActivity::class.java))
        }

        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        drawerLayout = findViewById(R.id.drawerlayout)
        buttonDrawerToggle = findViewById(R.id.buttondrawertoggel)
        listView = findViewById(R.id.drawer_menu_list)  // Make sure your ListView has this ID
        logoutButton = findViewById(R.id.logout_btn_drawer)  // Make sure the logout button has this ID

        buttonDrawerToggle.setOnClickListener {
            val drawerContainer: View = findViewById(R.id.custom_drawer)
            drawerLayout.openDrawer(drawerContainer)
        }


        // Handle navigation menu clicks
        val drawerItems = arrayOf("My Cases", "Your Complaints","Report Violation", "Pay Fines", "Contact", "FAQs", "Privacy Policy")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, drawerItems)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    startActivity(Intent(this, MyCasesActivity::class.java))
                    // Start activity for my cases
                }
                1 -> {
                    startActivity(Intent(this, MyComplaintsActivity::class.java))
                }
                2 -> {
                    startActivity(Intent(this, CameraActivity::class.java))

                }
                3 -> {
                    Toast.makeText(this, "Pay Fines Clicked", Toast.LENGTH_SHORT).show()
                    // Start activity for paying fines
                }
                4 -> {
                    Toast.makeText(this, "Contact Clicked", Toast.LENGTH_SHORT).show()
                    // Start activity for contact
                }
                5 -> {
                    Toast.makeText(this, "FAQs Clicked", Toast.LENGTH_SHORT).show()
                    // Start activity for FAQs
                }
                6 -> {
                    Toast.makeText(this, "Privacy Policy Clicked", Toast.LENGTH_SHORT).show()
                    // Start activity for privacy policy
                }
            }
            drawerLayout.closeDrawers()
        }

        // Set up logout button click
        logoutButton.setOnClickListener {
            logout()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_search -> {

                    true
                }
                R.id.nav_add -> {
                    // load Add New Offence screen
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this,MyCasesActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, MyProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        cameraCardView.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        galleryCardView.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }

        myComplaintsCardView.setOnClickListener {
            val intent = Intent(this, MyComplaintsActivity::class.java)
            startActivity(intent)
        }
        myViolationsCardView.setOnClickListener {
            val intent = Intent(this, MyCasesActivity::class.java)
            startActivity(intent)
        }

    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, Register::class.java))
        finish()
    }

    fun viewProfile(view: View) {
        startActivity(Intent(this, MyProfileActivity::class.java))
        drawerLayout.closeDrawers()
    }
    override fun onResume() {
        super.onResume()
        updateUserNameInDrawer()
    }
    private fun updateUserNameInDrawer() {
        val headerView = findViewById<View>(R.id.drawer_content)
        val userNameTextView = headerView.findViewById<TextView>(R.id.user_name_text)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    val name = document.getString("Name") ?: "User"
                    userNameTextView.text = name
                }
                .addOnFailureListener {
                    userNameTextView.text = "User"
                }
        } else {
            userNameTextView.text = "Guest"
        }
    }
}
