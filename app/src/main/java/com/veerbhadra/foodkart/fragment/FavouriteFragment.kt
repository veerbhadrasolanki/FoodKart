package com.veerbhadra.foodkart.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.veerbhadra.foodkart.R
import com.veerbhadra.foodkart.adapter.FavouriteRecyclerAdapter
import com.veerbhadra.foodkart.database.RestaurantDatabase
import com.veerbhadra.foodkart.database.RestaurantEntity

class FavouriteFragment : Fragment() {
    lateinit var recyclerDashboard: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var llContentNoFav: RelativeLayout
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    var dbRestaurantList = listOf<RestaurantEntity>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerDashboard = view.findViewById(R.id.recyclerFav)
        llContentNoFav = view.findViewById(R.id.llContentNoFav)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        llContentNoFav.visibility = View.GONE
        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()
        if (dbRestaurantList.isEmpty()) {
            llContentNoFav.visibility = View.VISIBLE
        }
        if (activity != null) {
            progressLayout.visibility = View.GONE
            progressBar.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbRestaurantList)
            recyclerDashboard.adapter = recyclerAdapter
            recyclerDashboard.layoutManager = layoutManager
        }
        return view
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
            return db.restaurantDao().getAllRestaurants()
        }

    }
}
