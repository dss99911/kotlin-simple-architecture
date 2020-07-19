package com.example.sampleandroid.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OtherDeeplinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(TextView(this).apply {
            text = "other Depplink : ${getDeeplinkData()}"
        })
    }

    fun getDeeplinkData() = intent.data?.getQueryParameter("param")
}