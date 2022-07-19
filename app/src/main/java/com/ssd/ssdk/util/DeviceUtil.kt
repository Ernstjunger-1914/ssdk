package com.ssd.ssdk.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi

@SuppressLint("HardwareIds", "MissingPermission")
open class DeviceUtil(
    private val applicationContext: Context
) {

    companion object {
        const val TAG: String = "DeviceUtil"
    }

    private val telephonyManager: TelephonyManager by lazy {
        applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    private val subscriptionManager: SubscriptionManager
        @RequiresApi(value = Build.VERSION_CODES.LOLLIPOP_MR1)
        get() = applicationContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

    /**
     * OS 버전
     */
    open val osVersion: String
        get() = Build.VERSION.RELEASE

    /**
     * 브랜드
     */
    open val deviceBrand: String
        get() = Build.BRAND

    open val deviceId: String
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> androidId
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> usimNumber
            else -> imei
        }

    /**
     * IMEI
     */
    open val imei: String
        get() = try {
            telephonyManager.deviceId ?: ""
        } catch (e: Exception) {
            ""
        }

    /**
     * USIM Serial Number
     */
    open val usimNumber: String
        get() = try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 ->
                    subscriptionManager.activeSubscriptionInfoList?.firstOrNull()?.iccId ?: ""

                else -> telephonyManager.simSerialNumber ?: ""
            }
        } catch (e: Exception) {
            ""
        }

    /**
     * 안드로이드 ID
     * 8 버전 이후에는 공장 초기화하면 변경, 7 버전까지는 영구 유지
     */
    open val androidId: String
        get() = Settings.Secure.getString(
            applicationContext.contentResolver, Settings.Secure.ANDROID_ID
        )

    /**
     * 휴대폰 전화번호
     */
    open val phoneNumber: String
        get() = try {
            telephonyManager.line1Number ?: ""
        } catch (e: Exception) {
            Logd.e(e)
            ""
        }

}