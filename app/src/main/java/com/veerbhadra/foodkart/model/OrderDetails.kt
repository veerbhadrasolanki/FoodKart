package com.veerbhadra.foodkart.model

import org.json.JSONArray
import com.google.gson.JsonArray

data class OrderDetails(
    val orderId: String,
    val resName: String,
    val orderDate: String,
    val foodItem: JSONArray
)