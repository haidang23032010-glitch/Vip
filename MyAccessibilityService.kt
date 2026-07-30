package com.lunar.autotool

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    private lateinit var analyzer: UIAnalyzer
    private lateinit var stateMachine: AutoStateMachine

    override fun onServiceConnected() {
        super.onServiceConnected()
        Logger.log("Accessibility Service đã kết nối")
        analyzer = UIAnalyzer(this)
        stateMachine = AutoStateMachine(this, analyzer)

        // TODO: đổi tên nút / thời gian chờ cho đúng app mục tiêu của bạn
        stateMachine.targetButtonText = "Tiếp tục"
        stateMachine.waitAfterPressMs = 1500L
        stateMachine.pollIntervalMs = 500L

        stateMachine.start()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Sự kiện thay đổi giao diện có thể dùng để trigger phân tích sớm hơn
        // thay vì chỉ dựa vào polling trong StateMachine, nếu muốn tối ưu tốc độ.
    }

    override fun onInterrupt() {
        Logger.log("Service bị gián đoạn")
    }

    override fun onDestroy() {
        super.onDestroy()
        stateMachine.stop()
        Logger.log("Service đã dừng")
    }

    fun performGlobalBack() {
        performGlobalAction(GLOBAL_ACTION_BACK)
    }
}
