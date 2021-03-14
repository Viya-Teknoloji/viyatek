package com.viyatek.lockscreen.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.viyatek.lockscreen.R
import com.viyatek.lockscreen.ScreenDisplayCoordinator
import com.viyatek.lockscreen.Statics
import com.viyatek.lockscreen.Statics.IS_AFTERNOON_OK
import com.viyatek.lockscreen.Statics.IS_EVENING_OK
import com.viyatek.lockscreen.Statics.IS_LOCK_SCREEN_NOTIFICATION_OK
import com.viyatek.lockscreen.Statics.IS_LOCK_SCREEN_OK
import com.viyatek.lockscreen.Statics.IS_MORNING_OK
import com.viyatek.lockscreen.Statics.IS_NIGHT_OK
import com.viyatek.lockscreen.databinding.PeriodFragmentBinding
import com.viyatek.preferences.ViyatekSharedPrefsHandler

abstract class PeriodFragment : Fragment() {

    private val sharedPrefsHandler by lazy {
        ViyatekSharedPrefsHandler(
            requireContext(),
            Statics.LOCK_SCREEN_PREFS
        )
    }
    private val scd by lazy { ScreenDisplayCoordinator(requireContext()) }

    private val words by lazy { ArrayList<String>() }
    private var thiscontainer: ViewGroup? = null
    private var appStartUpCompleted = false
    private var _binding: PeriodFragmentBinding? = null
    private val isLockScreenOk by lazy {
        sharedPrefsHandler.getBooleanValue(
            IS_LOCK_SCREEN_OK,
            true
        )
    }
    private val isLockScreenNotificationOk by lazy {
        sharedPrefsHandler.getBooleanValue(
            IS_LOCK_SCREEN_NOTIFICATION_OK,
            true
        )
    }


    var isInformativeTextVisible = false
    var titleText: String = "Which Period?"
    var actionButtonText: String = "Continue"


    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = PeriodFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRequiredVariables()

        if (isInformativeTextVisible) {
            binding.periodInformativeText.visibility = View.VISIBLE
        } else {
            binding.periodInformativeText.visibility = View.GONE
        }

        binding.quotePeriodTitle.fragmentTitle.text = titleText
        binding.quotePeriodContinueButton.theContinue.text = actionButtonText

        createInformativeText()

        bindData()
    }

    abstract fun setRequiredVariables()

    private fun bindData() {
        binding.morningCheckbox.apply {
            isChecked = sharedPrefsHandler.getBooleanValue(IS_MORNING_OK, true)
            setOnClickListener {
                binding.morningCheckbox.isChecked = !binding.morningCheckbox.isChecked
            }
            setOnCheckedChangeListener { card, isChecked ->
                if(checkControl(card))
                { sharedPrefsHandler.applyPrefs(IS_MORNING_OK, isChecked) }
                createInformativeText()
            }

        }
        binding.noonCheckBox.apply {
            isChecked= sharedPrefsHandler.getBooleanValue(IS_AFTERNOON_OK, true)
            setOnClickListener {
                binding.noonCheckBox.isChecked = !binding.noonCheckBox.isChecked
            }
            setOnCheckedChangeListener { card, isChecked ->
                if(checkControl(card))
                { sharedPrefsHandler.applyPrefs(IS_AFTERNOON_OK, isChecked) }
                createInformativeText()
            }

        }
        binding.eveningCheckbox.apply {
            isChecked = sharedPrefsHandler.getBooleanValue(IS_EVENING_OK, true)
            setOnClickListener {
                binding.eveningCheckbox.isChecked = !binding.eveningCheckbox.isChecked
            }
            setOnCheckedChangeListener { card, isChecked ->
                if(checkControl(card))
                { sharedPrefsHandler.applyPrefs(IS_EVENING_OK, isChecked) }
                createInformativeText()
            }

        }
        binding.nightCheckbox.apply {
            isChecked= sharedPrefsHandler.getBooleanValue(IS_NIGHT_OK, false)
            setOnClickListener {
                binding.nightCheckbox.isChecked = !binding.nightCheckbox.isChecked
            }
            setOnCheckedChangeListener { card, isChecked ->
                if(checkControl(card))
                { sharedPrefsHandler.applyPrefs(IS_NIGHT_OK, isChecked) }
                createInformativeText()
            }

        }
        binding.quotePeriodContinueButton.theContinue.setOnClickListener {
            sharedPrefsHandler.applyPrefs(IS_MORNING_OK,binding.morningCheckbox.isChecked)
            sharedPrefsHandler.applyPrefs(IS_AFTERNOON_OK,binding.noonCheckBox.isChecked)
            sharedPrefsHandler.applyPrefs(IS_EVENING_OK,binding.eveningCheckbox.isChecked)
            sharedPrefsHandler.applyPrefs(IS_NIGHT_OK,binding.nightCheckbox.isChecked)

            if ((isLockScreenOk || isLockScreenNotificationOk) && appStartUpCompleted) {
                scd.updateFutureTime()
                setAlarm()
            }

            setPeriodFragmentAction()
        }
    }

    abstract fun setAlarm()

    private fun createInformativeText() {
        createWordsList()
        var wholeInformativeText: String = resources.getString(R.string.period_informative_text)
        wholeInformativeText += handleInformativeText() + resources.getString(R.string.period) + handleS()
        binding.periodInformativeText.text = wholeInformativeText
    }

    private fun handleInformativeText(): String {
        if (words.size == 1) {
            return words[0] + " "
        }
        if (words.size == 2) {
            return words[0] + " and " + words[1] + " "
        }
        val resultStr = StringBuilder()
        if (words.size > 0) {
            for (i in words.indices) {
                resultStr.append(words[i])
                if (i == words.size - 1) {
                    resultStr.append(" ")
                } else if (i == words.size - 2) {
                    resultStr.append(", and ")
                } else {
                    resultStr.append(", ")
                }
            }
        }
        return resultStr.toString()
    }

    private fun handleS(): String {
        return if (words.size > 1) "s" else {
            ""
        }
    }

    private fun createWordsList() {

        words.clear()

        if (sharedPrefsHandler.getBooleanValue(IS_MORNING_OK, true)) {
            words.add(resources.getString(R.string.first_period))
        }
        if (sharedPrefsHandler.getBooleanValue(IS_AFTERNOON_OK, true)) {
            words.add(resources.getString(R.string.second_period))
        }
        if (sharedPrefsHandler.getBooleanValue(IS_EVENING_OK, true)) {
            words.add(getResources().getString(R.string.third_period))
        }
        if (sharedPrefsHandler.getBooleanValue(IS_NIGHT_OK, false)) {
            words.add(getResources().getString(R.string.fourth_period))
        }
    }
    
    abstract fun setPeriodFragmentAction()

    private fun checkControl(card: MaterialCardView) : Boolean
    {
        if (!binding.morningCheckbox.isChecked && !binding.noonCheckBox.isChecked && !binding.eveningCheckbox.isChecked && !binding.nightCheckbox.isChecked) {

            card.isChecked = true

            val newToast = Toast.makeText(
                context,
                "You have to select at least one period",
                Toast.LENGTH_SHORT
            )
            newToast.setGravity(Gravity.CENTER, 0, 0)
            newToast.show()
            return false
        }

        return true
    }

}