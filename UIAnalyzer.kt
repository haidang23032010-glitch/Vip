package com.lunar.autotool

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Phân tích giao diện: duyệt cây AccessibilityNodeInfo của màn hình hiện tại
 * để tìm node theo text hoặc resource-id.
 */
class UIAnalyzer(private val service: AccessibilityService) {

    /** Tìm node có thể click theo text hiển thị (khớp gần đúng, không phân biệt hoa/thường) */
    fun findClickableByText(text: String): AccessibilityNodeInfo? {
        val root = service.rootInActiveWindow ?: return null
        return search(root) { node ->
            val nodeText = node.text?.toString() ?: node.contentDescription?.toString()
            nodeText != null && nodeText.contains(text, ignoreCase = true) && (node.isClickable || node.isCheckable)
        }
    }

    /** Tìm node theo resource-id đầy đủ, ví dụ: "com.target.app:id/btn_next" */
    fun findClickableById(resourceId: String): AccessibilityNodeInfo? {
        val root = service.rootInActiveWindow ?: return null
        val list = root.findAccessibilityNodeInfosByViewId(resourceId)
        return list.firstOrNull()
    }

    private fun search(
        node: AccessibilityNodeInfo,
        predicate: (AccessibilityNodeInfo) -> Boolean
    ): AccessibilityNodeInfo? {
        if (predicate(node)) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = search(child, predicate)
            if (result != null) return result
        }
        return null
    }

    /** Click vào node; nếu bản thân node không clickable thì thử click node cha gần nhất có thể click */
    fun clickNode(node: AccessibilityNodeInfo): Boolean {
        var target: AccessibilityNodeInfo? = node
        while (target != null) {
            if (target.isClickable) {
                return target.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            target = target.parent
        }
        return false
    }
}
