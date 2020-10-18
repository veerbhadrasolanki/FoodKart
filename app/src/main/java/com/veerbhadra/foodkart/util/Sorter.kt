package com.veerbhadra.foodkart.util

import com.veerbhadra.foodkart.model.Restaurant

class Sorter {
    companion object {
        var costComparator = Comparator<Restaurant> { res1, res2 ->
            val costOne = res1.restaurantCostForOne
            val costTwo = res2.restaurantCostForOne
            if (costOne.compareTo(costTwo) == 0) {
                ratingComparator.compare(res1, res2)
            } else {
                costOne.compareTo(costTwo)
            }
        }

        var ratingComparator = Comparator<Restaurant> { res1, res2 ->
            val ratingOne = res1.restaurantRating
            val ratingTwo = res2.restaurantRating
            if (ratingOne.compareTo(ratingTwo) == 0) {
                val costOne = res1.restaurantCostForOne
                val costTwo = res2.restaurantCostForOne
                costOne.compareTo(costTwo)
            } else {
                ratingOne.compareTo(ratingTwo)
            }
        }
    }

}