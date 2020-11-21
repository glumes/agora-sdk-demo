package com.glumes.videochat

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration

class VideoChatActivity : AppCompatActivity() {

    private val TAG: String = VideoChatActivity::class.java.getSimpleName()

    private var mLocalContainer: FrameLayout? = null
    private var mRemoteContainer: RelativeLayout? = null

    private var mLocalView: SurfaceView? = null
    private var mRemoteView: SurfaceView? = null

    private var mCallBtn: ImageView? = null
    private var mMuteBtn: ImageView? = null
    private var mSwitchCameraBtn: ImageView? = null

    private var mRtcEngine: RtcEngine? = null
    private var mCallEnd = false

    private var mMuted = false

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            Log.d(TAG,"Join channel success") }


        override fun onRemoteVideoStateChanged(
            uid: Int,
            state: Int,
            reason: Int,
            elapsed: Int
        ) {
            if (state == Constants.REMOTE_VIDEO_STATE_STARTING) {
                runOnUiThread { setupRemoteVideo(uid) }
            }
        }


        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                onRemoteUserLeft()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_chat)
        initUI()
        initEngine()
        setupLocalVideo()
        joinChannel()
    }

    private fun initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container)
        mRemoteContainer = findViewById(R.id.remote_video_view_container)

        mCallBtn = findViewById(R.id.btn_call)
        mMuteBtn = findViewById(R.id.btn_mute)
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera)


    }


    private fun initEngine() {
        mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), mRtcEventHandler)
    }

    private fun setupLocalVideo(){
        mLocalView = RtcEngine.CreateRendererView(baseContext)
        mLocalContainer!!.addView(mLocalView)
        mRtcEngine!!.enableVideo()
        setupVideoConfig()
        mRtcEngine!!.setupLocalVideo(VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN,0))
        mRtcEngine!!.startPreview()
    }

    private fun setupVideoConfig(){
        mRtcEngine!!.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
        mRtcEngine!!.setAudioProfile(
            Constants.AudioProfile.DEFAULT.ordinal,
            Constants.AudioScenario.DEFAULT.ordinal
        )
    }

    private fun joinChannel(){
        val token = getString(R.string.agora_access_token)
        mRtcEngine?.joinChannel(token,"videochat","",0)
    }


    private fun setupRemoteVideo(uid: Int) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        val count = mRemoteContainer!!.childCount
        var view: View? = null
        for (i in 0 until count) {
            val v = mRemoteContainer!!.getChildAt(i)
            if (v.tag is Int && v.tag as Int == uid) {
                view = v
            }
        }
        if (view != null) {
            return
        }
        mRemoteView = RtcEngine.CreateRendererView(baseContext)
        mRemoteContainer!!.addView(mRemoteView)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        mRemoteView?.tag = uid
    }

    private fun onRemoteUserLeft() {
        removeRemoteVideo()
    }

    private fun leaveChannel(){ mRtcEngine!!.leaveChannel() }

    private fun removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer!!.removeView(mRemoteView)
        }
        mRemoteView = null
    }


    override fun onDestroy() {
        super.onDestroy()
        if(!mCallEnd){ leaveChannel() }
        RtcEngine.destroy()
    }

    fun onLocalAudioMuteClicked(view: View?) {
        mMuted = !mMuted
        mRtcEngine!!.muteLocalAudioStream(mMuted)
        val res = if (mMuted) R.drawable.btn_mute else R.drawable.btn_unmute
        mMuteBtn!!.setImageResource(res)
    }

    fun onSwitchCameraClicked(view: View?) {
        mRtcEngine!!.switchCamera()
    }

    fun onCallClicked(view: View?) {
        if (mCallEnd) {
            startCall()
            mCallEnd = false
            mCallBtn!!.setImageResource(R.drawable.btn_endcall)
        } else {
            endCall()
            mCallEnd = true
            mCallBtn!!.setImageResource(R.drawable.btn_startcall)
        }
        showButtons(!mCallEnd)
    }


    private fun startCall() {
        setupLocalVideo()
        joinChannel();
    }

    private fun endCall() {
        removeLocalVideo()
        removeRemoteVideo()
        leaveChannel()
    }


    private fun removeLocalVideo() {
        if (mLocalView != null) {
            mLocalContainer!!.removeView(mLocalView)
        }
        mLocalView = null
    }

    private fun showButtons(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        mMuteBtn!!.visibility = visibility
        mSwitchCameraBtn!!.visibility = visibility
    }
}
