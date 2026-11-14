package com.capeitassist.fixtrackpro

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val rootView = findViewById<android.view.View>(R.id.registerLayout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nameEditText = findViewById(R.id.regNameEditText)
        emailEditText = findViewById(R.id.regEmailEditText)
        passwordEditText = findViewById(R.id.regPasswordEditText)

        val nameInputLayout = findViewById<TextInputLayout>(R.id.regNameInputLayout)
        val emailInputLayout = findViewById<TextInputLayout>(R.id.regEmailInputLayout)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.regPasswordInputLayout)

        submitButton = findViewById(R.id.registerSubmitButton)
        val backToLoginButton = findViewById<Button>(R.id.backToLoginButton)

        submitButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            var isValid = true

            if (name.isEmpty()) {
                nameInputLayout.error = "Full Name is required"
                isValid = false
            } else {
                nameInputLayout.error = null
            }

            if (email.isEmpty()) {
                emailInputLayout.error = "Email is required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.error = "Enter a valid email address"
                isValid = false
            } else {
                emailInputLayout.error = null
            }

            if (password.length < 6) {
                passwordInputLayout.error = "Password must be at least 6 characters"
                isValid = false
            } else {
                passwordInputLayout.error = null
            }

            if (isValid) {
                performRegistration(name, email, password)
            }
        }

        backToLoginButton.setOnClickListener {
            finish()
        }
    }

    private fun performRegistration(name: String, email: String, pass: String) {
        submitButton.isEnabled = false
        submitButton.text = "Registering..."

        val url = "${NetworkConfig.BASE_URL}register.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                submitButton.isEnabled = true
                submitButton.text = "Register"

                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.optString("status")
                    val message = jsonResponse.optString("message", "Registration response received")

                    if (status == "success") {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        finish() // Head back to LoginActivity
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "JSON parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                submitButton.isEnabled = true
                submitButton.text = "Register"
                Toast.makeText(this, "Network Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["name"] = name // Matched to standard PHP $_POST['name']
                params["email"] = email
                params["password"] = pass
                return params
            }
        }

        queue.add(stringRequest)
    }
}