package com.example.telehealth

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import io.agora.agorauikit_android.AgoraButton
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraSettings
import io.agora.agorauikit_android.AgoraVideoViewer
import io.agora.rtc.Constants


class VideoActivity : AppCompatActivity() {
    private lateinit var videoContainer: FrameLayout
    private var agView: AgoraVideoViewer? = null
    private val appId="0ce32711a8814c9fa0bb74905dcd2b9d"
    private var channel="1"
    private val token="5736233b804c41de93ac35ecf393cc9f"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val doctorId=intent.getStringExtra("DOCTOR_ID")
        if(doctorId != null) {
            channel=doctorId.toString()
        }

        val exitButton = AgoraButton(this)
        exitButton.setImageResource(R.drawable.exit_icon)
        exitButton.setOnClickListener{
            finish()
        }

        val agoraSettings = AgoraSettings()
        agoraSettings.enabledButtons = mutableSetOf(AgoraSettings.BuiltinButton.CAMERA, AgoraSettings.BuiltinButton.MIC,AgoraSettings.BuiltinButton.FLIP)
        agoraSettings.extraButtons= mutableListOf(exitButton)

        agView = AgoraVideoViewer(
            this,
            AgoraConnectionData(appId, token),
            AgoraVideoViewer.Style.GRID,
            agoraSettings,
            null
        )
        this.addContentView(agView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        agView!!.join(channel, role=Constants.CLIENT_ROLE_BROADCASTER)
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}