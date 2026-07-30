package com.lunar.autotool

import android.os.Handler
import android.os.Looper

/** 4 trạng thái theo sơ đồ */
enum class State {
    CHO,        // Chờ - đang chờ điều kiện / giao diện xuất hiện
    NHAN_NUT,   // Nhấn nút - thực hiện click vào node tìm được
    DOI,        // Đợi - chờ một khoảng thời gian sau khi nhấn
    QUAY_LAI    // Quay lại - bấm nút back / quay về màn hình trước
}

/**
 * State Machine điều khiển toàn bộ luồng tự động.
 * service: tham chiếu tới AccessibilityService để thao tác (click, back)
 * analyzer: dùng để phân tích giao diện hiện tại, tìm nút cần nhấn
 */
class AutoStateMachine(
    private val service: MyAccessibilityService,
    private val analyzer: UIAnalyzer
) {
    private var state: State = State.CHO
    private val handler = Handler(Looper.getMainLooper())

    // ===== CẤU HÌNH: chỉnh theo app/nút mà bạn muốn tự động =====
    // Ví dụ: text hiển thị trên nút cần nhấn (có thể đổi thành resource-id)
    var targetButtonText: String = "Tiếp tục"
    var waitAfterPressMs: Long = 1500L   // thời gian ở trạng thái Đợi
    var pollIntervalMs: Long = 500L      // tần suất kiểm tra ở trạng thái Chờ
    var maxWaitLoops: Int = 20           // số lần Chờ tối đa trước khi Quay lại
    // ==============================================================

    private var waitLoopCount = 0

    fun start() {
        Logger.startTimer()
        transitionTo(State.CHO)
    }

    fun stop() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun transitionTo(newState: State) {
        Logger.logStateChange(state, newState)
        state = newState
        Logger.startTimer()
        runState()
    }

    private fun runState() {
        when (state) {
            State.CHO -> handleCho()
            State.NHAN_NUT -> handleNhanNut()
            State.DOI -> handleDoi()
            State.QUAY_LAI -> handleQuayLai()
        }
    }

    /** Trạng thái CHỜ: liên tục phân tích giao diện tới khi thấy nút mục tiêu */
    private fun handleCho() {
        val node = analyzer.findClickableByText(targetButtonText)
        if (node != null) {
            waitLoopCount = 0
            transitionTo(State.NHAN_NUT)
        } else {
            waitLoopCount++
            if (waitLoopCount >= maxWaitLoops) {
                Logger.log("Không tìm thấy nút '$targetButtonText' sau $maxWaitLoops lần chờ -> Quay lại")
                waitLoopCount = 0
                transitionTo(State.QUAY_LAI)
                return
            }
            handler.postDelayed({ runState() }, pollIntervalMs)
        }
    }

    /** Trạng thái NHẤN NÚT: tìm lại node và click */
    private fun handleNhanNut() {
        val node = analyzer.findClickableByText(targetButtonText)
        if (node != null) {
            val success = analyzer.clickNode(node)
            Logger.log("Nhấn nút '$targetButtonText': ${if (success) "thành công" else "thất bại"}")
        } else {
            Logger.log("Nút biến mất trước khi kịp nhấn")
        }
        transitionTo(State.DOI)
    }

    /** Trạng thái ĐỢI: chờ waitAfterPressMs rồi quay lại CHỜ để lặp chu trình */
    private fun handleDoi() {
        handler.postDelayed({
            transitionTo(State.CHO)
        }, waitAfterPressMs)
    }

    /** Trạng thái QUAY LẠI: bấm nút back của hệ thống */
    private fun handleQuayLai() {
        service.performGlobalBack()
        handler.postDelayed({
            transitionTo(State.CHO)
        }, waitAfterPressMs)
    }
}
