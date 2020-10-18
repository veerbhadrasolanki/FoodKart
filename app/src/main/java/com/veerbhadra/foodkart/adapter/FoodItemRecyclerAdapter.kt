package com.veerbhadra.foodkart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.veerbhadra.foodkart.R
import com.veerbhadra.foodkart.model.FoodItemsList

class FoodItemRecyclerAdapter(
    val context: Context,
    private val MenuList: ArrayList<FoodItemsList>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<FoodItemRecyclerAdapter.RestaurantViewHolder>() {

    companion object {
        var isCartEmpty = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_foodlist_single_row, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return MenuList.size
    }

    interface OnItemClickListener {
        fun onAddItemClick(foodItem: FoodItemsList)
        fun onRemoveItemClick(foodItem: FoodItemsList)
    }

    override fun onBindViewHolder(
        holder: RestaurantViewHolder,
        position: Int
    ) {
        val menu = MenuList[position]
        holder.foodName.text = menu.foodName
        holder.txtFoodPrice.text = menu.foodCost
        holder.txtSNo.text = (position + 1).toString()

        holder.btnAddToCart.setOnClickListener {
            holder.btnAddToCart.visibility = View.GONE
            holder.btnRemoveFromCart.visibility = View.VISIBLE
            listener.onAddItemClick(menu)
        }
        holder.btnRemoveFromCart.setOnClickListener {
            holder.btnRemoveFromCart.visibility = View.GONE
            holder.btnAddToCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(menu)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSNo: TextView = view.findViewById(R.id.txtSNo)
        val foodName: TextView = view.findViewById(R.id.foodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
        val btnRemoveFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)
    }

}
