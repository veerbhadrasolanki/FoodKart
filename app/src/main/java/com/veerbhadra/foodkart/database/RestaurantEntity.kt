package com.veerbhadra.foodkart.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant")
data class RestaurantEntity(
    @PrimaryKey @ColumnInfo(name = "restaurant_id") val restaurantId: String,
    @ColumnInfo(name = "restaurant_name") val restaurantName: String,
    @ColumnInfo(name = "restaurant_rating") val restaurantRating: String,
    @ColumnInfo(name = "restaurant_cost_for_one") val restaurantCostForOne: String,
    @ColumnInfo(name = "restaurant_image") val restaurantImage: String
)
