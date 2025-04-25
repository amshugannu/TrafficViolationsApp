package com.example.snapfine

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.Log
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

class MyCasesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViolationAdapter // Corrected Line 10
    private lateinit var backbtn: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cases)

        recyclerView = findViewById(R.id.recyclerViewMyCases)
        recyclerView.layoutManager = LinearLayoutManager(this)

        backbtn = findViewById(R.id.btn_back)
        backbtn.setOnClickListener { onBackPressed() }

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid == null) {
            // Handle the case where the user is not authenticated
            return
        }

        // Query Firestore to get violations reported to the current user
        val query: Query = FirebaseFirestore.getInstance()
            .collection("violations")
            .whereEqualTo("reportedToUID", currentUserUid)
            .orderBy("timestamp", Query.Direction.DESCENDING) // Optional: Order by timestamp

        val options = FirestoreRecyclerOptions.Builder<Violation>()
            .setQuery(query, Violation::class.java)
            .build()

        adapter = ViolationAdapter(options)  // Corrected Line 42
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening() // Corrected Line 52
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening() // Corrected Line 57
    }
}

data class Violation(
    val vehicleNumber: String = "",
    val reportedByUID: String = "",
    val reportedToUID: String = "",
    val imageUrl: String = "",
    val violationType: String = "",
    val location: String = "",
    val description: String = "",
    val fineAmount: String = "",
    val status: String = "",
    val time: String = "",
    val date: String = "",
    val timestamp: Long = 0
)


class ViolationAdapter(options: FirestoreRecyclerOptions<Violation>) :
    FirestoreRecyclerAdapter<Violation, ViolationAdapter.ViolationViewHolder>(options) {

    class ViolationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val violationTypeTextView: TextView = itemView.findViewById(R.id.violationTypeTextView)
        val vehicleNumberTextView: TextView = itemView.findViewById(R.id.vehicleNumberTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val fineAmountTextView: TextView = itemView.findViewById(R.id.fineAmountTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViolationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_violation, parent, false) // Replace with your item layout
        return ViolationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViolationViewHolder, position: Int, model: Violation) {
        holder.violationTypeTextView.text = "Violation: ${model.violationType}"
        holder.vehicleNumberTextView.text = "Vehicle: ${model.vehicleNumber}"
        holder.locationTextView.text = "Location: ${model.location}"
        holder.dateTextView.text = "Date: ${model.date}"
        holder.timeTextView.text = "Time: ${model.time}"
        holder.descriptionTextView.text = "Description: ${model.description}"
        holder.fineAmountTextView.text = "Fine: ₹${model.fineAmount}"
        holder.statusTextView.text = "Status: ${model.status}"

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val violationType = model.violationType?.trim()
        val formattedTimestamp = if (model.timestamp != 0L)
            dateFormat.format(Date(model.timestamp))
        else
            "N/A"
        holder.timestampTextView.text = "Reported at: $formattedTimestamp"

        val finesCollection = FirebaseFirestore.getInstance().collection("fines")
        finesCollection.document("fineid") // Replace with your actual document ID
            .get()
            .addOnSuccessListener { document ->
                if (!violationType.isNullOrEmpty()) {
                    try {
                        val fineValue = document.get(violationType)
                        val fineAmount = fineValue?.toString() ?: "Not available"
                        holder.fineAmountTextView.text = "Fine: ₹$fineAmount"
                    } catch (e: IllegalArgumentException) {
                        android.util.Log.e("ViolationAdapter", "Invalid field path: $violationType", e)
                        holder.fineAmountTextView.text = "Fine: Not available"
                    }
                } else {
                    android.util.Log.e("ViolationAdapter", "Empty or null violationType for model: $model")
                    holder.fineAmountTextView.text = "Fine: Not available"
                }
            }
            .addOnFailureListener { exception ->
                holder.fineAmountTextView.text = "Error: ${exception.message}"
            }
    }
}
