package com.example.snapfine

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class MyComplaintsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViolationAdapter
    private lateinit var emptyView: TextView
    private lateinit var backbtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_complaints)

        recyclerView = findViewById(R.id.recyclerViewMyComplaints)
        recyclerView.layoutManager = LinearLayoutManager(this)
        emptyView = findViewById(R.id.emptyView)

        backbtn = findViewById(R.id.btn_back)
        backbtn.setOnClickListener { onBackPressed() }

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid == null) {
            Toast.makeText(this, "Please login to view your complaints.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Query Firestore to get violations reported by the current user
        val query: Query = FirebaseFirestore.getInstance()
            .collection("violations")
            .whereEqualTo("reportedByUID", currentUserUid)
            .orderBy("timestamp", Query.Direction.DESCENDING) // Optional: Order by timestamp

        val options = FirestoreRecyclerOptions.Builder<Violation>()
            .setQuery(query, Violation::class.java)
            .build()

        adapter = ViolationAdapter(options)
        recyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (adapter.itemCount == 0) {
                    emptyView.visibility = View.VISIBLE // Show empty state
                } else {
                    emptyView.visibility = View.GONE // Hide empty state
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}
