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
import com.google.android.play.core.review.ReviewManagerFactory
import com.viyatek.helper.EmailComposer
import com.viyatek.helper.GetScreenWidth
import com.viyatek.helper.GetThemeColor
import com.viyatek.helper.OpenPlayStore
import com.viyatek.rate.databinding.InAppRateUsDialogBinding


abstract class RateUsDialog : DialogFragment() {

    private val emailComposer by lazy { EmailComposer(requireActivity()) }
    private val playStoreOpener by lazy { OpenPlayStore(requireActivity()) }

    var inAppReviewEnabled = false
    var appName : String = "Quote to Inspire"
    var adresses = arrayOf("viyateknoloji@gmail.com")
    var extraUserInfo = ""

    var _binding: InAppRateUsDialogBinding? = null
    val binding get() = _binding!!

    var rateValue = 0f
    var rateUsPosition = 3

    lateinit var fourStarTitle : String
    lateinit var fourStarText : String
    var fourStarDrawable : Int = R.drawable.normal
    lateinit var fourStarActionButtonText : String
    lateinit var fourStarNoActionButtonText : String

    lateinit var belowFourStarTitle : String
    lateinit var belowFourStarText : String
    var belowFourStarDrawable : Int = R.drawable.sad

    lateinit var fiveStartTitle : String
    lateinit var fiveStartText : String
    var fiveStarDrawable : Int = R.drawable.happy
    lateinit var fiveStarActionButtonText : String
    lateinit var fiveStarNoActionButtonText : String


    private val manager by lazy { ReviewManagerFactory.create(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = InAppRateUsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fourStarTitle = getString(R.string.in_app_four_start_title)
        fourStarText = getString(R.string.in_app_rate_us_four_star_text)

        belowFourStarTitle = getString(R.string.in_app_below_four_start_title)
        belowFourStarText =  getString(R.string.in_app_rate_us_below_four_star_text)

        fiveStartTitle = getString(R.string.five_star_rate_title)
        fiveStartText = getString(R.string.five_star_play_store)

        fourStarActionButtonText = getString(R.string.in_app_rate_us_feedback)
        fourStarNoActionButtonText = getString(R.string.in_app_below_five_negative)

        fiveStarActionButtonText = getString(R.string.five_star_play_store_positive)
        fiveStarNoActionButtonText = getString(R.string.five_start_play_store_negative)

        setRequiredValues()

        when {
            rateValue <= 4 -> {
                when (rateValue) {
                    4f -> {
                        binding.rateUsTitle.text = fourStarTitle
                        binding.rateUsText.text = fourStarText
                        Glide.with(requireContext()).load(fourStarDrawable).into(binding.rateUsImage)
                    }
                    else -> {
                        binding.rateUsTitle.text = belowFourStarTitle
                        binding.rateUsText.text = belowFourStarText
                        Glide.with(requireContext()).load(belowFourStarDrawable).into(binding.rateUsImage)
                    }
                }
                binding.rateUsAction.apply {
                    text = fourStarActionButtonText
                    setOnClickListener {

                        dismissAllowingStateLoss()

                        rateBelowFiveSendFeedBackClicked()

                        emailComposer.composeEmail(
                            appName,
                            adresses,
                            extraUserInfo
                        )
                    }
                }
                binding.rateUsNoAction.apply {
                    text = fourStarNoActionButtonText
                    setOnClickListener {
                        dismissAllowingStateLoss()
                        rateBelowFiveNoActionClicked()
                    }
                }
            }
            else -> {
                binding.rateUsTitle.text = fiveStartTitle
                binding.rateUsText.text = fiveStartText
                Glide.with(requireContext()).load(fiveStarDrawable).into(binding.rateUsImage)
                binding.rateUsAction.apply {
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
                binding.rateUsNoAction.apply {
                    text = fiveStarNoActionButtonText
                    setOnClickListener {

                        rateFiveStarNoActionClicked()
                        dismissAllowingStateLoss()

                    }
                }
            }
        }

        binding.rateUsClose.setOnClickListener {

            rateUsCloseIconClicked()
            dismissAllowingStateLoss()
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

    override fun onResume() {
        super.onResume()

        val width: Int = GetScreenWidth().execute()

        dialog?.window.let {
            it?.setLayout(6 * width / 7, ViewGroup.LayoutParams.WRAP_CONTENT)
            it?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }






}