package com.veerbhadra.foodkart.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.veerbhadra.foodkart.R
import com.veerbhadra.foodkart.adapter.FoodItemRecyclerAdapter
import com.veerbhadra.foodkart.database.OrderDatabase
import com.veerbhadra.foodkart.database.OrderEntity
import com.veerbhadra.foodkart.model.FoodItemsList
import com.veerbhadra.foodkart.util.ConnectivityManager
import org.json.JSONException


class FoodItemActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var progressBar: ProgressBar
    lateinit var recyclerFoodList: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FoodItemRecyclerAdapter
    lateinit var sharedPreference: SharedPreferences
    val menuInfoList = ArrayList<FoodItemsList>()
    var orderList = ArrayList<FoodItemsList>()


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var btnGoToCart: Button
        var resId: String? = "100"
        var resName: String? = "name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foodlist)

        toolbar = findViewById(R.id.toolbar)
        btnGoToCart = findViewById(R.id.btnGoToCart)
        recyclerFoodList = findViewById(R.id.recyclerFoodList)
        layoutManager = LinearLayoutManager(this)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE
        btnGoToCart.visibility = View.GONE
        sharedPreference =
            getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE)

        if (intent != null) {
            resId = intent.getStringExtra("restaurant_id")
            resName = intent.getStringExtra("restaurant_name")
        } else {
            finish()
            Toast.makeText(this, "Some Unexpected Error Occured!", Toast.LENGTH_SHORT).show()
        }
        setUpToolbar()

        if (ConnectivityManager().checkConnectivity(this)) {
            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/${resId}"
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            progressBar.visibility = View.GONE
                            progressLayout.visibility = View.GONE
                            val resMenuArray = data.getJSONArray("data")
                            for (i in 0 until resMenuArray.length()) {
                                val restaurantJsonObject = resMenuArray.getJSONObject(i)
                                val resDetailObject = FoodItemsList(
                                    foodId = restaurantJsonObject.getString("id"),
                                    foodName = restaurantJsonObject.getString("name"),
                                    foodCost = restaurantJsonObject.getString("cost_for_one")
                                )
                                menuInfoList.add(resDetailObject)
                                recyclerAdapter =
                                    FoodItemRecyclerAdapter(this@FoodItemActivity, menuInfoList,
                                        object : FoodItemRecyclerAdapter.OnItemClickListener {
                                            override fun onAddItemClick(resDetailOject: FoodItemsList) {
                                                orderList.add(resDetailObject)
                                                if (orderList.size > 0) {
                                                    btnGoToCart.visibility = View.VISIBLE
                                                    FoodItemRecyclerAdapter.isCartEmpty = false
                                                }
                                            }

                                            override fun onRemoveItemClick(resDetailObject: FoodItemsList) {
                                                orderList.remove(resDetailObject)
                                                if (orderList.isEmpty()) {
                                                    btnGoToCart.visibility = View.GONE
                                                    FoodItemRecyclerAdapter.isCartEmpty = true
                                                }
                                            }
                                        }
                                    )
                                recyclerFoodList.adapter = recyclerAdapter
                                recyclerFoodList.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Some Unexpected Error Occured!!!",
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
                },
                    Response.ErrorListener {
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
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is Not Found!!!")
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

        btnGoToCart.setOnClickListener {
            val gson = Gson()
            val foodItems = gson.toJson(orderList)
            val async = InsertDBAsyncTask(applicationContext, resId.toString(), foodItems).execute()
            val success = async.get()
            if (success) {
                val data = Bundle()
                data.putInt("resId", resId as Int)
                data.putString("resName", resName)
                val intent = Intent(this, FoodCartActivity::class.java)
                intent.putExtra("data", data)
                startActivity(intent)
            }
        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = resName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        if (orderList.size > 0) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Confirmation")
            dialog.setMessage("Going Back Will Reset Cart Items.Do You Still Want To Proceed?")
            dialog.setPositiveButton("YES") { text, listener ->
                super.onBackPressed()
            }
            dialog.setNegativeButton("NO") { text, listener ->
            }
            dialog.create()
            dialog.show()
        } else if (orderList.isEmpty()) {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (orderList.size > 0) {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Confirmation")
                dialog.setMessage("Going Back Will Reset Cart Items.Do You Still Want To Proceed?")
                dialog.setPositiveButton("YES") { text, listener ->
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                dialog.setNegativeButton("NO") { text, listener ->
                }
                dialog.create()
                dialog.show()
            } else if (orderList.isEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class InsertDBAsyncTask(
        val context: Context,
        private val resId: String,
        private val foodItems: String
    ) : AsyncTask<Void, Void, Boolean>() {
        private val db =
            Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().insertOrder(OrderEntity(resId, foodItems))
            db.close()
            return true
        }
    }
}