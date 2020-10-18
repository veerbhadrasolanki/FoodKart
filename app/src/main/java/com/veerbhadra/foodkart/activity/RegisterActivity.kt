package com.veerbhadra.foodkart.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.veerbhadra.foodkart.R
import com.veerbhadra.foodkart.activity.MainActivity
import com.veerbhadra.foodkart.adapter.FoodItemRecyclerAdapter
import org.json.JSONObject
import com.veerbhadra.foodkart.util.ConnectivityManager
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    lateinit var edtName: EditText
    lateinit var edtEmail: EditText
    lateinit var edtDelivery: EditText
    lateinit var edtMobileNumber: EditText
    lateinit var edtPassword: EditText
    lateinit var edtConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var toolBar: Toolbar
    var emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtDelivery = findViewById(R.id.edtDelivery)
        edtMobileNumber = findViewById(R.id.edtMobileNumber)
        btnRegister = findViewById(R.id.btnRegister)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        sharedPrefs = getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE)
        toolBar = findViewById(R.id.toolbar)
        setUpToolbar()
        btnRegister.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val phn = edtMobileNumber.text.toString()
            val address = edtDelivery.text.toString()
            val pwd = edtPassword.text.toString()
            val confirmPwd = edtConfirmPassword.text.toString()

            if ((name == "") || (email == "") || (phn == "") || (address == "") || (pwd == "") || (confirmPwd == "")) {
                Toast.makeText(this@RegisterActivity, "All Fields Required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (phn.length != 10)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Phone Number Should Be Of Exact 10 Digits",
                        Toast.LENGTH_SHORT
                    ).show()
                if (pwd.length < 4 || confirmPwd.length < 4)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Minimum 4 Characters For Password",
                        Toast.LENGTH_SHORT
                    ).show()
                if (pwd != confirmPwd)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Password And Confirm Password Do Not Match",
                        Toast.LENGTH_SHORT
                    ).show()
                if (!email.trim().matches(emailPattern))
                    Toast.makeText(this@RegisterActivity, "Invalid Email", Toast.LENGTH_SHORT)
                        .show()
                else {
                    val queue = Volley.newRequestQueue(this)
                    val url = "http://13.235.250.119/v2/register/fetch_result/"
                    val jsonParams = JSONObject()
                    jsonParams.put("name", name)
                    jsonParams.put("mobile_number", phn)
                    jsonParams.put("password", pwd)
                    jsonParams.put("address", address)
                    jsonParams.put("email", email)
                    if (ConnectivityManager().checkConnectivity(this@RegisterActivity)) {

                        val jsonRequest =
                            object : JsonObjectRequest(
                                Method.POST, url, jsonParams,
                                Response.Listener {
                                    try {

                                        val item = it.getJSONObject("data")
                                        val success = item.getBoolean("success")
                                        if (success) {

                                            val userInfoJSONObject = item.getJSONObject("data")
                                            sharedPrefs.edit().putString(
                                                "user_id",
                                                userInfoJSONObject.getString("user_id")
                                            ).apply()
                                            sharedPrefs.edit().putString(
                                                "name",
                                                userInfoJSONObject.getString("name")
                                            ).apply()
                                            sharedPrefs.edit().putString(
                                                "email",
                                                userInfoJSONObject.getString("email")
                                            ).apply()
                                            sharedPrefs.edit().putString(
                                                "mobile_number",
                                                userInfoJSONObject.getString("mobile_number")
                                            ).apply()
                                            sharedPrefs.edit().putString(
                                                "address",
                                                userInfoJSONObject.getString("address")
                                            ).apply()
                                            startActivity(
                                                Intent(
                                                    this@RegisterActivity,
                                                    MainActivity::class.java
                                                )
                                            )
                                        } else {

                                            Toast.makeText(
                                                this,
                                                ("${item.getString("errorMessage")}"),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this,
                                            "Some Exception Occured $e",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }, Response.ErrorListener {

                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] = "4e616c846a2241"
                                    return headers
                                }

                            }
                        queue.add(jsonRequest)
                    } else {
                        val dialog = AlertDialog.Builder(this)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection Not Found. Turn On Internet Connection And Restart App")
                        dialog.setPositiveButton("Close") { text, listner ->
                            ActivityCompat.finishAffinity(this)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

}

