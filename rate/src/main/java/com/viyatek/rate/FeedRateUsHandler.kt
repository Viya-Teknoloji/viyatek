package com.viyatek.rate

import android.graphics.Typeface
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import com.viyatek.rate.interfaces.RateUsDone

class FeedRateUsHandler(private val fragment: Fragment, private val rateUsDone: RateUsDone) {

    fun handle(theRate: Float, rateUsViewHolder: RateUsViewHolder, typeface: Typeface?) {

        rateUsViewHolder.rateUsRatingBar.rating = theRate

        typeface?.let {font->
            rateUsViewHolder.rateUsDidYouLikeText.typeface = font
            rateUsViewHolder.rateUsDetailedText?.let { it.typeface = font }
        }

        rateUsViewHolder.rateUsRatingBar.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
                    if (fromUser) {
                        rateUsDone.userFinishedRating(rating)
                    }
                }
    }
}