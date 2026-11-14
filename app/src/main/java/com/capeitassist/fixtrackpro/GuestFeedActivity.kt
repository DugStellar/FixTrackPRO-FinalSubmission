package com.capeitassist.fixtrackpro

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject

class GuestFeedActivity : AppCompatActivity() {

    private lateinit var rvReports: RecyclerView
    private lateinit var tvWelcomeUser: TextView
    private lateinit var tvStatActive: TextView
    private lateinit var bottomNav: BottomNavigationView
    private val reportList = mutableListOf<FeedReport>()
    private lateinit var adapter: FeedReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_feed)

        tvWelcomeUser = findViewById(R.id.tvWelcomeUser)
        tvStatActive = findViewById(R.id.tvStatActive)
        rvReports = findViewById(R.id.rvReports)
        bottomNav = findViewById(R.id.bottomNavigation)

        // Setup RecyclerView
        rvReports.layoutManager = LinearLayoutManager(this)
        adapter = FeedReportAdapter(reportList)
        rvReports.adapter = adapter

        // Load logged in user's name if available
        val sharedPref = getSharedPreferences("FixTrackPrefs", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "Guest")
        tvWelcomeUser.text = "Welcome, $userName!"

        // Tapping the Active card also opens MyReportsActivity
        tvStatActive.setOnClickListener {
            startActivity(Intent(this, MyReportsActivity::class.java))
        }

        // Configure Bottom Navigation
        bottomNav.selectedItemId = R.id.nav_feed
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> {
                    fetchReports()
                    true
                }
                R.id.nav_my_reports -> {
                    startActivity(Intent(this, MyReportsActivity::class.java))
                    true
                }
                R.id.nav_report -> {
                    startActivity(Intent(this, ReportActivity::class.java))
                    true
                }
                R.id.nav_raffle -> {
                    startActivity(Intent(this, RaffleActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.nav_feed
        fetchReports()
    }

    private fun fetchReports() {
        val baseUrl = if (NetworkConfig.BASE_URL.endsWith("/")) NetworkConfig.BASE_URL else "${NetworkConfig.BASE_URL}/"
        val url = "${baseUrl}api_get_reports.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val trimmed = response.trim()
                    val reportsArray = if (trimmed.startsWith("[")) {
                        JSONArray(trimmed)
                    } else {
                        val jsonObj = JSONObject(trimmed)
                        jsonObj.optJSONArray("reports") ?: JSONArray()
                    }

                    tvStatActive.text = reportsArray.length().toString()
                    reportList.clear()
                    for (i in 0 until reportsArray.length()) {
                        val obj = reportsArray.getJSONObject(i)
                        reportList.add(
                            FeedReport(
                                title = obj.optString("title", "No Title"),
                                location = obj.optString("location", obj.optString("category", "Campus")),
                                category = obj.optString("category", "General"),
                                status = obj.optString("status", "Pending"),
                                createdAt = obj.optString("created_at", "")
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Log.e("GuestFeed", "Parse error: $response", e)
                    Toast.makeText(this, "Parse Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val statusCode = error.networkResponse?.statusCode
                val reason = error.message ?: error.cause?.message ?: "Connection refused / Timeout ($url)"
                Log.e("GuestFeed", "Volley error (HTTP $statusCode): $reason", error)
                Toast.makeText(this, "Feed Error (${statusCode ?: "Net"}): $reason", Toast.LENGTH_LONG).show()
            }
        )

        stringRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(stringRequest)
    }

    data class FeedReport(
        val title: String,
        val location: String,
        val category: String,
        val status: String,
        val createdAt: String
    )

    class FeedReportAdapter(private val reports: List<FeedReport>) :
        RecyclerView.Adapter<FeedReportAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTitle: TextView = view.findViewById(R.id.tvTitle)
            val tvLocation: TextView? = view.findViewById(R.id.tvLocation)
            val tvCategory: TextView? = view.findViewById(R.id.tvCategory)
            val tvDate: TextView? = view.findViewById(R.id.tvDate)
            val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val report = reports[position]
            holder.tvTitle.text = report.title
            holder.tvLocation?.text = "Location: ${report.location}"
            holder.tvCategory?.text = "Category: ${report.category}"
            holder.tvDate?.text = report.createdAt
            holder.tvStatus.text = report.status

            when (report.status.trim().lowercase()) {
                "being fixed", "working on it", "in progress", "inprogress" -> {
                    holder.tvStatus.setBackgroundColor(Color.parseColor("#1E3A8A"))
                    holder.tvStatus.setTextColor(Color.parseColor("#38BDF8")) // Sky Blue
                }
                "progressed" -> {
                    holder.tvStatus.setBackgroundColor(Color.parseColor("#4C1D95"))
                    holder.tvStatus.setTextColor(Color.parseColor("#A78BFA")) // Purple
                }
                "repaired", "fixed", "resolved", "completed" -> {
                    holder.tvStatus.setBackgroundColor(Color.parseColor("#064E3B"))
                    holder.tvStatus.setTextColor(Color.parseColor("#34D399")) // Emerald Green
                }
                else -> {
                    holder.tvStatus.setBackgroundColor(Color.parseColor("#78350F"))
                    holder.tvStatus.setTextColor(Color.parseColor("#FBBF24")) // Amber Yellow
                }
            }
        }

        override fun getItemCount() = reports.size
    }
}