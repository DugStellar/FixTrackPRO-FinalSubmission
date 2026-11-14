package com.capeitassist.fixtrackpro

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONArray
import org.json.JSONObject

class RaffleActivity : AppCompatActivity() {

    private lateinit var tvRaffleTitle: TextView
    private lateinit var tvRaffleDescription: TextView
    private lateinit var btnEnterRaffle: Button
    private lateinit var progressBar: ProgressBar

    private var activeRaffleId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raffle)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        tvRaffleTitle = findViewById(R.id.tvRaffleTitle)
        tvRaffleDescription = findViewById(R.id.tvRaffleDescription)
        btnEnterRaffle = findViewById(R.id.btnEnterRaffle)
        progressBar = findViewById(R.id.progressBar)

        // Fetch live active raffle details from dedicated API endpoint
        fetchActiveRaffle()

        btnEnterRaffle.setOnClickListener {
            submitRaffleEntry()
        }
    }

    private fun fetchActiveRaffle() {
        progressBar.visibility = View.VISIBLE
        val url = "${NetworkConfig.BASE_URL}api_get_raffles.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    val trimmed = response.trim()
                    val rafflesArray = if (trimmed.startsWith("[")) {
                        JSONArray(trimmed)
                    } else {
                        val jsonResponse = JSONObject(trimmed)
                        jsonResponse.optJSONArray("raffles") ?: JSONArray()
                    }

                    if (rafflesArray.length() > 0) {
                        val raffleObj = rafflesArray.getJSONObject(0)
                        activeRaffleId = raffleObj.optInt("id", raffleObj.optInt("raffle_id", 1))
                        val title = raffleObj.optString("title", raffleObj.optString("prize", "Community Hero Raffle"))
                        val description = raffleObj.optString("description", "Enter now to win community rewards!")

                        tvRaffleTitle.text = title
                        tvRaffleDescription.text = description
                        btnEnterRaffle.isEnabled = true
                    } else {
                        tvRaffleTitle.text = "No Active Raffles"
                        tvRaffleDescription.text = "Check back soon for new community hero draws!"
                        btnEnterRaffle.isEnabled = false
                    }
                } catch (e: Exception) {
                    Log.e("FixTrackRaffle", "Parsing error: ${e.message}")
                    Toast.makeText(this, "Parse Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Network Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(stringRequest)
    }

    private fun submitRaffleEntry() {
        val sharedPref = getSharedPreferences("FixTrackPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 1)

        btnEnterRaffle.isEnabled = false
        btnEnterRaffle.text = "Entering Draw..."

        val url = "${NetworkConfig.BASE_URL}api_enter_raffle.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                btnEnterRaffle.isEnabled = true
                btnEnterRaffle.text = "Enter Raffle Now"
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.optString("status")
                    val message = jsonResponse.optString("message")

                    if (status == "success") {
                        val ticketNumber = jsonResponse.optString("ticket_number", "FTP-12345")
                        Toast.makeText(this, "🎟️ Entry Confirmed!\nTicket: $ticketNumber", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Parse Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                btnEnterRaffle.isEnabled = true
                btnEnterRaffle.text = "Enter Raffle Now"
                Toast.makeText(this, "Network Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userId.toString()
                params["raffle_id"] = activeRaffleId.toString()
                return params
            }
        }

        queue.add(stringRequest)
    }
}
