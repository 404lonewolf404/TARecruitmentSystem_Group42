# BUPT TA Recruitment System - V4-final

A comprehensive web-based Teaching Assistant (TA) recruitment management system built with Jakarta Servlet and JSP. The system supports three user roles: Module Owner (MO), Teaching Assistant (TA), and Administrator (ADMIN), each with distinct functionalities.

---

## 📋 Team Information

### Support TA
- **kelayCHEN**: 2024010445@bupt.cn

### Group Members

| GitHub Username | Student ID | Role |
|----------------|------------|------|
| 404lonewolf404 | 231222028 | Lead |
| Qustty | 231220909 | Member |
| tyfcty | 231221833 | Member |
| lsyyy334 | 231221342 | Member |
| CptRoger | 231221803 | Member |
| yuanze1315 | 231222718 | Member |

---

## 🛠️ Technology Stack

- **Backend**: Java 21, Jakarta Servlet 5.0, JSP
- **Frontend**: HTML5, CSS3, JavaScript
- **Server**: Apache Tomcat 10.1.28
- **Data Storage**: CSV files
- **Architecture**: MVC with layered architecture (Model-DAO-Service-Servlet-View)

---

## ✨ Features

### For Teaching Assistants (TA)
- Browse and search positions
- Apply for positions with resume upload
- Track application status
- Withdraw applications
- Favorite positions
- Message communication with Module Owners
- Real-time notifications

### For Module Owners (MO)
- Create and manage positions
- Review applications
- Select or reject applicants
- Message communication with applicants
- Dashboard with statistics

### For Administrators (ADMIN)
- View system-wide statistics
- Monitor all users, positions, and applications
- Generate workload reports

---

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or higher
- Apache Tomcat 10.1.x

### Installation

1. Clone the repository
2. Copy the project to Tomcat's webapps directory
3. Compile the source code:
   ```cmd
   compile-all.bat
   ```
4. Start Tomcat
5. Access at `http://localhost:8080/TARecruitmentSystem/`

---

## � Project Structure

```
TARecruitmentSystem/
├── src/com/bupt/tarecruitment/
│   ├── model/          # Data models
│   ├── dao/            # Data access layer
│   ├── service/        # Business logic
│   ├── servlet/        # Controllers
│   ├── filter/         # Authentication & Authorization
│   └── util/           # Utilities
├── WEB-INF/
│   ├── jsp/            # View files (ta/mo/admin)
│   └── web.xml         # Configuration
├── css/                # Stylesheets
├── js/                 # JavaScript
└── data/               # CSV data files
```


**Version**: V4-final  
**Institution**: Beijing University of Posts and Telecommunications
