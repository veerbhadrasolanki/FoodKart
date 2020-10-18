package com.veerbhadra.foodkart.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.squareup.picasso.Picasso
import com.veerbhadra.foodkart.R
import com.veerbhadra.foodkart.activity.FoodItemActivity
import com.veerbhadra.foodkart.database.RestaurantDatabase
import com.veerbhadra.foodkart.database.RestaurantEntity
import com.veerbhadra.foodkart.model.FoodItemsList

class FavouriteRecyclerAdapter(val context: Context, val restaurantList: List<RestaurantEntity>) :
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(
        holder: FavouriteViewHolder,
        position: Int
    ) {
        val resObject = restaurantList[position]
        holder.txtRestaurantName.text = resObject.restaurantName
        holder.txtPerPersonRate.text = resObject.restaurantCostForOne
        holder.txtRating.text = resObject.restaurantRating
        Picasso.get().load(resObject.restaurantImage).into(holder.imgFoodImage)
        holder.llContent.setOnClickListener {
            val intent = Intent(context, FoodItemActivity::class.java)
            intent.putExtra("restaurant_id", resObject.restaurantId)
            intent.putExtra("restaurant_name", resObject.restaurantName)
            context.startActivity(intent)
        }
        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()
        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(resObject.restaurantId.toString())) {
            holder.imgFavourite.setImageResource(R.drawable.ic_favourite)
        } else {
            holder.imgFavourite.setImageResource(R.drawable.ic_action_fav)
        }
        holder.imgFavourite.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                resObject.restaurantId,
                resObject.restaurantName,
                resObject.restaurantRating,
                resObject.restaurantCostForOne,
                resObject.restaurantImage
            )
            if (!DashboardRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 1).execute()
                    .get()
            ) {
                val async =
                    DashboardRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.imgFavourite.setImageResource(R.drawable.ic_favourite)
                }
            } else {
                val async =
                    DashboardRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.imgFavourite.setImageResource(R.drawable.ic_action_fav)
                }
            }
        }
    }

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPerPersonRate: TextView = view.findViewById(R.id.txtCostForTwo)
        val imgFavourite: ImageView = view.findViewById(R.id.imgIsFav)
        val txtRating: TextView = view.findViewById(R.id.txtRestaurantRating)
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(params: Array<Void?>): Boolean {

            when (mode) {
                1 -> {

                    val res: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.restaurantId)
                    db.close()
                    return res != null
                }
                2 -> {

                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {

                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}

class GetAllFavAsyncTask1(
    context: Context
) :
    AsyncTask<Void, Void, List<String>>() {

    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
    override fun doInBackground(vararg params: Void?): List<String> {

        val list = db.restaurantDao().getAllRestaurants()
        val listOfIds = arrayListOf<String>()
        for (i in list) {
            listOfIds.add(i.restaurantId)
        }
        return listOfIds
    }
}