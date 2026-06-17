package com.gugze.magic

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gugze.magic.service.WearableControlService

class MainActivity : AppCompatActivity() {

    private var selectedFaceIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val faceSelector = findViewById<SeekBar>(R.id.seekbar_face_selector)
        val selectedFaceText = findViewById<TextView>(R.id.text_selected_face)

        faceSelector.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedFaceIndex = progress
                selectedFaceText.text = "선택된 Watch Face: #${progress + 1}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            startAccessibilityService()
        }

        findViewById<Button>(R.id.btn_launch_wearable).setOnClickListener {
            launchWearableApp()
        }

        findViewById<Button>(R.id.btn_control).setOnClickListener {
            startWatchFaceControl()
        }
    }

    private fun startAccessibilityService() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
        Toast.makeText(
            this,
            "설정에서 '규즈의 마법'을 활성화하세요",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun launchWearableApp() {
        try {
            val intent = packageManager.getLaunchIntentForPackage(
                "com.samsung.android.wearable.app"
            ) ?: packageManager.getLaunchIntentForPackage(
                "com.samsung.android.app.watchmanager"
            )

            if (intent != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Wearable 앱을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "앱 실행 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startWatchFaceControl() {
        val service = WearableControlService.instance
        if (service == null) {
            Toast.makeText(
                this,
                "접근성 서비스가 실행 중이 아닙니다. 먼저 활성화하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (WearableControlService.isAutomationRunning) {
            Toast.makeText(this, "이미 실행 중입니다", Toast.LENGTH_SHORT).show()
            return
        }

        service.startWatchFaceControl(selectedFaceIndex)
        Toast.makeText(
            this,
            "Watch Face #${selectedFaceIndex + 1} 적용 중...",
            Toast.LENGTH_SHORT
        ).show()
    }
}
