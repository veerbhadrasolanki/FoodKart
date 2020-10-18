package com.veerbhadra.foodkart.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.veerbhadra.foodkart.R
import com.veerbhadra.foodkart.fragment.*

class MainActivity : AppCompatActivity() {

    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var headerView: View
    lateinit var textName: TextView
    lateinit var textMobileNumber: TextView
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var sharedPreference: SharedPreferences
    var previousMenuItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        sharedPreference =
            getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE)
        headerView = navigationView.getHeaderView(0)
        textName = headerView.findViewById(R.id.txtName)
        textMobileNumber = headerView.findViewById(R.id.txtMobileNumber)
        textName.text = sharedPreference.getString("name", "")
        textMobileNumber.text = "+91-" + sharedPreference.getString("mobile_number", "")
        setUpActionbar()
        openDashboard()
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when (it.itemId) {
                R.id.Dashboard -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.Profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            ProfileFragment()
                        )
                        .commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.Favourite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FavouriteFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.order -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            OrderFragment()
                        )
                        .commit()
                    supportActionBar?.title = "My Previous Orders"
                    drawerLayout.closeDrawers()
                }
                R.id.Faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FaqFragment()
                        )
                        .commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.Logout -> {
                    drawerLayout.closeDrawers()
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want exit?")
                    dialog.setPositiveButton("YES") { text, listener ->
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        sharedPreference.edit().clear().apply()
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("No") { text, listener ->
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpActionbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frameLayout,
                DashboardFragment()
            )
            .commit()
        supportActionBar?.title = "Dashboard"
        navigationView.setCheckedItem(R.id.Dashboard)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (fragment) {
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()
        }
    }
}