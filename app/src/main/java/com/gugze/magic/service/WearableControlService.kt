package com.gugze.magic.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class WearableControlService : AccessibilityService() {

    companion object {
        const val TAG = "WearableControlService"
        var isAutomationRunning = false
        var currentStep = Step.IDLE
        var instance: WearableControlService? = null

        const val WEARABLE_PACKAGE = "com.samsung.android.wearable.app"
        const val WEARABLE_MANAGER_PACKAGE = "com.samsung.android.app.watchmanager"
    }

    enum class Step {
        IDLE,
        LAUNCH_WEARABLE_APP,
        FIND_WATCH_FACE_MENU,
        CLICK_WATCH_FACE_SETTINGS,
        FIND_AVAILABLE_FACES,
        SELECT_WATCH_FACE,
        APPLY_WATCH_FACE,
        CONFIGURE_WATCH_FACE,
        COMPLETE
    }

    private val handler = Handler(Looper.getMainLooper())
    private var availableFaces: List<AccessibilityNodeInfo> = emptyList()
    private var selectedFaceIndex = 0

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "Accessibility Service Connected")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    fun startWatchFaceControl(faceIndex: Int = 0) {
        Log.d(TAG, "Starting Watch Face control automation...")
        isAutomationRunning = true
        currentStep = Step.LAUNCH_WEARABLE_APP
        selectedFaceIndex = faceIndex

        launchWearableApp()
    }

    private fun launchWearableApp() {
        val intent = packageManager.getLaunchIntentForPackage(WEARABLE_PACKAGE)
            ?: packageManager.getLaunchIntentForPackage(WEARABLE_MANAGER_PACKAGE)

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            Log.d(TAG, "Wearable app launched")
        } else {
            Log.e(TAG, "Wearable app not found")
            isAutomationRunning = false
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isAutomationRunning || event == null) return

        val rootNode = rootInActiveWindow ?: return

        Log.d(TAG, "Step: $currentStep, Event Type: ${event.eventType}")

        when (currentStep) {
            Step.LAUNCH_WEARABLE_APP -> {
                handler.postDelayed({
                    currentStep = Step.FIND_WATCH_FACE_MENU
                }, 1500)
            }

            Step.FIND_WATCH_FACE_MENU -> {
                val watchFaceMenus = findNodesByText(
                    rootNode,
                    "Watch Face", "시계 화면", "Watch face", "watchface"
                )
                if (watchFaceMenus.isNotEmpty()) {
                    Log.d(TAG, "Found Watch Face menu, clicking...")
                    clickNode(watchFaceMenus[0])
                    currentStep = Step.CLICK_WATCH_FACE_SETTINGS
                    handler.postDelayed({
                        // 다음 단계로 자동 전환
                    }, 1000)
                }
            }

            Step.CLICK_WATCH_FACE_SETTINGS -> {
                val settingsButtons = findNodesByText(
                    rootNode,
                    "설정", "변경", "Change", "More", "옵션", "Settings", "Edit"
                )
                if (settingsButtons.isNotEmpty()) {
                    Log.d(TAG, "Found Settings button, clicking...")
                    clickNode(settingsButtons[0])
                    currentStep = Step.FIND_AVAILABLE_FACES
                    handler.postDelayed({}, 1500)
                }
            }

            Step.FIND_AVAILABLE_FACES -> {
                handler.postDelayed({
                    val root = rootInActiveWindow ?: return@postDelayed

                    val allClickable = findAllClickableNodes(root)
                    availableFaces = allClickable.filter { node ->
                        node.className?.contains("ImageView") == true ||
                        node.className?.contains("Card") == true ||
                        (node.text != null && node.isClickable)
                    }.distinctBy { it.bounds }

                    Log.d(TAG, "Found ${availableFaces.size} available Watch Faces")

                    if (availableFaces.isNotEmpty() && selectedFaceIndex < availableFaces.size) {
                        currentStep = Step.SELECT_WATCH_FACE
                    } else {
                        Log.w(TAG, "No Watch Faces found or invalid index")
                        isAutomationRunning = false
                        currentStep = Step.IDLE
                    }
                }, 1000)
            }

            Step.SELECT_WATCH_FACE -> {
                if (selectedFaceIndex < availableFaces.size) {
                    Log.d(TAG, "Selecting Watch Face at index: $selectedFaceIndex")
                    clickNode(availableFaces[selectedFaceIndex])
                    currentStep = Step.APPLY_WATCH_FACE
                    handler.postDelayed({}, 800)
                }
            }

            Step.APPLY_WATCH_FACE -> {
                val applyButtons = findNodesByText(
                    rootNode,
                    "적용", "Apply", "확인", "OK", "설정", "Done"
                )
                if (applyButtons.isNotEmpty()) {
                    Log.d(TAG, "Found Apply button, clicking...")
                    clickNode(applyButtons[0])
                    currentStep = Step.COMPLETE

                    handler.postDelayed({
                        Log.d(TAG, "Watch Face control completed successfully")
                        isAutomationRunning = false
                        currentStep = Step.IDLE
                    }, 1000)
                }
            }

            else -> {}
        }
    }

    private fun findNodesByText(root: AccessibilityNodeInfo, vararg texts: String): List<AccessibilityNodeInfo> {
        val result = mutableListOf<AccessibilityNodeInfo>()
        for (text in texts) {
            result.addAll(root.findAccessibilityNodeInfosByText(text))
            result.addAll(findAllNodesWithContentDescription(root, text))
        }
        return result.distinctBy { it.bounds }
    }

    private fun findAllNodesWithContentDescription(
        root: AccessibilityNodeInfo,
        text: String
    ): List<AccessibilityNodeInfo> {
        val result = mutableListOf<AccessibilityNodeInfo>()
        for (i in 0 until root.childCount) {
            val child = root.getChild(i) ?: continue
            if (child.contentDescription?.toString()?.contains(text, ignoreCase = true) == true) {
                result.add(child)
            }
            result.addAll(findAllNodesWithContentDescription(child, text))
        }
        return result
    }

    private fun findAllClickableNodes(root: AccessibilityNodeInfo): List<AccessibilityNodeInfo> {
        val result = mutableListOf<AccessibilityNodeInfo>()
        if (root.isClickable) {
            result.add(root)
        }
        for (i in 0 until root.childCount) {
            val child = root.getChild(i) ?: continue
            result.addAll(findAllClickableNodes(child))
        }
        return result
    }

    private fun clickNode(node: AccessibilityNodeInfo) {
        if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d(TAG, "Clicked node: ${node.text ?: node.contentDescription}")
        } else {
            node.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d(TAG, "Clicked parent node")
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Automation Interrupted")
        isAutomationRunning = false
        currentStep = Step.IDLE
    }
}
