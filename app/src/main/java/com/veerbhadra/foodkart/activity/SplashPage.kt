package com.veerbhadra.foodkart.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.veerbhadra.foodkart.R

class SplashPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashpage)
        Handler().postDelayed({
            val intent = Intent(this@SplashPage, LoginActivity::class.java)
            startActivity(intent)
        }, 2000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
