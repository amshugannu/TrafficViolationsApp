package com.example.snapfine

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MyProfileActivity : AppCompatActivity() {

    private lateinit var backbtn: ImageButton
    private lateinit var completeprofile: TextView
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var editmaterial: ImageButton

    private lateinit var fullnameedittext: EditText
    private lateinit var emailedittext: EditText
    private lateinit var phonenumberedittext: EditText
    private lateinit var vehicleNumberEditText: EditText
    private lateinit var vehicleTypeEditText: EditText
    private lateinit var registrationIdEditText: EditText
    private lateinit var licenseNumberEditText: EditText

    private lateinit var submitButton: Button
    private lateinit var editButton: Button
    private lateinit var okbtn: Button

    private lateinit var fullName: TextView
    private lateinit var email: TextView
    private lateinit var phonenumber: TextView
    private lateinit var displayVehicleNumberTextView: TextView
    private lateinit var displayVehicleTypeTextView: TextView
    private lateinit var displayRegistrationIdTextView: TextView
    private lateinit var displayLicenseNumberTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_profile)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userId = fAuth.currentUser?.uid ?: return

        // UI References
        completeprofile = findViewById(R.id.completeprofile)
        backbtn = findViewById(R.id.btn_back)
        backbtn.setOnClickListener { onBackPressed() }

        fullnameedittext = findViewById(R.id.fullnameedittext)
        emailedittext = findViewById(R.id.emailedittext)
        phonenumberedittext = findViewById(R.id.phonenumberedittext)
        vehicleNumberEditText = findViewById(R.id.vehicleNumberEditText)
        vehicleTypeEditText = findViewById(R.id.vehicleTypeEditText)
        registrationIdEditText = findViewById(R.id.registrationIdEditText)
        licenseNumberEditText = findViewById(R.id.licenseNumberEditText)

        fullName = findViewById(R.id.displayFullNameTextView)
        email = findViewById(R.id.displayEmailTextView)
        phonenumber = findViewById(R.id.displayPhoneTextView)
        displayVehicleNumberTextView = findViewById(R.id.displayVehicleNumberTextView)
        displayVehicleTypeTextView = findViewById(R.id.displayVehicleTypeTextView)
        displayRegistrationIdTextView = findViewById(R.id.displayRegistrationIdTextView)
        displayLicenseNumberTextView = findViewById(R.id.displayLicenseNumberTextView)

        submitButton = findViewById(R.id.submitButton)
        editButton = findViewById(R.id.editButton)
        editmaterial = findViewById(R.id.edit)
        okbtn = findViewById(R.id.okbtn)

        // Edit basic info button
        editmaterial.setOnClickListener {
            toggleBasicEditMode(true)
        }

        // OK button to update basic info
        okbtn.setOnClickListener {
            val fullname = fullnameedittext.text.toString().trim()
            val emailstring = emailedittext.text.toString().trim()
            val phoneNumber = phonenumberedittext.text.toString().trim()

            if (fullname.isEmpty() || emailstring.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateBasicInfoInFirestore(fullname, emailstring, phoneNumber)
        }

        // Edit vehicle info button
        editButton.setOnClickListener {
            handleEdit()
        }

        // Submit vehicle info button
        submitButton.setOnClickListener {
            handleSubmit()
        }

        // Load user data from Firestore
        loadUserData()
    }

    private fun toggleBasicEditMode(isEdit: Boolean) {
        fullnameedittext.visibility = if (isEdit) View.VISIBLE else View.GONE
        emailedittext.visibility = if (isEdit) View.VISIBLE else View.GONE
        phonenumberedittext.visibility = if (isEdit) View.VISIBLE else View.GONE
        fullName.visibility = if (isEdit) View.GONE else View.VISIBLE
        email.visibility = if (isEdit) View.GONE else View.VISIBLE
        phonenumber.visibility = if (isEdit) View.GONE else View.VISIBLE
        okbtn.visibility = if (isEdit) View.VISIBLE else View.GONE
        editmaterial.visibility = if (isEdit) View.GONE else View.VISIBLE

        if (isEdit) {
            // Pre-fill edit texts with current values
            fullnameedittext.setText(fullName.text)
            emailedittext.setText(email.text)
            phonenumberedittext.setText(phonenumber.text)
        }
    }

    private fun updateBasicInfoInFirestore(fullName: String, email: String, phoneNumber: String) {
        val userBasicInfo = hashMapOf(
            "Name" to fullName,
            "email" to email,
            "phone" to phoneNumber
        )
        fStore.collection("users").document(userId).set(userBasicInfo, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Basic Info Updated Successfully", Toast.LENGTH_SHORT).show()
                // Update UI
                this.fullName.text = fullName
                this.email.text = email
                this.phonenumber.text = phoneNumber
                toggleBasicEditMode(false)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating basic info: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserData() {
        fStore.collection("users").document(userId).get().addOnSuccessListener { doc ->
            if (doc != null && doc.exists()) {
                val name = doc.getString("Name") ?: ""
                val emailStr = doc.getString("email") ?: ""
                val phone = doc.getString("phone") ?: ""

                fullName.text = name
                email.text = emailStr
                phonenumber.text = phone

                val vehicleNumber = doc.getString("Vehicle Number")
                val vehicleType = doc.getString("Vehicle Type")
                val registrationId = doc.getString("Vehicle Registration Id")
                val licenseNumber = doc.getString("licenseNumber")

                if (!vehicleNumber.isNullOrEmpty() || !vehicleType.isNullOrEmpty() || !registrationId.isNullOrEmpty() || !licenseNumber.isNullOrEmpty()) {
                    displayVehicleNumberTextView.text = vehicleNumber ?: ""
                    displayVehicleTypeTextView.text = vehicleType ?: ""
                    displayRegistrationIdTextView.text = registrationId ?: ""
                    displayLicenseNumberTextView.text = licenseNumber ?: ""
                    toggleVehicleEditMode(false)
                } else {
                    toggleVehicleEditMode(true)
                }
            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleVehicleEditMode(isEditMode: Boolean) {
        vehicleNumberEditText.visibility = if (isEditMode) View.VISIBLE else View.GONE
        vehicleTypeEditText.visibility = if (isEditMode) View.VISIBLE else View.GONE
        registrationIdEditText.visibility = if (isEditMode) View.VISIBLE else View.GONE
        licenseNumberEditText.visibility = if (isEditMode) View.VISIBLE else View.GONE
        submitButton.visibility = if (isEditMode) View.VISIBLE else View.GONE
        completeprofile.visibility = if (isEditMode) View.VISIBLE else View.GONE

        displayVehicleNumberTextView.visibility = if (isEditMode) View.GONE else View.VISIBLE
        displayVehicleTypeTextView.visibility = if (isEditMode) View.GONE else View.VISIBLE
        displayRegistrationIdTextView.visibility = if (isEditMode) View.GONE else View.VISIBLE
        displayLicenseNumberTextView.visibility = if (isEditMode) View.GONE else View.VISIBLE
        editButton.visibility = if (isEditMode) View.GONE else View.VISIBLE
    }

    private fun handleEdit() {
        vehicleNumberEditText.setText(displayVehicleNumberTextView.text.toString())
        vehicleTypeEditText.setText(displayVehicleTypeTextView.text.toString())
        registrationIdEditText.setText(displayRegistrationIdTextView.text.toString())
        licenseNumberEditText.setText(displayLicenseNumberTextView.text.toString())
        toggleVehicleEditMode(true)
    }

    private fun handleSubmit() {
        val vehicleNumber = vehicleNumberEditText.text.toString().trim()
        val vehicleType = vehicleTypeEditText.text.toString().trim()
        val registrationId = registrationIdEditText.text.toString().trim()
        val licenseNumber = licenseNumberEditText.text.toString().trim()

        if (vehicleNumber.isEmpty() || vehicleType.isEmpty() || registrationId.isEmpty() || licenseNumber.isEmpty()) {
            Toast.makeText(this, "Please fill all vehicle fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = fStore.collection("users").document(userId)
        val vehicleData = hashMapOf(
            "Vehicle Number" to vehicleNumber,
            "Vehicle Type" to vehicleType,
            "Vehicle Registration Id" to registrationId,
            "licenseNumber" to licenseNumber
        )

        userRef.set(vehicleData, SetOptions.merge()).addOnSuccessListener {
            // Also update vehicles collection for quick lookup by vehicle number
            val vehicleMap = hashMapOf(
                "uid" to userId,
                "phone" to phonenumber.text.toString()
            )

            fStore.collection("vehicles").document(vehicleNumber)
                .set(vehicleMap, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "Vehicle mapping saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save vehicle mapping", Toast.LENGTH_SHORT).show()
                }

            // Update UI
            displayVehicleNumberTextView.text = vehicleNumber
            displayVehicleTypeTextView.text = vehicleType
            displayRegistrationIdTextView.text = registrationId
            displayLicenseNumberTextView.text = licenseNumber

            toggleVehicleEditMode(false)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update vehicle info", Toast.LENGTH_SHORT).show()
        }
    }
}
