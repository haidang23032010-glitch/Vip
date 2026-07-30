package com.lunar.autotool

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
        }

        val info = TextView(this).apply {
            text = "Bước 1: Bấm nút bên dưới để mở Cài đặt.\n" +
                    "Bước 2: Tìm mục 'Auto Tool' trong danh sách Dịch vụ trợ năng (Accessibility) và bật lên.\n" +
                    "Sau khi bật, tool sẽ tự chạy theo State Machine đã cấu hình."
            textSize = 16f
        }

        val button = Button(this).apply {
            text = "Mở Cài đặt Accessibility"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        layout.addView(info)
        layout.addView(button)
        setContentView(layout)
    }
}
