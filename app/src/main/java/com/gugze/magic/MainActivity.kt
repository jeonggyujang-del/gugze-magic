package com.gugze.magic

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gugze.magic.service.WearableControlService

class MainActivity : AppCompatActivity() {

    private var wearableService: WearableControlService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            openAccessibilitySettings()
        }

        findViewById<Button>(R.id.btn_control).setOnClickListener {
            launchWearableApp()
        }
    }

    private fun openAccessibilitySettings() {
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
                WearableControlService.WEARABLE_PACKAGE
            ) ?: packageManager.getLaunchIntentForPackage(
                WearableControlService.WEARABLE_MANAGER_PACKAGE
            )

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                Toast.makeText(this, "Wearable 앱 실행 중...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Wearable 앱을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
