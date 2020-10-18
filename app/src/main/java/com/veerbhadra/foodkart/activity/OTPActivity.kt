package com.veerbhadra.foodkart.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.veerbhadra.foodkart.R
import com.veerbhadra.foodkart.util.ConnectivityManager
import org.json.JSONException
import org.json.JSONObject

class OTPActivity : AppCompatActivity() {
    lateinit var edtEnterOtp: EditText
    lateinit var edtEnterNewPass: EditText
    lateinit var edtEnterCnfPass: EditText
    lateinit var btnSubmit: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBarOtp: ProgressBar
    var mobileEntered: String? = "100"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        showDialog()
        edtEnterOtp = findViewById(R.id.edtEnterOtp)
        edtEnterNewPass = findViewById(R.id.edtEnterNewPass)
        edtEnterCnfPass = findViewById(R.id.edtEnterCnfPass)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressLayout = findViewById(R.id.progressLayout)
        progressBarOtp = findViewById(R.id.progressBarOtp)
        btnSubmit.visibility = View.VISIBLE
        progressLayout.visibility = View.GONE
        progressBarOtp.visibility = View.GONE
        if (intent != null) {
            mobileEntered = intent.getStringExtra("mobile1")
        }

        btnSubmit.setOnClickListener {
            progressBarOtp.visibility = View.VISIBLE
            btnSubmit.visibility = View.GONE
            val otpEntered = edtEnterOtp.text.toString()
            val newPassEntered = edtEnterNewPass.text.toString()
            val cnfPassEntered = edtEnterCnfPass.text.toString()
            if (otpEntered == "" || newPassEntered == "" || cnfPassEntered == "") {
                progressBarOtp.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                Toast.makeText(this@OTPActivity, "All Fields Required1", Toast.LENGTH_SHORT).show()
            } else {
                if (newPassEntered.length < 4 || cnfPassEntered.length < 4)
                    Toast.makeText(this, "Minimum 4 Characters Required", Toast.LENGTH_SHORT).show()
                else {
                    if (newPassEntered != cnfPassEntered) {
                        progressBarOtp.visibility = View.GONE
                        btnSubmit.visibility = View.VISIBLE
                        Toast.makeText(
                            this@OTPActivity,
                            "Password And Confirm Password Do Not Match",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val queue = Volley.newRequestQueue(this)
                        val url2 = "http://13.235.250.119/v2/reset_password/fetch_result "
                        val jsonParams2 = JSONObject()
                        jsonParams2.put("mobile_number", mobileEntered)
                        jsonParams2.put("password", newPassEntered)
                        jsonParams2.put("otp", otpEntered)
                        if (ConnectivityManager().checkConnectivity(this@OTPActivity)) {
                            val jsonRequest =
                                object : JsonObjectRequest(Method.POST, url2, jsonParams2,
                                    Response.Listener {
                                        val data = it.getJSONObject("data")
                                        val success = data.getBoolean("success")
                                        try {
                                            if (success) {
                                                val dialog = AlertDialog.Builder(this)
                                                dialog.setTitle("Confirmation")
                                                dialog.setIcon(R.drawable.ic_done)
                                                dialog.setMessage("Password has Successfully Changed.")
                                                dialog.setPositiveButton("OK") { text, listener ->
                                                    startActivity(
                                                        Intent(
                                                            this,
                                                            LoginActivity::class.java
                                                        )
                                                    )
                                                }
                                                dialog.create()
                                                dialog.show()
                                            } else {
                                                progressBarOtp.visibility = View.GONE
                                                btnSubmit.visibility = View.VISIBLE
                                                Toast.makeText(
                                                    this,
                                                    data.getString("Error Message"),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } catch (e: JSONException) {
                                            Toast.makeText(
                                                this,
                                                "Some Unexpected Error Occured!!!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }, Response.ErrorListener { }) {
                                    override fun getHeaders(): MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["Content-type"] = "application/json"
                                        headers["token"] = "4e616c846a2241"
                                        return headers
                                    }
                                }
                            queue.add(jsonRequest)
                        }
                    }
                }
            }
        }
    }


    fun showDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Information")
        dialog.setMessage("Please refer to the previous email for the OTP")
        dialog.setPositiveButton("OK") { text, listener ->
        }
        dialog.create()
        dialog.show()
    }
}
