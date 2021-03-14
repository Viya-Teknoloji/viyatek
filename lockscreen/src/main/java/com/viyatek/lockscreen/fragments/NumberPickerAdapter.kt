package com.viyatek.lockscreen.fragments

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.viyatek.lockscreen.R

class NumberPickerAdapter(val numberList: ArrayList<Int>, val context: Context, val onNumberItemClicked: OnNumberItemClicked) : RecyclerView.Adapter<NumberPickerViewHolder>() {

    val LOG_TAG = "Number Picker"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NumberPickerViewHolder {
        return NumberPickerViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.number_picker_number_layout, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: NumberPickerViewHolder,
        position: Int
    ) {

        holder.numberText.text = numberList[position].toString()

        holder.itemView.setOnClickListener {
            onNumberItemClicked.OnNumberItemClicked(position)
            Log.d(LOG_TAG, "On Position Clicked  $position")
        }
    }

    override fun getItemCount(): Int {
        return numberList.size
    }


}

    class NumberPickerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val numberText: TextView = itemView.findViewById<TextView>(R.id.numberText)
    }