package com.gugze.magic.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class WearableControlService : AccessibilityService() {

    companion object {
        const val TAG = "WearableControlService"
        const val WEARABLE_PACKAGE = "com.samsung.android.wearable.app"
        const val WEARABLE_MANAGER_PACKAGE = "com.samsung.android.app.watchmanager"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility Service Connected")
    }

    fun launchWearableApp() {
        Log.d(TAG, "Launching Wearable app...")

        val intent = packageManager.getLaunchIntentForPackage(WEARABLE_PACKAGE)
            ?: packageManager.getLaunchIntentForPackage(WEARABLE_MANAGER_PACKAGE)

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            Log.d(TAG, "Wearable app launched successfully")
        } else {
            Log.e(TAG, "Wearable app not found")
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used yet
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted")
    }
}
