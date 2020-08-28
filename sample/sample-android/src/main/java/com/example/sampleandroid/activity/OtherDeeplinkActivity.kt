package com.example.sampleandroid.activity

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.util.log


class OtherDeeplinkActivity : AppCompatActivity() {

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        textView = TextView(this).apply {
            text = "other Depplink : ${getDeeplinkData()}"
        }
        linearLayout.addView(textView)
        linearLayout.addView(Button(this).apply {
            setOnClickListener {
                aa()
            }
        })
        setContentView(linearLayout)
//        aa()
    }

    fun getDeeplinkData() = intent.data?.getQueryParameter("param")

    fun aa() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                log.i(pendingDynamicLinkData?.link?.toString())
                textView.text = textView.text.toString() + pendingDynamicLinkData?.link?.toString()
            }
            .addOnFailureListener(this) { e ->
                toast(e.message)
                log.i(e)
            }

    }
}