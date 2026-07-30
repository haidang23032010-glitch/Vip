# Auto Tool (Accessibility Service + State Machine)

Tool tự động dựa trên sơ đồ:
Accessibility Service → Phân tích giao diện → State Machine (Chờ / Nhấn nút / Đợi / Quay lại) → Bộ đếm thời gian & Log

## Cách lấy file APK để cài lên điện thoại (không cần Android Studio)

1. Tạo 1 repo GitHub mới, đẩy toàn bộ thư mục này lên (`git init`, `git add .`, `git commit`, `git push`).
2. Vào tab **Actions** trên GitHub → workflow "Build APK" sẽ tự chạy.
3. Sau khi build xong (khoảng 2-4 phút), vào **Actions → lần chạy mới nhất → Artifacts**,
   tải file `AutoTool-debug-apk` về (là file `app-debug.apk`).
4. Chuyển file `.apk` vào điện thoại (qua Zalo, Google Drive, USB...) rồi cài đặt.
   - Nếu máy chặn "cài từ nguồn không xác định", vào Cài đặt để cho phép.

## Cách dùng sau khi cài

1. Mở app "Auto Tool" → bấm **"Mở Cài đặt Accessibility"**.
2. Trong danh sách, tìm **Auto Tool** → bật lên → xác nhận.
3. Từ lúc này service chạy nền, tự lặp chu trình:
   - **Chờ**: quét giao diện tìm nút có chữ trùng với `targetButtonText`.
   - **Nhấn nút**: click vào nút tìm thấy.
   - **Đợi**: dừng `waitAfterPressMs` mili-giây.
   - **Quay lại**: nếu chờ quá lâu không thấy nút, tự bấm Back rồi chờ tiếp.
4. Xem log qua `adb logcat -s AutoTool` (cắm điện thoại vào máy tính, bật USB debugging).

## Chỉnh cho đúng app/nút bạn muốn tự động

Sửa trong file `MyAccessibilityService.kt`, hàm `onServiceConnected()`:

```kotlin
stateMachine.targetButtonText = "Tiếp tục"   // đổi thành chữ trên nút thật
stateMachine.waitAfterPressMs = 1500L        // thời gian đợi sau khi nhấn (ms)
stateMachine.pollIntervalMs = 500L           // tần suất quét giao diện (ms)
```

Nếu nút không có chữ ổn định (icon, ảnh...), dùng `analyzer.findClickableById("com.tencongty:id/id_nut")`
trong `UIAnalyzer.kt` thay cho tìm theo text — lấy resource-id bằng Layout Inspector của Android Studio
hoặc bằng UIAutomatorViewer.

## Lưu ý quan trọng

- Dịch vụ Accessibility có quyền rất mạnh (đọc & thao tác toàn bộ màn hình). Chỉ cài trên máy của bạn,
  không phát tán tool tự động thao tác thay người dùng trên các nền tảng có điều khoản cấm việc này
  (ví dụ tương tác ảo, click ảo trên app mạng xã hội/quảng cáo) — nhiều nơi coi đây là gian lận và có thể khóa tài khoản.
- Phù hợp nhất cho: tự động hoá thao tác lặp lại trong app riêng của bạn, test app, hỗ trợ người dùng thao tác khó khăn.
