package com.glumes.videochat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission

class MainActivity : AppCompatActivity() {

    private val REQUESTED_PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val PERMISSION_REQ_ID = 22

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).setOnClickListener { requestPermission() }
        findViewById<Button>(R.id.button2).setOnClickListener { toCameraPreview() }
        findViewById<Button>(R.id.button3).setOnClickListener { toVideoChat() }
    }


    private fun toCameraPreview() {
        startActivity(Intent(this, CameraPreviewActivity::class.java))

    }


    private fun toVideoChat() {
        startActivity(Intent(this, VideoChatActivity::class.java))

    }

    private fun requestPermission() {
        checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)
        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)
        checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)
    }


    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode)
            return false
        }
        return true
    }

}
