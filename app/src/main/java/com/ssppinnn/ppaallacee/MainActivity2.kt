package com.ssppinnn.ppaallacee

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.skline.sdksupport.MainClass


class MainActivity2 : AppCompatActivity() {

    lateinit var remoteConfig : FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val mainClass = MainClass()

        mainClass.initialize(
            applicationContext,
            this,
            "i7bGr85W5QTZLgT45vFfEn",
            "89eac504-d48d-4d74-b4f4-378a2cc601ee",
            "22f1f0f9f23393aff25053554b21ce0e",
            "verflight",
            "vf_media_source",
            "vf_af_stat",
            "vf_camp",
            "vfidfa",
            "vftime",
            "vf_af_id",
            "vf_deep"
        ){ str ->
            if (str == "false") {
                start_application()
            } else {
                startWeb(str)
            }
        }


        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                } else {
                    start_application()
                }
//                if(remoteConfig.getString("domen") == "" || remoteConfig.getString("domen") == null){
//                    start_application()
//                } else {
                   // mainClass.domen = remoteConfig.getString("domen")
              //  }
                mainClass.domen = "https://uytnj-rutyh.buzz"
            }
    }

    fun start_application(){
        println("START APP")
    }

    fun startWeb(str: String){
        println(str)
    }

}