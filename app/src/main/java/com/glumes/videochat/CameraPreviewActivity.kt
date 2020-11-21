package com.glumes.videochat

import android.os.Bundle
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas

class CameraPreviewActivity : AppCompatActivity() {

    private var mLocalContainer: FrameLayout? = null
    private var mLocalView: SurfaceView? = null
    private var mRtcEngine: RtcEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_preview)
        initUI()
        initEngine()
        setupLocalVideo()
    }

    private fun initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container)
    }

    private fun initEngine() {
        mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), object :IRtcEngineEventHandler(){})
    }

    private fun setupLocalVideo(){
        mLocalView = RtcEngine.CreateRendererView(baseContext)
        mLocalContainer!!.addView(mLocalView)
        mRtcEngine!!.enableVideo()
        mRtcEngine!!.setupLocalVideo(VideoCanvas(mLocalView,VideoCanvas.RENDER_MODE_HIDDEN,0))
        mRtcEngine!!.startPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        RtcEngine.destroy()
    }
}
