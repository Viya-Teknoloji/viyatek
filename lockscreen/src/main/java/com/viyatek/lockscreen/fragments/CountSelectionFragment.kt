package com.viyatek.lockscreen.fragments

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.viyatek.lockscreen.R
import com.viyatek.lockscreen.Statics
import com.viyatek.lockscreen.Statics.SHOW_FACT_COUNT
import com.viyatek.lockscreen.databinding.CountSelectionBinding
import com.viyatek.preferences.ViyatekSharedPrefsHandler

abstract class CountSelectionFragment : Fragment(), OnNumberItemSelected, OnNumberItemClicked {

    val LOG_TAG = "Count Selection"
    private val numberList = arrayListOf<Int>()
    private val sharedPrefsHandler by lazy { ViyatekSharedPrefsHandler(requireContext(), Statics.LOCK_SCREEN_PREFS) } 
    private val show_count by lazy { sharedPrefsHandler.getIntegerValue(SHOW_FACT_COUNT, 15) }

    private var _binding: CountSelectionBinding? = null

    var theQuestion : String =""
    var actionButtonText = "Continue"
    var maxCount = 15

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CountSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRequiredVariables()

        numberList.clear()
        for (x in 1..maxCount) {
            numberList.add(x)
        }

        Log.d(
            LOG_TAG,
            "Dp To Px value " + resources.getDimension(R.dimen.number_horizontal_padding)
        )

        val padding: Int =
            Resources.getSystem().displayMetrics.widthPixels / 2 - resources.getDimension(R.dimen.number_horizontal_padding)
                .toInt()

        Log.d(LOG_TAG, "Padding $padding")


        binding.numberPickerRv.setPadding(padding, 0, padding, 0)

        val adapter: NumberPickerAdapter = NumberPickerAdapter(numberList, requireContext(), this)

        binding.numberPickerRv.adapter = adapter

        val sliderLayoutManager = SliderLayoutManager(requireContext(), this, binding.numberPickerRv)

        binding.numberPickerRv.layoutManager = sliderLayoutManager
        binding.numberPickerRv.setHasFixedSize(true)

        OnNumberItemClicked(show_count)

        Log.d(LOG_TAG, "Shared Pref Item: $show_count")
        binding.theQuestion.fragmentTitle.text = theQuestion
        binding.continueButton.theContinue.setOnClickListener{ setAction() }
    }

    abstract fun setRequiredVariables()

    abstract fun setAction()

    override fun OnNumberItemClicked(position: Int) {
        Log.d(LOG_TAG, "Clicked Item: $position")
        try
        {
            binding.numberPickerRv.post { binding.numberPickerRv.smoothScrollToPosition(position) }
        }
        catch (e: IllegalArgumentException)
        {

        }

    }

    override fun OnNumberItemSelected(position: Int) {
        Log.d(LOG_TAG, "Selected Position : $position")

        if(_binding != null) {
            binding.selectedNumber.text = numberList[position].toString()
        }
        sharedPrefsHandler.applyPrefs(SHOW_FACT_COUNT, numberList[position])

    }
}