package com.lunar.autotool

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Bộ đếm thời gian & Log
 * - Ghi log ra Logcat (xem bằng: adb logcat -s AutoTool)
 * - Đo thời gian mỗi bước trong State Machine
 */
object Logger {
    private const val TAG = "AutoTool"
    private val fmt = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    private var stepStartTime: Long = 0L

    fun log(message: String) {
        val time = fmt.format(Date())
        Log.d(TAG, "[$time] $message")
    }

    fun startTimer() {
        stepStartTime = System.currentTimeMillis()
    }

    /** Trả về số ms đã trôi qua kể từ lần startTimer() gần nhất */
    fun elapsedMs(): Long = System.currentTimeMillis() - stepStartTime

    fun logStateChange(from: State, to: State) {
        log("Chuyển trạng thái: $from -> $to (mất ${elapsedMs()}ms)")
    }
}
