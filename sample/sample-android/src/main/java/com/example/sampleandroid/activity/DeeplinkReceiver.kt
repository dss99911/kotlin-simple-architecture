package com.example.sampleandroid.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.util.log

class DeeplinkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        toast("referrer start")
        log.i("referrer start")
        if (intent?.action == "com.android.vending.INSTALL_REFERRER") {

            toast(intent.getStringExtra("referrer"))
            log.i(intent.getStringExtra("referrer") ?: "")
        }
    }
}