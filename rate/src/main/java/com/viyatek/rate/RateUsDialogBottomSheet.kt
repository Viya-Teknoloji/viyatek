package com.viyatek.rate

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.play.core.review.ReviewManagerFactory
import com.viyatek.helper.EmailComposer
import com.viyatek.helper.GetScreenWidth
import com.viyatek.helper.GetThemeColor
import com.viyatek.helper.OpenPlayStore
import com.viyatek.rate.databinding.InAppRateUsDialogBinding


abstract class RateUsDialogBottomSheet : BottomSheetDialogFragment() {

    private val emailComposer by lazy { EmailComposer(requireActivity()) }
    private val playStoreOpener by lazy { OpenPlayStore(requireActivity()) }


    var inAppReviewEnabled = false
    var appName : String = "Quote to Inspire"
    var adresses = arrayOf("viyateknoloji@gmail.com")

    var _binding: InAppRateUsDialogBinding? = null
    val binding get() = _binding!!

    var rateValue = 0f
    var rateUsPosition = 3


    private val manager by lazy { ReviewManagerFactory.create(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        dialog?.window?.navigationBarColor = GetThemeColor(requireContext()).get(R.attr.colorSurface)
        //  val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.MyTheme)
      //  val localInflater = inflater.cloneInContext(contextThemeWrapper)
        _binding = InAppRateUsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRequiredValues()

        when {
            rateValue <= 4 -> {
                when (rateValue) {
                    4f -> {
                        binding.rateUsTitle.text = getString(R.string.in_app_four_start_title)
                        binding.rateUsText.text = getString(R.string.in_app_rate_us_four_star_text)
                        Glide.with(requireContext()).load(R.drawable.normal).into(binding.rateUsImage)
                    }
                    else -> {
                        binding.rateUsTitle.text = getString(R.string.in_app_below_four_start_title)
                        binding.rateUsText.text = getString(R.string.in_app_rate_us_below_four_star_text)
                        Glide.with(requireContext()).load(R.drawable.sad).into(binding.rateUsImage)
                    }
                }
                binding.rateUsAction.apply {
                    text = getString(R.string.in_app_rate_us_feedback)
                    setOnClickListener {

                        dismissAllowingStateLoss()

                        rateBelowFiveSendFeedBackClicked()

                        emailComposer.composeEmail(
                            appName,
                            adresses
                        )
                    }
                }
                binding.rateUsNoAction.apply {
                    text = getString(R.string.in_app_below_five_negative)
                    setOnClickListener {
                        dismissAllowingStateLoss()
                        rateBelowFiveNoActionClicked()
                    }
                }
            }
            else -> {
                binding.rateUsTitle.text = getString(R.string.five_star_rate_title)
                binding.rateUsText.text = getString(R.string.five_star_play_store)
                Glide.with(requireContext()).load(R.drawable.happy).into(binding.rateUsImage)
                binding.rateUsAction.apply {
                    text = getString(R.string.five_star_play_store_positive)
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
                                        playStoreOpener.Open(requireActivity().applicationContext.packageName)
                                        // There was some problem, continue regardless of the result.
                                    }
                                }
                            }
                            catch (e: Exception) {
                                inAppReviewException()

                            }
                        }
                        else { playStoreOpener.Open(requireActivity().applicationContext.packageName) }

                        dismissAllowingStateLoss()
                    }
                }
                binding.rateUsNoAction.apply {
                    text = getString(R.string.five_start_play_store_negative)
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

        if(dialog != null) {        // or since com.google.android.material:material:1.1.0-beta01
            (dialog as BottomSheetDialog?)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED

            dialog?.window?.setLayout(width * 6 / 7, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            if (dialog != null && dialog?.window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val window: Window? = dialog?.window
                window?.findViewById<View>(com.google.android.material.R.id.container)?.fitsSystemWindows =
                    false
                // dark navigation bar icons
                val decorView: View? = dialog?.window?.decorView
                decorView?.systemUiVisibility =
                    decorView?.systemUiVisibility!! or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
       // hideSystemUI();
    }






}