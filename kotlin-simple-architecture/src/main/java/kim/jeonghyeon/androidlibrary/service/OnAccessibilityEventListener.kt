package kim.jeonghyeon.androidlibrary.service

import android.view.accessibility.AccessibilityEvent

interface OnAccessibilityEventListener {
    fun onAccessibilityEvent(event: AccessibilityEvent, service: BaseAccessibilityService)
}