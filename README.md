# 🏥 GAJA Clinic Management System

A comprehensive **Clinic Management System** developed using **Java Spring Boot** to simplify and digitize daily clinic operations. The system provides secure role-based access for administrators, doctors, and staff while streamlining patient registration, appointment management, prescriptions, billing, and medical records.

---

## 📌 Features

- 👨‍⚕️ Doctor Management
- 🧑 Patient Registration
- 📅 Appointment Scheduling
- 💊 Prescription Management
- 🧾 Billing & Invoice Generation
- 📄 PDF Prescription Generation
- 🔐 Role-Based Authentication & Authorization
- 📊 Dashboard for Clinic Administration
- 📂 Patient Medical History Management
- 🗄️ Secure Database Management
- 📱 Responsive User Interface

---

## 🛠️ Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Spring Security

### Frontend
- Thymeleaf
- HTML5
- CSS3
- JavaScript
- Bootstrap

### Database
- MySQL

### Build Tool
- Maven

### Tools & Technologies
- IntelliJ IDEA
- Git
- GitHub
- Postman

---

## 📂 Project Structure

```
src
├── controller
├── service
├── repository
├── entity
├── dto
├── config
├── security
├── templates
├── static
└── resources
```

---

## ✨ Modules

### Admin Module
- Dashboard
- Doctor Management
- Patient Management
- Appointment Management
- Billing Management
- User Management

### Doctor Module
- View Appointments
- Patient Consultation
- Prescription Generation
- Medical History

### Patient Module
- Registration
- Appointment Booking
- Prescription Records
- Billing Information

---

## 🔐 Security

- Spring Security Authentication
- Role-Based Authorization
- Secure Password Encryption
- Session Management

---

## 🚀 Getting Started

### Clone Repository

```bash
git clone https://github.com/your-username/gaja-clinic-management.git
```

### Navigate to Project

```bash
cd gaja-clinic-management
```

### Configure Database

Update the database configuration in:

```
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gaja_clinic
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
```

### Run the Project

```bash
mvn spring-boot:run
```

Or run the main application class directly from IntelliJ IDEA.

---

## 📷 Screenshots

You can add screenshots of:

- Login Page
- Dashboard
- Patient Registration
- Appointment Page
- Prescription Page
- Billing Page

---

## 🎯 Future Enhancements

- Email Notifications
- SMS Appointment Reminders
- Online Appointment Booking
- Payment Gateway Integration
- Inventory & Pharmacy Management
- Cloud Deployment
- Analytics Dashboard
- Multi-Clinic Support

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome.

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to your branch
5. Create a Pull Request

---

## 👨‍💻 Author

**Pragadeeshwaran K**

- LinkedIn: https://linkedin.com/in/your-profile
- GitHub: https://github.com/your-username
- Email: vetriop03@gmail.com

---

## 📄 License

This project is developed for educational and portfolio purposes.

---

⭐ If you found this project useful, please consider giving it a **Star** on GitHub!
