package com.eskimobile.jetvpn.common.utils

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.eskimobile.jetvpn.common.eventbus.ActionEvent
import java.util.concurrent.TimeUnit

class AdsWorker(private val context: Context, wp: WorkerParameters) : Worker(context, wp) {
    companion object {
        fun execute(context: Context) {
            val request = OneTimeWorkRequest.Builder(AdsWorker::class.java)
                .setInitialDelay(10, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }

    }

    override fun doWork(): Result {
        context.putBooleanPref(SharePrefs.KEY_SHOULD_SHOW_BANNER, true)
        postEvent(ActionEvent.shouldShowBanner)
        return Result.success()
    }
}