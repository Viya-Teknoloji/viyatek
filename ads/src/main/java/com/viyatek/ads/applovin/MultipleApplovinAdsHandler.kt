package com.viyatek.ads.applovin

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.viyatek.ads.interfaces.LoadMultipleAds
import kotlin.math.min


class MultipleApplovinAdsHandler (
    private val theContext: Context,
    private val nativeAdID: String,
    private val mRecyclerView: RecyclerView,
    private val mRecyclerViewList: ArrayList<Any>,
    private val adFrequency : IntArray = intArrayOf(3,5,8),
    private val adAfterFrequencyArray : Int = 5
    ) : LoadMultipleAds {

    private val mNativeAds: ArrayList<MaxAd> = ArrayList()
    private var adLoader: MaxNativeAdLoader? = null

    private var lastIndex = 0
    var finalindex: Int = 0
    private var adloaded = true


        override fun loadNewAds() {

            val index: Int = (mRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

            Log.d("MESAJLARIM", "index : $index is ad loaded : $adloaded my native ads size ${mNativeAds.size}")

            if (index > mNativeAds.size * 5 && adloaded) {
                adloaded = false
                Log.d("MESAJLARIM", "Loading new ads in mah")
                loadNativeAds()
            }

        }

        override fun insertAdsInMenuItems() {
            if (mNativeAds.size <= 0) { return }

            var index: Int
            var indexInCellData: Int
            var finalindex: Int

            for ((i, _) in mNativeAds.withIndex()) {

                index = min(i, adFrequency.size - 1)

                Log.d("Ad", "The index $index")

                indexInCellData = adFrequency[index]
                finalindex = indexInCellData + lastIndex

                if (finalindex < mRecyclerViewList.size) {
                    lastIndex += indexInCellData
                }
            }
        }

        fun insertAds()
        {
            if (mNativeAds.size <= 0) { return }

            val indexInCellData: Int

            Log.d("Ads","Ad Frequency size ${adFrequency.size}" )

            if (mNativeAds.size <= adFrequency.size)
            {
                indexInCellData = adFrequency[mNativeAds.size - 1]
                finalindex += indexInCellData

                if(mRecyclerViewList.size > finalindex && mRecyclerViewList[finalindex] !is MaxAd)
                {
                    mRecyclerViewList.add(finalindex, mNativeAds.last())
                    mRecyclerView.adapter?.notifyItemInserted(finalindex)
                }

            }

            else {
                finalindex += adAfterFrequencyArray
                if (finalindex < mRecyclerViewList.size && mRecyclerViewList[finalindex] !is MaxAd) {

                    mRecyclerViewList.add(finalindex, mNativeAds.last())
                    mRecyclerView.adapter?.notifyItemInserted(finalindex)

                }
            }



        }

    override fun loadNativeAds(multipleAdsCount : Int)
    {
        if(adLoader == null ) {adLoader = MaxNativeAdLoader(nativeAdID, theContext)}

        adLoader?.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd?) {
                adloaded = true
                ad?.let { mNativeAds.add(it) }
                insertAds()
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                adloaded = true
             }

            override fun onNativeAdClicked(ad: MaxAd) {
                // Optional click callback
            }
        })

        adloaded = false
        adLoader?.loadAd()

    }

    }
