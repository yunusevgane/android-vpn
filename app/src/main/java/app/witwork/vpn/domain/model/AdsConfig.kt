package com.eskimobile.jetvpn.domain.model

import com.eskimobile.jetvpn.BuildConfig

data class AdsConfig(val appId: String?, val banner: String?, val show: String?) {
    companion object {
        fun fromFirebase(google: MutableMap<*, *>): AdsConfig {
            val appId = google["appId"] as? String
            var banner = google["banner"] as? String
            var show = google["show"] as? String

            if(BuildConfig.DEBUG){
                banner = "ca-app-pub-2299497797502657/8216856975"
                show = "ca-app-pub-2299497797502657/9388476024"
            }
            return AdsConfig(appId, banner, show)
        }
    }
}