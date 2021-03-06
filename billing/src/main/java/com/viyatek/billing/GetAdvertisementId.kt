package com.viyatek.billing

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAdvertisementId(val context: Context) {

        private val adInfo = AdvertisingIdClient(context.applicationContext)

        suspend fun getAdvertisingId(): String =
            withContext(Dispatchers.IO) {
                //Connect with start(), disconnect with finish()
                adInfo.start()
                val adIdInfo = adInfo.info

                adInfo.zza()

                if(adIdInfo.id.isNullOrEmpty()) {
                    ""
                }
                else
                {
                    adIdInfo.id!!
                }



            }

    }
