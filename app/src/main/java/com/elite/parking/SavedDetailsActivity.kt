package com.elite.parking

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class SavedDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_details)

        // Get the data passed from the previous activity
        val vehicleNo = intent.getStringExtra("vehicleNo")
        val hookNo = intent.getStringExtra("hookNo")
        // Retrieve other details similarly

        // Display the data in the views
        findViewById<TextView>(R.id.vehicleNoTextView).text = vehicleNo
        findViewById<TextView>(R.id.hookNoTextView).text = hookNo
        // Set other text views similarly
    }

    override fun onBackPressed() {
        super.onBackPressed()  // Navigate back to the previous activity
    }
}
