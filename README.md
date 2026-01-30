# PTT_Module5_Team2
Module5_Team2
 Cách chạy dự án
Yêu cầu
JDK 17+

MySQL

Gradle hoặc IntelliJ IDEA

Bước chạy
Clone dự án

bash
git clone <repository-url>
Tạo file .env (nếu dùng spring-dotenv)

env
DB_URL=jdbc:mysql://localhost:3306/ecommerce
DB_USERNAME=root
DB_PASSWORD=yourpassword
JWT_SECRET=your-secret-key
Chạy MySQL và tạo database

sql
CREATE DATABASE ecommerce;
Chạy ứng dụng

bash
./gradlew bootRun

 Kiến trúc hệ thống
Backend: REST API

Frontend: HTML/CSS/JS hoặc framework tùy chọn

JWT Authentication

Role-based Authorization


 Cấu trúc thư mục (Backend)
Code
src/
 └── main/
      ├── java/com/project/
      │     ├── controller/
      │     ├── service/
      │     ├── repository/
      │     ├── entity/
      │     └── security/
      └── resources/
            ├── application.properties
            ├── templates/
            └── static/

 Kiểm thử
Spring Boot Test

Spring Security Test

JUnit 5
