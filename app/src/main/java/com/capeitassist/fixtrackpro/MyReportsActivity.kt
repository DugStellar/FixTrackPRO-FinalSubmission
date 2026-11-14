package com.capeitassist.fixtrackpro

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class MyReportsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val reportList = ArrayList<ReportModel>()
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_reports)

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.reportsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        reportAdapter = ReportAdapter(reportList)
        recyclerView.adapter = reportAdapter

        fetchMyReports()
    }

    override fun onResume() {
        super.onResume()
        fetchMyReports() // Refreshes automatically when returning to this screen
    }

    private fun fetchMyReports() {
        // Retrieve logged-in user's ID from SharedPreferences (fallback to 1)
        val sharedPref = getSharedPreferences("FixTrackPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 1)

        val baseUrl = if (NetworkConfig.BASE_URL.endsWith("/")) NetworkConfig.BASE_URL else "${NetworkConfig.BASE_URL}/"
        val url = "${baseUrl}api_get_reports.php?user_id=$userId"

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                reportList.clear()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    reportList.add(
                        ReportModel(
                            id = obj.optInt("id", 0),
                            title = obj.optString("title", "No Title"),
                            category = obj.optString("category", "General"),
                            status = obj.optString("status", "Pending"),
                            createdAt = obj.optString("created_at", "")
                        )
                    )
                }
                reportAdapter.notifyDataSetChanged()
                Log.d("MyReports", "Loaded ${reportList.size} reports successfully for user ID: $userId")
            },
            { error ->
                val reason = error.message ?: error.cause?.message ?: "Unknown network error"
                Toast.makeText(this, "Failed to load reports: $reason", Toast.LENGTH_LONG).show()
                Log.e("MyReports", "Error fetching reports", error)
            }
        )
        Volley.newRequestQueue(this).add(request)
    }
}