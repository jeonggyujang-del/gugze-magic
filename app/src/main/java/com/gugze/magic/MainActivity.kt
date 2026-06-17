package com.gugze.magic

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gugze.magic.service.WearableControlService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            startAccessibilityService()
        }

        findViewById<Button>(R.id.btn_launch_wearable).setOnClickListener {
            launchWearableApp()
        }

        findViewById<Button>(R.id.btn_control).setOnClickListener {
            Toast.makeText(this, "컨트롤 시작", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startAccessibilityService() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
        Toast.makeText(this, "설정에서 규즈의 마법을 활성화하세요", Toast.LENGTH_LONG).show()
    }

    private fun launchWearableApp() {
        try {
            val intent = packageManager.getLaunchIntentForPackage("com.samsung.android.app.watchmanager")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Wearable 앱을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}
