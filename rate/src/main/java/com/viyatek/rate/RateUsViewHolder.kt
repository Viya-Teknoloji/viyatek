package com.viyatek.rate

import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class RateUsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var rateUsConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.rate_us_constraint)
    var rateUsDidYouLikeText: TextView = itemView.findViewById(R.id.did_you_like_text)
    var rateUsRatingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    var rateUsDetailedText: TextView? = itemView.findViewById(R.id.rate_us_detail_text)
}