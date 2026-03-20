# TA Recruitment System (助教招聘系统)

A web-based Teaching Assistant recruitment and management system built with Java Servlets, JSP, and CSV data storage.

## 📋 Features

### For Teaching Assistants (TA)
- Browse available positions with recruitment quota information
- Apply for positions with resume upload options
- View application status
- Withdraw applications
- Manage personal profile and upload resume

### For Module Owners (MO)
- Create and manage positions with recruitment quotas
- View applications for positions
- Select applicants
- View selected TA information on position list
- Delete positions

### For Administrators
- View workload reports
- Monitor system usage
- Manage overall system

## 🛠️ Technology Stack

- **Backend**: Java 21, Jakarta Servlet 5.0
- **Frontend**: JSP, HTML5, CSS3, JavaScript
- **Server**: Apache Tomcat 10.1.28
- **Data Storage**: CSV files
- **Architecture**: MVC pattern with layered architecture

## 📁 Project Structure

```
TARecruitmentSystem/
├── src/                          # Java source code
│   └── com/bupt/tarecruitment/
│       ├── model/                # Data models
│       ├── dao/                  # Data access layer
│       ├── service/              # Business logic layer
│       ├── filter/               # Servlet filters
│       └── servlet/              # Controllers
├── WEB-INF/
│   ├── classes/                  # Compiled .class files (not in Git)
│   ├── jsp/                      # JSP view files
│   │   ├── ta/                   # TA role pages
│   │   ├── mo/                   # MO role pages
│   │   └── admin/                # Admin role pages
│   └── web.xml                   # Web application configuration
├── css/                          # Stylesheets
├── js/                           # JavaScript files
├── data/                         # CSV data files
│   └── cv/                       # Resume files
├── uploads/                      # Application-specific resume uploads
└── README.md                     # This file
```

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or higher
- Apache Tomcat 10.1.x
- Git (optional, for version control)

### Installation

1. **Clone or download this repository**
   ```bash
   git clone https://github.com/404lonewolf404/software-engineering.git
   cd software-engineering
   ```

2. **Copy to Tomcat webapps directory**
   ```
   Copy the entire project folder to:
   [TOMCAT_HOME]/webapps/TARecruitmentSystem
   ```

3. **Compile the source code**
   ```cmd
   cd [TOMCAT_HOME]/webapps/TARecruitmentSystem
   build.bat
   ```

4. **Start Tomcat**
   ```cmd
   cd [TOMCAT_HOME]/bin
   startup.bat
   ```

5. **Access the application**
   ```
   Open browser and visit:
   http://localhost:8080/TARecruitmentSystem/
   ```

## 👥 Default Users

You can register new users or manually create users in `data/users.csv`:

### Example Admin User
- Email: admin@bupt.edu.cn
- Password: admin123
- Role: ADMIN

### Example MO User
- Email: prof.li@bupt.edu.cn
- Password: password123
- Role: MO

### Example TA User
- Email: zhangsan@bupt.edu.cn
- Password: password123
- Role: TA

## 🔧 Development

### Build the project
```cmd
build.bat
```

### Clean compiled files
```cmd
clean.bat
```

### Restart Tomcat
You need to manually restart Tomcat after code changes:
```cmd
cd [TOMCAT_HOME]/bin
shutdown.bat
startup.bat
```

## 📝 Key Features Implementation

### Authentication & Authorization
- Session-based authentication
- Role-based access control (RBAC)
- Filter-based security

### Position Management with Recruitment Quotas
- MO can specify how many TAs to recruit for each position
- System displays recruitment quota on position listings
- MO can view selected TA information directly on position list

### Resume Management
- TA can upload default resume in profile
- TA can upload new resume when applying for positions
- MO can download resumes from applications

### Data Persistence
- CSV-based storage for simplicity
- Thread-safe file operations
- Automatic data synchronization

### User Interface
- Responsive design
- Client-side form validation
- Real-time feedback
- Intuitive navigation

## 🏗️ Architecture

### Layered Architecture
```
Presentation Layer (JSP)
         ↓
Controller Layer (Servlet)
         ↓
Service Layer (Business Logic)
         ↓
DAO Layer (Data Access)
         ↓
Data Storage (CSV Files)
```

### Design Patterns
- MVC (Model-View-Controller)
- DAO (Data Access Object)
- Filter Chain
- Singleton (for data stores)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is for educational purposes.

## 👨‍💻 Author

Developed as a course project for BUPT (Beijing University of Posts and Telecommunications)

## 🙏 Acknowledgments

- Apache Tomcat team for the excellent servlet container
- Jakarta EE for the servlet specifications
- All contributors and testers

---

**Note**: This project uses CSV files for data storage, which is suitable for learning and small-scale applications. For production use, consider migrating to a proper database system.
