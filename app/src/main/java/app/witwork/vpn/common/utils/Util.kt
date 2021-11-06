package com.eskimobile.jetvpn.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.MyApp
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod

@SuppressLint("HardwareIds")
object Util {
    val anonymousId: String by lazy {
        return@lazy Settings.Secure.getString(MyApp.self.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun getDeviceInfo(context: Context): Map<String, String> {
        val easyDeviceMod = EasyDeviceMod(context)
        return mutableMapOf(
            "imei" to Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID),
            "screen_display_id" to easyDeviceMod.screenDisplayID,
            "model" to easyDeviceMod.model,
            "manufacturer" to easyDeviceMod.manufacturer,
            "os_codename" to easyDeviceMod.osCodename,
            "os_version" to easyDeviceMod.osVersion,
            "product" to easyDeviceMod.product,
            "hardware" to easyDeviceMod.hardware,
            "display_version" to easyDeviceMod.displayVersion,
        )
    }

    fun getResId(input: String?): Int? {
        if (input == null) return null

        var result = input.toResId()
        if (result == -1) {
            result = "ic_$input".toResId()
        }

        if (result == -1) {
            result = "ic_${input.subSequence(0, 1)}".toResId()
        }
        return result
    }

}

fun String.toResId(): Int {
    return try {
        val clazz = R.drawable::class.java
        val field = clazz.getDeclaredField(this)
        field.getInt(field)
    } catch (exception: Exception) {
        -1
    }
}