package com.viyatek.inappads

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.net.toUri
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.viyatek.helper.OpenInstagram
import com.viyatek.helper.OpenPlayStore
import com.viyatek.helper.OpenTwitter


import com.viyatek.inappads.databinding.SocialMediaBinding
import com.viyatek.inappads.databinding.ViyatekAppInstallAdBinding
import java.util.*
import kotlin.collections.ArrayList


class ViyatekAdHandler(private val activity: Activity,
                       private val dynamicLinkPrefix : String? = null,
                       private val iInAppListener: iInAppListener? = null) {

    private val socialMediaView by lazy { SocialMediaBinding.inflate(LayoutInflater.from(activity)) }
    private val installAdView by lazy { ViyatekAppInstallAdBinding.inflate(LayoutInflater.from(activity)) }
    private val openInstagram by lazy { OpenInstagram(activity) }
    private val openTwitter by lazy { OpenTwitter(activity) }
    private val playStore by lazy { OpenPlayStore(activity) }

    private var adReference : String? = null
    private val theList = ArrayList<String>()
    private val viyatekInAppPrefHandlers by lazy { ViyatekInAppPrefHandlers(activity) }

    private fun createAvailableAdsList() : ArrayList<String>
    {
        theList.clear()


        viyatekInAppPrefHandlers.apply {
            theList.apply {
                if(!isUfInstagramClicked()) { add(ViyatekInAppAdTypes.ULTIMATE_FACTS_INSTAGRAM.name) }
                if(!isUfTwitterClicked()) { add(ViyatekInAppAdTypes.ULTIMATE_FACTS_TWITTER.name) }
                if(!isUqInstagramClicked()) {add(ViyatekInAppAdTypes.ULTIMATE_QUOTES_INSTAGRAM.name)}
                if(!isBasketBustersAdClicked()) { add(ViyatekInAppAdTypes.BASKET_BUSTERS.name) }
                if(!isBottleCapChallangeAdClicked()) { add(ViyatekInAppAdTypes.BOTTLE_CAP_CHALLANGE.name) }
                if(!isColorUpAdClicked()) { add(ViyatekInAppAdTypes.COLOR_UP.name) }
                if(!isFactClicked() && !activity.packageName.contains("ultimatefacts")) { add(ViyatekInAppAdTypes.FACTS.name) }
                if(!isUqClicked() && !activity.packageName.contains("ultimatequotes")) { add(ViyatekInAppAdTypes.QUOTES.name) }
                if(!isMotilifeClicked() &&  !activity.packageName.contains("motilife")) { add(ViyatekInAppAdTypes.MOTILIFE.name) }
                if(!faceFindClicked() && checkThisApp("com.vkontakte.android")) {add(ViyatekInAppAdTypes.FACE_FIND.name)}
            }

        }

        return theList
    }

    fun handleAds(container : FrameLayout, isInternetOn : Boolean)
    {

        val allAds = createAvailableAdsList()

        if(allAds.isEmpty()) return

        adReference = findAdReference(allAds)

        if(container.childCount >0)
        { container.removeAllViews(); }

        createAd(container, isInternetOn);
    }

    private fun createAd(container: FrameLayout, isInternetOn: Boolean) {
        if (!activity.isFinishing || !activity.isDestroyed) {
            when (adReference) {
                ViyatekInAppAdTypes.ULTIMATE_FACTS_INSTAGRAM.name -> {
                    container.apply {
                        addView(socialMediaView.root)
                        setOnClickListener {
                            iInAppListener?.inAppAdClicked(
                                ViyatekInAppAdTypes.ULTIMATE_FACTS_INSTAGRAM.name,
                                isInternetOn,
                                null
                            )
                            if (isInternetOn) viyatekInAppPrefHandlers.setUfInstagramClicked(true)
                            openInstagram.OpenMyInstagramAccount(context.getString(R.string.uf_insta_id))
                        }
                    }


                    socialMediaView.socialMediaAccountName.text =
                        activity.getString(R.string.uf_insta_id_with_at)
                    socialMediaView.socialMediaTitle.text =
                        activity.getString(R.string.ultimateFactsInsta)

                    iInAppListener?.inAppAdImpression(
                        ViyatekInAppAdTypes.ULTIMATE_FACTS_INSTAGRAM.name,
                        isInternetOn,
                        null
                    )
                }
                ViyatekInAppAdTypes.ULTIMATE_FACTS_TWITTER.name -> {
                    container.apply {
                        addView(socialMediaView.root)
                        setOnClickListener {
                            iInAppListener?.inAppAdClicked(
                                ViyatekInAppAdTypes.ULTIMATE_FACTS_TWITTER.name,
                                isInternetOn,
                                null
                            )
                            if (isInternetOn) viyatekInAppPrefHandlers.setUfTwitterClicked(true)
                            openTwitter.OpenMyTwitterAccount(context.getString(R.string.uf_twitter_id))
                        }
                    }

                    socialMediaView.apply {
                        socialMediaIcon.setImageResource(R.drawable.twitter)
                        socialMediaAccountName.text =
                            activity.getString(R.string.uf_twitter_with_at)
                        socialMediaTitle.text = activity.getString(R.string.ultimateFactsTwitter)
                    }


                    iInAppListener?.inAppAdImpression(
                        ViyatekInAppAdTypes.ULTIMATE_FACTS_TWITTER.name,
                        isInternetOn,
                        null
                    )
                }
                ViyatekInAppAdTypes.ULTIMATE_QUOTES_INSTAGRAM.name -> {
                    container.apply {
                        addView(socialMediaView.root)
                        setOnClickListener {
                            iInAppListener?.inAppAdClicked(
                                ViyatekInAppAdTypes.ULTIMATE_QUOTES_INSTAGRAM.name,
                                isInternetOn,
                                null
                            )
                            if (isInternetOn) viyatekInAppPrefHandlers.setUqInstagramClicked(true)
                            openInstagram.OpenMyInstagramAccount(context.getString(R.string.uq_insta_id))
                        }
                    }

                    socialMediaView.apply {
                        socialMediaAccountName.text =
                            activity.getString(R.string.uq_insta_id_with_at)
                        socialMediaTitle.text = activity.getString(R.string.ultimateQuotesInstaText)
                    }


                    iInAppListener?.inAppAdImpression(
                        ViyatekInAppAdTypes.ULTIMATE_QUOTES_INSTAGRAM.name,
                        isInternetOn,
                        null
                    )
                }
                ViyatekInAppAdTypes.FACTS.name -> {
                    container.apply {
                        addView(installAdView.root)
                        setOnClickListener {
                            iInAppListener?.inAppAdClicked(
                                ViyatekInAppAdTypes.FACTS.name,
                                isInternetOn,
                                null
                            )
                            if (isInternetOn) viyatekInAppPrefHandlers.setFactClicked(true)
                            if (dynamicLinkPrefix != null && isInternetOn) {
                                createDynamicLink(
                                    "com.viyatek.ultimatefacts",
                                    activity.getString(R.string.uf_ad),
                                    activity.getString(R.string.ultimate_facts_motto),
                                    "https://apps-images.s3-us-west-2.amazonaws.com/fact-topic-images/green_icon.jpg",
                                    "com.viyatek.ultimatefacts",
                                    "1477824910"
                                )
                            } else {
                                playStore.Open("com.viyatek.ultimatefacts")
                            }
                        }
                    }

                    installAdView.apply {
                        viyatekInstallAppImg.setImageResource(R.drawable.uf)
                        twLockScreenAdHeadline.text = activity.getString(R.string.uf_ad)
                        twLockScreenAdText.text = activity.getString(R.string.ultimate_facts_motto)
                        twLockScreenAdButton.setTextColor(Color.WHITE)
                    }
                }
                ViyatekInAppAdTypes.QUOTES.name -> {
                    container.apply {
                        addView(installAdView.root)
                        setOnClickListener {
                            iInAppListener?.inAppAdClicked(
                                ViyatekInAppAdTypes.QUOTES.name,
                                isInternetOn,
                                null
                            )
                            if (isInternetOn) viyatekInAppPrefHandlers.setUqClicked(true)

                            if (dynamicLinkPrefix != null && isInternetOn) {

                                Log.d("Dynamic", "Creating the link")

                                createDynamicLink(
                                    "com.viyatek.ultimatequotes",
                                    activity.getString(R.string.quotes_ad),
                                    activity.getString(R.string.quotes_ad_text),
                                    "https://d2e1xfn23zsdmn.cloudfront.net/others/qi_icon_512.jpg",
                                    "com.viyatek.motilife",
                                    "1531721462"
                                )
                            } else {
                                Log.d("Dynamic", "It is null")
                                playStore.Open("com.viyatek.ultimatequotes")
                            }

                        }
                    }

                    installAdView.apply {
                        viyatekInstallAppImg.setImageResource(R.drawable.iq)
                        twLockScreenAdHeadline.text = activity.getString(R.string.quotes_ad)
                        twLockScreenAdText.text = activity.getString(R.string.quotes_ad_text)
                        twLockScreenAdButton.setTextColor(Color.WHITE)
                    }
                }
                ViyatekInAppAdTypes.BASKET_BUSTERS.name -> {

                    container.apply {
                        addView(installAdView.root)
                        setOnClickListener {
                            iInAppListener?.inAppAdClicked(
                                ViyatekInAppAdTypes.BASKET_BUSTERS.name,
                                isInternetOn,
                                null
                            )
                            if (isInternetOn) viyatekInAppPrefHandlers.setBasketBustersAdClicked(
                                true
                            )

                        }
                    }

                    installAdView.apply {
                        viyatekInstallAppImg.setImageResource(R.drawable.basket_buster)
                        twLockScreenAdHeadline.text = activity.getString(R.string.basket_busters_ad)
                        twLockScreenAdText.text =
                            activity.getString(R.string.basket_busters_ad_text)
                        twLockScreenAdButton.setTextColor(Color.WHITE)
                    }

                }
                ViyatekInAppAdTypes.COLOR_UP.name -> {
                    container.apply {
                        addView(installAdView.root)
                        setOnClickListener {
                            iInAppListener?.inAppAdClicked(
                                ViyatekInAppAdTypes.COLOR_UP.name,
                                isInternetOn,
                                null
                            )
                            if (isInternetOn) viyatekInAppPrefHandlers.setColorUpAdClicked(true)
                            playStore.Open("com.viyatek.colorupgame")
                        }
                    }

                    installAdView.apply {
                        viyatekInstallAppImg.setImageResource(R.drawable.color_up_ad)
                        twLockScreenAdHeadline.text = activity.getString(R.string.color_up_ad)
                        twLockScreenAdText.text = activity.getString(R.string.color_up_ad_text)
                        twLockScreenAdButton.setTextColor(Color.WHITE)
                    }
                }
                ViyatekInAppAdTypes.BOTTLE_CAP_CHALLANGE.name -> {
                    container.apply {
                        addView(installAdView.root)
                        setOnClickListener {
                            iInAppListener?.inAppAdClicked(
                                ViyatekInAppAdTypes.BOTTLE_CAP_CHALLANGE.name,
                                isInternetOn,
                                null
                            )
                            if (isInternetOn) viyatekInAppPrefHandlers.setBottleCapChallangeAdClicked(
                                true
                            )
                            playStore.Open("com.viyatek.bottlecapchallenge")
                        }
                    }

                    installAdView.apply {
                        viyatekInstallAppImg.setImageResource(R.drawable.bottle_cap)
                        twLockScreenAdHeadline.text = activity.getString(R.string.bottle_cap_ad)
                        twLockScreenAdText.text = activity.getString(R.string.bottle_cap_text)
                        twLockScreenAdButton.setTextColor(Color.WHITE)
                    }
                }
                ViyatekInAppAdTypes.MOTILIFE.name -> {
                    container.apply {
                        addView(installAdView.root)
                        setOnClickListener {
                            iInAppListener?.inAppAdClicked(
                                ViyatekInAppAdTypes.MOTILIFE.name,
                                isInternetOn,
                                null
                            )

                            if (isInternetOn) viyatekInAppPrefHandlers.setMotilifeClicked(true)

                            if (dynamicLinkPrefix != null && isInternetOn) {

                                Log.d("Dynamic", "Creating the link")

                                createDynamicLink(
                                    "com.viyatek.motilife",
                                    activity.getString(R.string.motilife_ad),
                                    activity.getString(R.string.motilife_motto),
                                    "https://d2e1xfn23zsdmn.cloudfront.net/iq.png",
                                    "com.viyatek.motilife",
                                    "1531721462"
                                )
                            } else {
                                Log.d("Dynamic", "It is null")
                                playStore.Open("com.viyatek.motilife")
                            }
                        }
                    }

                    installAdView.apply {
                        viyatekInstallAppImg.setImageResource(R.drawable.qi)
                        twLockScreenAdHeadline.text = activity.getString(R.string.motilife_ad)
                        twLockScreenAdText.text = activity.getString(R.string.motilife_motto)
                        twLockScreenAdButton.setTextColor(Color.WHITE)
                    }
                }
                ViyatekInAppAdTypes.FACE_FIND.name -> {
                    container.apply {
                        addView(installAdView.root)

                        setOnClickListener {
                            iInAppListener?.inAppAdClicked(
                                ViyatekInAppAdTypes.FACE_FIND.name,
                                isInternetOn,
                                null
                            )

                            if (isInternetOn) viyatekInAppPrefHandlers.setFaceFindClicked(true)

                            if (dynamicLinkPrefix != null && isInternetOn) {

                                Log.d("Dynamic", "Creating the link")

                                createDynamicLink(
                                    "com.viyatek.facefind",
                                    activity.getString(R.string.face_find_ad),
                                    activity.getString(R.string.face_find_motto),
                                    "https://d2e1xfn23zsdmn.cloudfront.net/ff.png",
                                    "com.viyatek.facefind",
                                    "1531721462"
                                )
                            } else {
                                Log.d("Dynamic", "It is null")
                                playStore.Open("com.viyatek.facefind")
                            }


                        }

                        installAdView.apply {
                            viyatekInstallAppImg.setImageResource(R.drawable.ff)
                            twLockScreenAdHeadline.text = activity.getString(R.string.face_find_ad)
                            twLockScreenAdText.text = activity.getString(R.string.face_find_motto)
                            twLockScreenAdButton.setTextColor(Color.WHITE)
                        }
                    }
                }
            }
        }
    }

    private fun findAdReference(allAds:  ArrayList<String>): String? {

        val referenceValue = Random().nextFloat()

        val eachChoiceProbability = 1f / allAds.size

        for(i in 0 until allAds.size)
        {
            if(referenceValue <((i+1) * eachChoiceProbability))
            { return allAds[i]; }
        }

        return null;
    }

    private fun checkThisApp(theUri: String): Boolean {
        val myPackageManager: PackageManager = activity.packageManager
        return try {
            myPackageManager.getPackageInfo(theUri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun createDynamicLink(packageNameAndroid : String,
                                  adTitle:String,
                                  adDescription:String,
                                  adImageUrl:String,
                                  packageNameIos : String? = null,
                                  appStoreId:String? = null)
    {
        //Deeplink hepsinin Android Play Store Linki olacak

        dynamicLinkPrefix?.let {

            val deepLink = activity.getString(R.string.store_base_link, packageNameAndroid).toUri()

            Firebase.dynamicLinks.dynamicLink { // or Firebase.dynamicLinks.shortLinkAsync
                link = deepLink
                domainUriPrefix = it
                androidParameters(packageNameAndroid) {
                    fallbackUrl = deepLink
                    build()
                   }
                packageNameIos?.let {
                    iosParameters(it) {
                        appStoreId?.let { this.appStoreId = it }
                        build()
                    }
                }
                googleAnalyticsParameters {
                    source = activity.packageName
                    medium = "In-App-Ads"
                    campaign = "${activity.packageName} In-App Campaign"
                }
                socialMetaTagParameters {
                    title = adTitle
                    description = adDescription
                    imageUrl = adImageUrl.toUri()
                }
                buildShortDynamicLink()
                    .addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            // Short link created

                            // Short link created
                            val shortLink: Uri? = it.result?.shortLink
                            val flowchartLink: Uri? = it.result?.previewLink


                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = shortLink
                            activity.startActivity(intent)

                            Log.d("Dynamic Link", "Created Short Link : $shortLink")
                            Log.d("Dynamic Link", "Flow Chart Link : $flowchartLink")


                        }
                        else
                        {
                            Log.d("Dynamic Link", "Dynamic Link Creation is failed")
                            playStore.Open(packageNameAndroid)
                        }
                    }
            }
        }

    }
}