package com.skline.sdksupport

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MainClass {

    var requestPartFromApps : String = "";

    var deepLink : String = "null"

    var domen : String = ""


     fun initialize(context: Context, activity: Activity, af_key: String, osKey: String, fbClientToken: String, endpoint: String, media_source: String, af_status: String, campaign : String, idfa: String, timezone: String, af_id: String, deep: String, logicFun: (String) -> Unit, sharedPrefsName: String, prefsUrlName: String) {

         val sharedPreferences = activity.getSharedPreferences(sharedPrefsName, AppCompatActivity.MODE_PRIVATE)

         val savedLink = sharedPreferences.getString(prefsUrlName, "")

         if(savedLink == ""){
             FacebookSdk.setClientToken(fbClientToken)
             FacebookSdk.setAutoInitEnabled(true)
             FacebookSdk.fullyInitialize()
             FacebookSdk.sdkInitialize(context)

             AppLinkData.fetchDeferredAppLinkData(context) {
                 if (it != null) {
                     val delim = "://"
                     val list = it.targetUri.toString().split(delim)
                     deepLink = list[1];
                 }
             }

             OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
             OneSignal.initWithContext(context)
             OneSignal.setAppId(osKey)

             val otrewqwdasd: AppsFlyerConversionListener =
                 object : AppsFlyerConversionListener {
                     override fun onConversionDataSuccess(appsflyerConversationDatas: Map<String, Any?>) {

                         var appsflyer_user_id = AppsFlyerLib.getInstance().getAppsFlyerUID(context)
                         var google_ad_id = "";
                         val advertisingInfomation = AdvertisingIdClient.getAdvertisingIdInfo(
                             context
                         )
                         val googleAdId = advertisingInfomation?.id
                         if (googleAdId != null) {
                             google_ad_id = googleAdId
                         };

                         var request = "/${endpoint}?${media_source}=${appsflyerConversationDatas["media_source"]}&${af_status}=${appsflyerConversationDatas["af_status"]}&${campaign}=${appsflyerConversationDatas["campaign"]}&${idfa}=${google_ad_id}&${timezone}=${TimeZone.getDefault().id}&${af_id}=${appsflyer_user_id}&${deep}=${deepLink}"
                         requestPartFromApps = request;

                         if(domen != ""){
                             makeLogicChanges(context, domen, logicFun, activity, sharedPrefsName, prefsUrlName)
                         }

                     }

                     override fun onConversionDataFail(errorMessage: String) {

                     }

                     override fun onAppOpenAttribution(attributionData: Map<String, String?>) {

                     }

                     override fun onAttributionFailure(errorMessage: String) {

                     }
                 }
//
             AppsFlyerLib.getInstance().init(af_key, otrewqwdasd, context)
             AppsFlyerLib.getInstance().start(context)
         } else {
             if (savedLink != null) {
                 logicFun(savedLink)
             } else {
                 logicFun("false")
             }
         }

    //    if(isDeveloperModeEnabled(context)){

//        } else {
//            logicFun("false")
//        }
    }

    private fun makeLogicChanges(context: Context, domen: String, logicFun: (String) -> Unit, activity: Activity, sharedPrefsName: String, prefsUrlName: String){

        val sharedPreferences = activity.getSharedPreferences(sharedPrefsName, AppCompatActivity.MODE_PRIVATE)

            var fullLink = domen + requestPartFromApps;
            val mURL = URL(fullLink)
            Log.d("SDK", fullLink)
            val thread = Thread {
                try {
                    with(mURL.openConnection() as HttpURLConnection) {
                        // optional default is GET
                        requestMethod = "GET"

                        var responseJSON = "";
                        val responseStatusCode: Int = this.responseCode

                        if (responseStatusCode == 200) {
                            BufferedReader(InputStreamReader(inputStream)).use {
                                val response = StringBuffer()
                                var inputLine = it.readLine()
                                while (inputLine != null) {
                                    response.append(inputLine)
                                    inputLine = it.readLine()
                                }
                                it.close()

                                responseJSON = response.toString();

                                if (responseJSON != "false") {
                                    sharedPreferences.edit().putString(prefsUrlName, responseJSON).apply()
                                    logicFun(responseJSON)
                                } else {
                                    logicFun("false")
                                }
                            }
                        } else {
                            logicFun("false")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
    }

//    private fun isDeveloperModeEnabled(context: Context): Boolean {
//        return Settings.Secure.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0
//    }

}