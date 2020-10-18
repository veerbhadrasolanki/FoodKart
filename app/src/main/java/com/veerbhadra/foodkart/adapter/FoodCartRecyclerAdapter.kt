package com.veerbhadra.foodkart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.veerbhadra.foodkart.R
import com.veerbhadra.foodkart.model.FoodItemsList

class FoodCartRecyclerAdapter(val context: Context, val orderList: ArrayList<FoodItemsList>) :
    RecyclerView.Adapter<FoodCartRecyclerAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_foodcart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartObject = orderList[position]
        holder.txtFoodItem.text = cartObject.foodName
        holder.txtFoodItemPrice.text = cartObject.foodCost
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFoodItem: TextView = view.findViewById(R.id.txtCartItemName)
        val txtFoodItemPrice: TextView = view.findViewById(R.id.txtCartPrice)
    }
}