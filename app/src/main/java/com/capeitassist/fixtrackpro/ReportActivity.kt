package com.capeitassist.fixtrackpro

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar

class ReportActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etLocation: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnAttachImage: Button
    private lateinit var tvImageStatus: TextView
    private lateinit var btnSubmitReport: Button

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            tvImageStatus.text = "Image attached successfully"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle Back Arrow Button click
        toolbar.setNavigationOnClickListener {
            finish()
        }

        etTitle = findViewById(R.id.etTitle)
        etLocation = findViewById(R.id.etLocation)
        etDescription = findViewById(R.id.etDescription)
        btnAttachImage = findViewById(R.id.btnAttachImage)
        tvImageStatus = findViewById(R.id.tvImageStatus)
        btnSubmitReport = findViewById(R.id.btnSubmitReport)

        btnAttachImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSubmitReport.setOnClickListener {
            submitReport()
        }
    }

    private fun getImageBase64(): String {
        val uri = selectedImageUri ?: return ""
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: return ""
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun submitReport() {
        val title = etTitle.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (title.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields (Title, Location, Description)", Toast.LENGTH_SHORT).show()
            return
        }

        val baseUrl = if (NetworkConfig.BASE_URL.endsWith("/")) NetworkConfig.BASE_URL else "${NetworkConfig.BASE_URL}/"
        val url = "${baseUrl}api_submit_report.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                Toast.makeText(this, "Report Submitted Successfully!", Toast.LENGTH_SHORT).show()
                finish() // Return to Home Feed
            },
            { error ->
                val statusCode = error.networkResponse?.statusCode
                val networkData = error.networkResponse?.data?.let { String(it) } ?: ""
                val reason = error.message ?: error.cause?.message ?: "Unknown error"

                Log.e("ReportSubmit", "Error HTTP $statusCode: $reason | Data: $networkData", error)
                Toast.makeText(this, "Submit Failed ($statusCode): ${if (networkData.isNotEmpty()) networkData else reason}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["title"] = title
                params["location"] = location
                params["category"] = "General"
                params["description"] = description

                val sharedPref = getSharedPreferences("FixTrackPrefs", Context.MODE_PRIVATE)
                val userId = sharedPref.getInt("user_id", 1).toString()
                params["user_id"] = userId

                val imageBase64 = getImageBase64()
                if (imageBase64.isNotEmpty()) {
                    params["image_base64"] = imageBase64
                    params["image_name"] = "report_${System.currentTimeMillis()}.jpg"
                }

                return params
            }
        }

        queue.add(stringRequest)
    }
}