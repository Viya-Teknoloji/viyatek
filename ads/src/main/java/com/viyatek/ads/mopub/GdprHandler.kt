package com.viyatek.ads.mopub

import android.util.Log
import com.mopub.common.MoPub
import com.mopub.common.privacy.ConsentDialogListener
import com.mopub.mobileads.MoPubErrorCode

class GdprHandler {

    fun checkForGDPR() {
        // Get a PersonalInformationManager instance, used to query the consent dialog
        val mPersonalInfoManager = MoPub.getPersonalInformationManager()

    // Check if you must show the consent dialog
        if (mPersonalInfoManager!!.shouldAllowLegitimateInterest()) {
            val consentDialogListener: ConsentDialogListener = object : ConsentDialogListener {
                override fun onConsentDialogLoaded() {
                    // If you choose to show the consent dialog in the future, check if it is ready using isConsentDialogReady() before showing it
                    mPersonalInfoManager.showConsentDialog()
                }

                override fun onConsentDialogLoadFailed(moPubErrorCode: MoPubErrorCode) {
                    Log.d("MoPub", "Consent dialog failed to load.")
                }
            }

            // Start loading the consent dialog. This call fails if the user has opted out of ad personalization.
            if (mPersonalInfoManager.shouldShowConsentDialog()) mPersonalInfoManager.loadConsentDialog(
                consentDialogListener
            )
        }
    }
}