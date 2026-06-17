package com.gugze.magic.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class WearableControlService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val packageName = event.packageName?.toString() ?: return
                if (packageName.contains("watch") || packageName.contains("wearable")) {
                    handleWearableApp(packageName)
                }
            }
        }
    }

    private fun handleWearableApp(packageName: String) {
        val rootNode = rootInActiveWindow ?: return
        findAndClickButton(rootNode, "시작")
    }

    private fun findAndClickButton(node: AccessibilityNodeInfo?, buttonText: String) {
        if (node == null) return

        if (node.text != null && node.text.toString().contains(buttonText)) {
            if (node.isClickable) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return
            }
        }

        for (i in 0 until (node.childCount ?: 0)) {
            findAndClickButton(node.getChild(i), buttonText)
        }
    }

    override fun onInterrupt() {
        // Handle interruption
    }
}
