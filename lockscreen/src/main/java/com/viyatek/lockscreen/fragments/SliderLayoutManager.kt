package com.viyatek.lockscreen.fragments

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class SliderLayoutManager(val context: Context, val onNumberItemSelected: OnNumberItemSelected, var recyclerView: RecyclerView ) : LinearLayoutManager(context) {

    init { orientation = HORIZONTAL }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        recyclerView = view!!

        // Smart snapping
        LinearSnapHelper().attachToRecyclerView(recyclerView)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        scaleDownView()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            scaleDownView()
            return scrolled
        } else {
            return 0
        }
    }

    private fun scaleDownView() {
        val mid = width / 2.0f
        for (i in 0 until childCount) {

            // Calculating the distance of the child from the center
            val child = getChildAt(i)
            val childMid = (getDecoratedLeft(child!!) + getDecoratedRight(child)) / 2.0f
            val distanceFromCenter = Math.abs(mid - childMid)

            // The scaling formula
            val scale = 1-Math.sqrt((distanceFromCenter/width).toDouble()).toFloat()*0.66f

            val alpha = 1-Math.sqrt((distanceFromCenter/width).toDouble()).toFloat()
            // Set scale to view
            child.alpha = alpha

            if(scale != Float.POSITIVE_INFINITY && scale != Float.NEGATIVE_INFINITY)
            {
                child.scaleX = scale
                child.scaleY = scale
            }

        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        // When scroll stops we notify on the selected item
        if (state.equals(RecyclerView.SCROLL_STATE_IDLE)) {

            // Find the closest child to the recyclerView center --> this is the selected item.
            val recyclerViewCenterX = getRecyclerViewCenterX()
            var minDistance = recyclerView.width
            var position = -1
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                val childCenterX = getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2
                val newDistance = Math.abs(childCenterX - recyclerViewCenterX)
                if (newDistance < minDistance) {
                    minDistance = newDistance
                    position = recyclerView.getChildLayoutPosition(child)
                }
            }


            Log.d("Slider Layout", "Posiiton Called $position")
            // Notify on item selection
            onNumberItemSelected.OnNumberItemSelected(position)
        }
    }

    private fun getRecyclerViewCenterX() : Int {
        return (recyclerView.right - recyclerView.left)/2 + recyclerView.left
    }

}