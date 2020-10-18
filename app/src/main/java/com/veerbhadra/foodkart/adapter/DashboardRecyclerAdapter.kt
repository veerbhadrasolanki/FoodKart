package com.veerbhadra.foodkart.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.squareup.picasso.Picasso
import com.veerbhadra.foodkart.R
import com.veerbhadra.foodkart.activity.FoodItemActivity
import com.veerbhadra.foodkart.database.RestaurantDatabase
import com.veerbhadra.foodkart.database.RestaurantEntity
import com.veerbhadra.foodkart.model.Restaurant

class DashboardRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): DashboardViewHolder {

        val view = LayoutInflater.from(p0.context)
            .inflate(R.layout.recycler_dashboard_single_row, p0, false)
        return DashboardViewHolder(view)
    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(p0: DashboardViewHolder, p1: Int) {

        val restaurant = itemList[p1]
        p0.txtRestaurantName.text = restaurant.restaurantName
        p0.txtPerPersonRate.text = restaurant.restaurantCostForOne
        p0.txtRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).into(p0.imgFoodImage)
        p0.llContent.setOnClickListener {
            val intent = Intent(context, FoodItemActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restaurantId)
            intent.putExtra("restaurant_name", restaurant.restaurantName)
            context.startActivity(intent)

        }
        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()
        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(restaurant.restaurantId.toString())) {
            p0.imgFavourite.setImageResource(R.drawable.ic_favourite)
        } else {
            p0.imgFavourite.setImageResource(R.drawable.ic_action_fav)
        }
        p0.imgFavourite.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                restaurant.restaurantId,
                restaurant.restaurantName,
                restaurant.restaurantRating,
                restaurant.restaurantCostForOne,
                restaurant.restaurantImage
            )
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    p0.imgFavourite.setImageResource(R.drawable.ic_favourite)
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    p0.imgFavourite.setImageResource(R.drawable.ic_action_fav)
                }
            }
        }
    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        override fun doInBackground(vararg params: Void?): Boolean {
            /*mode 1=> Check the database to see if a book has been added to favourites or not
           * mode 2=> Save the book in the database as favourites
           * mode 3=> Remove a book from favourites*/
            when (mode) {
                1 -> {
                    //Check DB if the Book is present or not
                    val book: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.restaurantId)
                    db.close()
                    return book != null
                }
                2 -> {
                    //Save the book into DB as favourties
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    //Remove the favourites book
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}

class GetAllFavAsyncTask(
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





