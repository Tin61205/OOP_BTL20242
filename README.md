# Internal Chat - Ứng dụng Chat Tích hợp AI

Internal Chat là một ứng dụng chat desktop hiện đại được xây dựng bằng JavaFX, tích hợp trí tuệ nhân tạo Gemini AI. Ứng dụng cung cấp môi trường chat nhóm real-time và chatbot cá nhân thông minh.

## 🌟 Tính năng chính

### 🔐 Xác thực người dùng
- **Đăng ký tài khoản**: Tạo tài khoản mới với username, email và password
- **Đăng nhập bảo mật**: Hệ thống xác thực an toàn
- **Quản lý phiên đăng nhập**: Tự động duy trì phiên làm việc

### 💬 Chat nhóm real-time
- **Chat đa người dùng**: Tham gia chat room với nhiều người cùng lúc
- **Gửi/nhận tin nhắn tức thì**: Hệ thống socket real-time
- **Bảng emoji phong phú**: Hơn 35 emoji để thể hiện cảm xúc
- **Lịch sử chat**: Lưu trữ và xem lại các cuộc trò chuyện
- **Giao diện trực quan**: Phân biệt rõ ràng tin nhắn của bản thân và người khác

### 🤖 Chatbot AI thông minh
- **Tích hợp Gemini AI**: Chatbot được hỗ trợ bởi Google Gemini
- **Hiển thị HTML cao cấp**: Chuyển đổi Markdown thành HTML với CSS styling đẹp mắt
- **WebView renderer**: Hiển thị nội dung phong phú với format HTML chuyên nghiệp
- **Chat cá nhân**: Không gian riêng tư để tương tác với AI
- **Phản hồi thông minh**: AI hiểu ngữ cảnh và trả lời chuyên nghiệp

### 👤 Quản lý thông tin cá nhân
- **Profile cá nhân**: Cập nhật tên, email, trạng thái
- **Ảnh đại diện**: Upload và thay đổi ảnh profile
- **Tùy chỉnh trạng thái**: Hiển thị trạng thái hoạt động của bạn

## 🛠️ Công nghệ sử dụng

- **JavaFX 23.0.1**: Framework UI chính
- **JavaFX WebView**: Engine hiển thị HTML cho chatbot
- **Java 21**: Ngôn ngữ lập trình
- **SQLite**: Cơ sở dữ liệu nhẹ
- **Socket Programming**: Giao tiếp real-time
- **Google Gemini AI**: Trí tuệ nhân tạo
- **Maven**: Quản lý dependencies
- **HTML/CSS**: Rendering và styling cho nội dung chatbot

## 🚀 Cài đặt và chạy ứng dụng

### 1. Clone repository
```bash
git clone https://github.com/your-username/OOP_BTL20242.git
cd OOP_BTL20242
```

### 2. Cấu hình Gemini AI
Chỉnh sửa file `src/main/resources/config.properties`:
```properties
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
```

### 3. Compile và chạy
```bash
# Compile dự án
mvn clean compile

# Chạy ứng dụng
mvn javafx:run
```


## 📖 Hướng dẫn sử dụng

### Đăng ký và đăng nhập
1. Khởi chạy ứng dụng
2. Chọn "Đăng ký" nếu chưa có tài khoản
3. Điền thông tin: username, email, password
4. Đăng nhập với tài khoản đã tạo

### Sử dụng chat nhóm
1. Sau khi đăng nhập, chọn tab "Chat"
2. Ứng dụng tự động kết nối đến server
3. Gõ tin nhắn và nhấn Enter hoặc nút Send
4. Sử dụng nút emoji để thêm biểu cảm

### Chat với AI Bot
1. Chọn tab "Chatbot"
2. Gõ câu hỏi hoặc tin nhắn
3. AI sẽ phản hồi và hiển thị dưới dạng HTML được format đẹp với CSS styling
4. Nội dung hỗ trợ headers, code blocks, lists, bold/italic text
5. Lịch sử chat được lưu trong phiên làm việc

### Cài đặt Profile
1. Chọn tab "Settings"
2. Cập nhật tên, trạng thái
3. Nhấn "Change Photo" để thay đổi ảnh đại diện
4. Nhấn "Save" để lưu thay đổi

## 📁 Cấu trúc dự án

```
src/
├── main/
│   ├── java/com/training/studyfx/
│   │   ├── controller/          # Controllers cho từng màn hình
│   │   │   ├── LoginController.java
│   │   │   ├── ChatViewController.java
│   │   │   ├── ChatbotViewController.java
│   │   │   └── ProfileSettingController.java
│   │   ├── model/              # Data models
│   │   │   ├── User.java
│   │   │   ├── Message.java
│   │   │   └── ChatMessage.java
│   │   ├── service/            # Business logic
│   │   │   ├── UserService.java
│   │   │   └── GeminiService.java
│   │   ├── server/             # Server components
│   │   │   ├── Server.java
│   │   │   └── ClientHandler.java
│   │   ├── util/               # Utilities
│   │   │   └── MarkdownToHtml.java # Chuyển đổi Markdown sang HTML
│   │   └── exception/          # Custom exceptions
│   └── resources/
│       ├── com/training/studyfx/   # FXML files
│       ├── images/                 # Icons và ảnh
│       ├── styles/                 # CSS files
│       └── config.properties       # Cấu hình
├── upload/                     # User uploaded files
│   ├── images/                 # Chat images
│   └── profile_images/         # Profile pictures
└── studyfx.db                  # SQLite database
```



## 📝 License

Dự án này được phát triển cho Project OOP tại HUST.

---

*Made with ❤️ by OOP BTL 2024-2 Team*