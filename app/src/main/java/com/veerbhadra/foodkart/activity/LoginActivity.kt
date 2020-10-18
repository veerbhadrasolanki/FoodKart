package com.veerbhadra.foodkart.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import com.veerbhadra.foodkart.util.ConnectivityManager
import android.provider.Settings
import com.veerbhadra.foodkart.R
import kotlin.collections.HashMap

class LoginActivity : AppCompatActivity() {

    lateinit var edtMobileNumber: EditText
    lateinit var edtPassword: EditText
    lateinit var btnLoginIn: Button
    lateinit var txtForgot: TextView
    lateinit var txtRegistration: TextView
    lateinit var sharedPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        edtMobileNumber = findViewById(R.id.edtMobileNumber)
        edtPassword = findViewById(R.id.edtPassword)
        btnLoginIn = findViewById(R.id.btnLogIn)
        txtForgot = findViewById(R.id.txtForgot)
        txtRegistration = findViewById(R.id.txtRegistration)
        btnLoginIn.visibility = View.VISIBLE
        sharedPreference =
            getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreference.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        title = "Log In"
        txtRegistration.setOnClickListener {
            if (ConnectivityManager().checkConnectivity(this@LoginActivity)) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()
            }
        }

        txtForgot.setOnClickListener {
            if (ConnectivityManager().checkConnectivity(this@LoginActivity)) {
                val intent = Intent(this@LoginActivity, ForgotActivity::class.java)
                startActivity(intent)
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()
            }
        }

        btnLoginIn.setOnClickListener {
            val queue = Volley.newRequestQueue(this@LoginActivity)
            val loginUrl = "http://13.235.250.119/v2/login/fetch_result/"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", edtMobileNumber.text.toString())
            jsonParams.put("password", edtPassword.text.toString())
            if (ConnectivityManager().checkConnectivity(this@LoginActivity)) {
                val jsonObjectRequest = object :
                    JsonObjectRequest(Request.Method.POST, loginUrl, jsonParams, Response.Listener {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        try {
                            if (success) {
                                val userInfoJSONObject = data.getJSONObject("data")
                                sharedPreference.edit().putString(
                                    "user_id",
                                    userInfoJSONObject.getString("user_id")
                                ).apply()
                                sharedPreference.edit().putString(
                                    "name",
                                    userInfoJSONObject.getString("name")
                                ).apply()
                                sharedPreference.edit().putString(
                                    "email",
                                    userInfoJSONObject.getString("email")
                                ).apply()
                                sharedPreference.edit().putString(
                                    "mobile_number",
                                    userInfoJSONObject.getString("mobile_number")
                                ).apply()
                                sharedPreference.edit().putString(
                                    "address",
                                    userInfoJSONObject.getString("address")
                                ).apply()
                                Toast.makeText(this, "Success!! Logged In", Toast.LENGTH_SHORT)
                                    .show()
                                savePreferences()
                                startActivity(
                                    Intent(this@LoginActivity, MainActivity::class.java)
                                )
                                finish()
                            } else {
                                btnLoginIn.visibility = View.VISIBLE
                                Toast.makeText(
                                    this,
                                    data.getString("errorMessage"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            btnLoginIn.visibility = View.VISIBLE
                            Toast.makeText(this, "Some unexpected", Toast.LENGTH_SHORT).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(this, "Volley Error Occured!!!", Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "4e616c846a2241"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            } else {
                btnLoginIn.visibility = View.VISIBLE
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    fun savePreferences() {
        sharedPreference.edit().putBoolean("isLoggedIn", true).apply()
    }

}

