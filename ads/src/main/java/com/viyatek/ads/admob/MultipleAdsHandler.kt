package admob

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.LogPrinter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.viyatek.ads.interfaces.AdMobAdListener
import com.viyatek.ads.interfaces.LoadMultipleAds
import kotlin.math.min

class MultipleAdsHandler (

    private val theContext: Context,
    private val nativeAdID: String,
    private val mRecyclerView: RecyclerView,
    private val mRecyclerViewList: ArrayList<Any>,
    private val adFrequency : IntArray = intArrayOf(3,5,8),
    private val adAfterFrequencyArray : Int = 5,
    private val adMobAdListener: AdMobAdListener? = null
    ) : LoadMultipleAds {

    private val mNativeAds: ArrayList<NativeAd> = ArrayList()
    private var adLoader: AdLoader? = null

    private var lastIndex = 0
    var finalindex: Int = 0
    private var adloaded = false

    override fun loadNativeAds() {

            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .setRequestMultipleImages(true)
                .build()


            val builder = AdLoader.Builder(theContext, nativeAdID)

            adLoader = builder.forNativeAd { nativeAd -> // A native ad loaded successfully, check if the ad loader has finished loading
                // and if so, insert the ads into the list.
                mNativeAds.add(nativeAd)
                if (!adLoader?.isLoading!!) {
                    adloaded = true
                    insertAds()
                }
            }.withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.

                        adMobAdListener?.adClicked()

                        if (!adLoader?.isLoading!!) {
                            adloaded = true
                            insertAds()
                        }
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()

                       adMobAdListener?.adFailedToLoad()
                    }
                })
                .withNativeAdOptions(adOptions)
                .build()

            try {
                // Load the Native Express ad.
                adLoader?.loadAd(AdRequest.Builder().build())
            } catch (e: VerifyError) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    override fun loadNewAds() {

            val index: Int = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            if (index > mNativeAds.size * 5 && adloaded) {
                adloaded = false
                loadNativeAds() }
        }

        override fun insertAdsInMenuItems() {
            if (mNativeAds.size <= 0) { return }

            var index: Int
            var indexInCellData: Int
            var finalindex: Int

            for ((i, ad) in mNativeAds.withIndex()) {

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


            if (mNativeAds.size <= adFrequency.size)
            {
                indexInCellData = adFrequency[mNativeAds.size - 1]
                finalindex += indexInCellData

            }

            else {
                finalindex += adAfterFrequencyArray
                if (finalindex < mRecyclerViewList.size) {

                    mRecyclerViewList.add(finalindex, mNativeAds.last())
                   mRecyclerView.adapter?.notifyItemInserted(finalindex)

                }
            }



        }


    }