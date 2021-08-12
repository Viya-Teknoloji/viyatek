package com.viyatek.rate

import android.content.ActivityNotFoundException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.play.core.review.ReviewManagerFactory
import com.viyatek.helper.EmailComposer
import com.viyatek.helper.GetScreenWidth
import com.viyatek.helper.GetThemeColor
import com.viyatek.helper.OpenPlayStore
import com.viyatek.rate.databinding.FacieTypeInAppRateUsDialogBinding
import com.viyatek.rate.databinding.InAppRateUsDialogBinding


abstract class FacieTypeRateUsDialog : BottomSheetDialogFragment() {

    private val emailComposer by lazy { EmailComposer(requireActivity()) }
    private val playStoreOpener by lazy { OpenPlayStore(requireActivity()) }

    var inAppReviewEnabled = false
    var appName : String = "Facie"
    var adresses = arrayOf("hello@viyatek.io")

    var _binding: FacieTypeInAppRateUsDialogBinding? = null
    val binding get() = _binding!!

    var rateValue : Float = 0f


    lateinit var fourStarText : String
    lateinit var belowFourStarText : String

    lateinit var belowFourActionButtonText : String
    lateinit var noActionText : String


    lateinit var fiveStartText : String
    lateinit var fiveStarActionButtonText : String



    private val manager by lazy { ReviewManagerFactory.create(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FacieTypeInAppRateUsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fourStarText = ""
        belowFourStarText = ""
        fiveStartText = ""

        belowFourActionButtonText = ""
        noActionText = ""

        fiveStarActionButtonText = ""

        setRequiredValues()

        binding.rateBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->

            if(fromUser) {
                rateValue = rating
                setValues(rateValue)
                binding.root.transitionToEnd()
            }

        }


    }

    private fun setValues(rating : Float)
    {
        when {
            rateValue <= 4 -> {
                when (rateValue) {
                    4f -> {
                        binding.ratingTitle.text = fourStarText
                    }
                    else -> {
                        binding.ratingTitle.text = belowFourStarText
                    }
                }
                binding.actionButton.apply {
                    text = belowFourActionButtonText
                    setOnClickListener {

                        dismissAllowingStateLoss()

                        rateBelowFiveSendFeedBackClicked()

                        emailComposer.composeEmail(
                            appName,
                            adresses
                        )
                    }
                }
                binding.noActionButton.apply {
                    text = noActionText
                    setOnClickListener {
                        dismissAllowingStateLoss()
                        rateBelowFiveNoActionClicked()
                    }
                }
            }
            else -> {
                binding.ratingTitle.text = fiveStartText
                binding.actionButton.apply {
                    text = fiveStarActionButtonText
                    setOnClickListener {

                        rateFiveStarActionClicked()

                        // mFirebaseRemoteConfig.getBoolean("inAppReview")
                        if (inAppReviewEnabled) {
                            try {
                                val request = manager.requestReviewFlow()

                                request.addOnCompleteListener { requestResult ->
                                    if (requestResult.isSuccessful) {
                                        // We got the ReviewInfo object
                                        val reviewInfo = requestResult.result

                                        if (isAdded) {
                                            val flow = manager.launchReviewFlow(
                                                requireActivity(),
                                                reviewInfo)

                                            flow.addOnCompleteListener {

                                                inAppReviewCompleted()

                                                // The flow has finished. The API does not indicate whether the user
                                                // reviewed or not, or even whether the review dialog was shown. Thus, no
                                                // matter the result, we continue our app flow.
                                            }
                                        }
                                        else {
                                            inAppReviewException()
                                        }

                                    } else {

                                        inAppReviewFailed()
                                        openStore()
                                        // There was some problem, continue regardless of the result.
                                    }
                                }
                            }
                            catch (e: Exception) {
                                inAppReviewException()

                            }
                        }
                        else {
                            openStore()
                        }

                        dismissAllowingStateLoss()
                    }
                }
                binding.noActionButton.apply {
                    text = noActionText
                    setOnClickListener {

                        rateFiveStarNoActionClicked()
                        dismissAllowingStateLoss()

                    }
                }
            }
        }
    }

    private fun openStore() {
        try {
            Log.d("MESAJLARIM", "Opening Activity")
            playStoreOpener.Open(requireActivity().applicationContext.packageName)
        } catch (e: ActivityNotFoundException) {
            Log.d("MESAJLARIM", "Activity Not Found")
        }
    }

    abstract fun setRequiredValues()

    abstract fun rateUsCloseIconClicked()

    abstract fun rateFiveStarNoActionClicked()

    open fun inAppReviewException() {}

    open fun inAppReviewFailed() {}

    open fun inAppReviewCompleted() {}

    open fun reviewRequestSuccessfull() {}

    abstract fun rateFiveStarActionClicked()

    abstract fun rateBelowFiveNoActionClicked()

    abstract fun rateBelowFiveSendFeedBackClicked()

    abstract fun removeRateUs()

}