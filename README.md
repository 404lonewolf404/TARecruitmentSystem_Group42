# BUPT TA Recruitment System - V1.0

A comprehensive web-based Teaching Assistant (TA) recruitment management system built with Jakarta Servlet and JSP. The system supports three user roles: **Module Owner (MO)**, **Teaching Assistant (TA)**, and **Administrator (ADMIN)**, each with distinct functionalities.

## 📋 Group Name-list

### Support TA
- kelayCHEN: 2024010445@bupt.cn (Support TA)

### GitHub User Names
- 404lonewolf404: 231222028 (Lead)
- Qustty: 231220909 (Member)
- tyfcty: 231221833 (Member)
- lsyyy334: 231221342 (Member)
- CptRoger: 231221803 (Member)
- yuanze1315: 231222718 (Member)

---

## 🎯 System Overview

The TA Recruitment System V1.0 is designed to streamline the process of recruiting teaching assistants at BUPT. It supports three distinct user roles with comprehensive functionality for position management, application processing, and system administration.

### Key Highlights
- **Multi-role Architecture**: TA, Module Owner (MO), and Administrator (ADMIN)
- **Complete Application Lifecycle**: From position creation to applicant selection
- **Profile Management**: User profiles with CV upload support
- **Enhanced Position Features**: Detailed position information with requirements and responsibilities
- **Dashboard Analytics**: Role-specific dashboards with statistics and quick actions
- **Secure Authentication**: SHA-256 password encryption with session management

---

## ✨ Core Features

### 1. User Management & Authentication

#### Registration System
- Support for three user roles: TA, MO, and ADMIN
- Email uniqueness validation
- Secure password storage with SHA-256 encryption
- Profile information collection (name, email, phone, major, student ID)

#### Login & Session Management
- Secure authentication with encrypted passwords
- Role-based dashboard redirection
- Session-based user state management
- Automatic logout functionality

#### Profile Management
- View and edit personal information
- Update contact details
- Change password functionality
- Profile picture support (reserved feature)

### 2. Position Management

#### For Module Owners (MO)
- **Create Positions**: 
  - Position title and description
  - Course information
  - Requirements and qualifications
  - Responsibilities and duties
  - Working hours and compensation
  - Application deadline
  - Position status (OPEN/CLOSED)

- **Manage Positions**:
  - View all created positions
  - Edit position details
  - Close/reopen positions
  - Delete positions (if no applications)
  - Track application count per position

#### For Teaching Assistants (TA)
- **Browse Positions**:
  - View all open positions
  - Filter by course or requirements
  - See detailed position information
  - Check application deadlines
  - View number of applicants

- **Position Details**:
  - Complete job description
  - Required qualifications
  - Responsibilities breakdown
  - Working hours and schedule
  - Compensation information

### 3. Application Management

#### For Teaching Assistants (TA)
- **Apply for Positions**:
  - Submit applications with cover letter
  - Upload CV (PDF format)
  - Track application status
  - View application history

- **Manage Applications**:
  - View all submitted applications
  - Check application status (PENDING/SELECTED/REJECTED/WITHDRAWN)
  - Withdraw pending applications
  - Receive status notifications

#### For Module Owners (MO)
- **Review Applications**:
  - View all applications for each position
  - Access applicant profiles
  - Download submitted CVs
  - Review cover letters

- **Process Applications**:
  - Select qualified applicants
  - Automatic rejection of other applications when one is selected
  - Track application statistics
  - Manage application workflow

#### Application Status Flow
1. **PENDING**: Initial state after submission
2. **SELECTED**: Applicant chosen by MO
3. **REJECTED**: Application declined (manual or automatic)
4. **WITHDRAWN**: Applicant withdrew application

### 4. Dashboard Features

#### TA Dashboard
- **Application Statistics**:
  - Total applications submitted
  - Pending applications count
  - Selected applications count
  - Rejected applications count

- **Quick Actions**:
  - Browse new positions
  - View my applications
  - Update profile

- **Recent Activity**:
  - Latest position postings
  - Application status updates

#### MO Dashboard
- **Position Statistics**:
  - Total positions created
  - Open positions count
  - Closed positions count
  - Total applications received

- **Pending Actions**:
  - Applications awaiting review
  - Positions nearing deadline
  - Recent applications

- **Quick Actions**:
  - Create new position
  - Review applications
  - Manage positions

#### Admin Dashboard
- **System Statistics**:
  - Total users (by role)
  - Total positions
  - Total applications
  - System activity metrics

- **System Monitoring**:
  - User registration trends
  - Application activity
  - Position creation rate

- **Administrative Actions**:
  - View workload reports
  - System configuration
  - Data management

### 5. CV Management

- **Upload Functionality**:
  - PDF file upload support
  - File size validation
  - Automatic file naming with UUID
  - Secure file storage in data/cv/ directory

- **Download & View**:
  - MO can download applicant CVs
  - Secure file access control
  - File type validation

### 6. Administrative Features

#### Workload Reporting
- Track TA working hours
- Generate workload statistics
- Monitor TA performance
- Export reports (reserved feature)

#### System Management
- User account management
- Data integrity monitoring
- System health checks
- Activity logging (reserved feature)

---

## 🏗️ Technical Architecture

### Backend Architecture (Java)

#### Model Layer (Entity Classes)
- **User.java**: User entity with profile information
  - Fields: id, name, email, password, phone, major, studentId, role, createdAt
  - Role types: TA, MO, ADMIN

- **Position.java**: Position entity with detailed information
  - Fields: id, title, description, course, requirements, responsibilities, hours, compensation, deadline, status, moId, createdAt
  - Status types: OPEN, CLOSED

- **Application.java**: Application entity
  - Fields: id, positionId, taId, coverLetter, cvPath, status, appliedAt, updatedAt
  - Status types: PENDING, SELECTED, REJECTED, WITHDRAWN

- **Enums**: UserRole, PositionStatus, ApplicationStatus

#### DAO Layer (Data Access)
- **UserDAO.java**: User data operations
  - CRUD operations for users
  - Email uniqueness validation
  - Password authentication
  - CSV file persistence

- **PositionDAO.java**: Position data operations
  - CRUD operations for positions
  - Filter by status and MO
  - Position search functionality
  - CSV file persistence

- **ApplicationDAO.java**: Application data operations
  - CRUD operations for applications
  - Filter by TA, position, and status
  - Application statistics
  - CSV file persistence

#### Service Layer (Business Logic)
- **AuthService.java**: Authentication and authorization
  - User registration with validation
  - Login authentication
  - Password encryption (SHA-256)
  - Session management

- **PositionService.java**: Position business logic
  - Position creation and validation
  - Position lifecycle management
  - Search and filter operations
  - Application count tracking

- **ApplicationService.java**: Application business logic
  - Application submission
  - Status management
  - Applicant selection logic
  - Withdrawal processing

- **WorkloadService.java**: Workload tracking
  - TA hours calculation
  - Workload statistics
  - Report generation

#### Servlet Layer (Controllers)
- **AuthServlet.java** (/auth/*): Authentication endpoints
  - /auth/login - User login
  - /auth/register - User registration
  - /auth/logout - User logout

- **DashboardServlet.java** (/dashboard): Dashboard rendering
  - Role-based dashboard display
  - Statistics calculation
  - Quick action links

- **PositionServlet.java** (/ta/*, /mo/*): Position management
  - /ta/positions - Browse positions (TA)
  - /mo/positions - Manage positions (MO)
  - /mo/positions/create - Create position (MO)
  - /mo/positions/edit - Edit position (MO)
  - /mo/positions/delete - Delete position (MO)

- **ApplicationServlet.java** (/ta/applications/*, /mo/applications/*): Application management
  - /ta/applications - View my applications (TA)
  - /ta/applications/apply - Submit application (TA)
  - /ta/applications/withdraw - Withdraw application (TA)
  - /mo/applications - Review applications (MO)
  - /mo/applications/select - Select applicant (MO)

- **ProfileServlet.java** (/profile/*): Profile management
  - /profile/view - View profile
  - /profile/edit - Edit profile
  - /profile/password - Change password

- **CVServlet.java** (/cv/*): CV file operations
  - /cv/upload - Upload CV
  - /cv/download - Download CV

- **AdminServlet.java** (/admin/*): Admin operations
  - /admin/workload - Workload reports
  - /admin/users - User management
  - /admin/statistics - System statistics

- **IndexServlet.java** (/): Home page routing

#### Filter Layer (Security)
- **AuthFilter.java**: Authentication filter
  - Intercepts all protected routes
  - Validates user session
  - Redirects to login if not authenticated
  - Excludes public paths (login, register, static resources)

- **RoleFilter.java**: Authorization filter
  - Role-based access control
  - Validates user permissions for routes
  - Prevents unauthorized access
  - Strict role separation (TA/MO/ADMIN)

### Frontend Architecture (JSP)

#### Common Pages
- **login.jsp**: User login interface
- **register.jsp**: User registration form
- **error.jsp**: Error message display

#### TA Pages (/WEB-INF/jsp/ta/)
- **dashboard.jsp**: TA dashboard with statistics
- **positions.jsp**: Browse available positions
- **applications.jsp**: View my applications
- **profile.jsp**: View/edit profile

#### MO Pages (/WEB-INF/jsp/mo/)
- **dashboard.jsp**: MO dashboard with statistics
- **positions.jsp**: Manage my positions
- **create-position.jsp**: Create new position form
- **applications.jsp**: Review applications
- **profile.jsp**: View/edit profile

#### Admin Pages (/WEB-INF/jsp/admin/)
- **dashboard.jsp**: Admin dashboard with system stats
- **workload.jsp**: TA workload reports
- **profile.jsp**: View/edit profile

### Data Storage

#### CSV File Structure
- **data/users.csv**: User records
  - Format: id,name,email,password,phone,major,studentId,role,createdAt

- **data/positions.csv**: Position records
  - Format: id,title,description,course,requirements,responsibilities,hours,compensation,deadline,status,moId,createdAt

- **data/applications.csv**: Application records
  - Format: id,positionId,taId,coverLetter,cvPath,status,appliedAt,updatedAt

#### File Storage
- **data/cv/**: CV file storage directory
  - Naming convention: {uuid}_{timestamp}.pdf
  - Secure access through CVServlet
  - Automatic directory creation

---

## 🚀 Deployment Guide

### Prerequisites
- **JDK 11 or higher**
- **Apache Tomcat 10.x**
- **Jakarta Servlet API 5.0.0**

### Installation Steps

1. **Prepare Dependencies**
\\\ash
# Create lib directory
mkdir lib

# Download Jakarta Servlet API
# Place jakarta.servlet-api-5.0.0.jar in lib/
\\\

2. **Compile Source Code**
\\\ash
# Compile all Java files
javac -encoding UTF-8 -d WEB-INF/classes -cp "lib/*;WEB-INF/classes" ^
  src/com/bupt/tarecruitment/model/*.java ^
  src/com/bupt/tarecruitment/dao/*.java ^
  src/com/bupt/tarecruitment/service/*.java ^
  src/com/bupt/tarecruitment/filter/*.java ^
  src/com/bupt/tarecruitment/servlet/*.java
\\\

3. **Deploy to Tomcat**
\\\ash
# Copy entire project to Tomcat webapps
xcopy /E /I TARecruitmentSystem %CATALINA_HOME%\webapps\TARecruitmentSystem

# Or use the provided build script
build.bat
\\\

4. **Start Tomcat**
\\\ash
# Start Tomcat server
%CATALINA_HOME%\bin\startup.bat

# Access the application
# http://localhost:8080/TARecruitmentSystem/
\\\

### Build Script (build.bat)
The project includes a build script for automated compilation and deployment:
\\\atch
@echo off
echo Building TA Recruitment System...
javac -encoding UTF-8 -d WEB-INF/classes -cp "lib/*;WEB-INF/classes" src/com/bupt/tarecruitment/*/*.java
echo Build complete!
\\\

---

## 🔑 Default Test Accounts

| Role | Email | Password | Description |
|------|-------|----------|-------------|
| TA | ta@test.com | 123456 | Teaching Assistant account |
| MO | mo@test.com | 123456 | Module Owner account |
| ADMIN | admin@test.com | 123456 | Administrator account |

---

## 🌐 URL Routing

### Public Routes
- \GET /\ - Home page (redirects based on auth status)
- \GET /auth/login\ - Login page
- \POST /auth/login\ - Login submission
- \GET /auth/register\ - Registration page
- \POST /auth/register\ - Registration submission
- \GET /auth/logout\ - Logout

### TA Routes
- \GET /dashboard\ - TA dashboard
- \GET /ta/positions\ - Browse positions
- \GET /ta/applications\ - My applications
- \POST /ta/applications/apply\ - Submit application
- \POST /ta/applications/withdraw\ - Withdraw application
- \GET /profile/view\ - View profile
- \POST /profile/edit\ - Edit profile
- \POST /cv/upload\ - Upload CV

### MO Routes
- \GET /dashboard\ - MO dashboard
- \GET /mo/positions\ - My positions
- \GET /mo/positions/create\ - Create position page
- \POST /mo/positions/create\ - Create position submission
- \POST /mo/positions/delete\ - Delete position
- \GET /mo/applications\ - Review applications
- \POST /mo/applications/select\ - Select applicant
- \GET /cv/download\ - Download applicant CV

### Admin Routes
- \GET /dashboard\ - Admin dashboard
- \GET /admin/workload\ - Workload reports
- \GET /admin/statistics\ - System statistics

---

## 📁 Project Structure

\\\
TARecruitmentSystem/
├── src/
│   └── com/bupt/tarecruitment/
│       ├── model/                  # Entity classes
│       │   ├── User.java
│       │   ├── Position.java
│       │   ├── Application.java
│       │   ├── UserRole.java
│       │   ├── PositionStatus.java
│       │   └── ApplicationStatus.java
│       ├── dao/                    # Data access layer
│       │   ├── UserDAO.java
│       │   ├── PositionDAO.java
│       │   └── ApplicationDAO.java
│       ├── service/                # Business logic layer
│       │   ├── AuthService.java
│       │   ├── PositionService.java
│       │   ├── ApplicationService.java
│       │   └── WorkloadService.java
│       ├── servlet/                # Controller layer
│       │   ├── AuthServlet.java
│       │   ├── DashboardServlet.java
│       │   ├── PositionServlet.java
│       │   ├── ApplicationServlet.java
│       │   ├── ProfileServlet.java
│       │   ├── CVServlet.java
│       │   ├── AdminServlet.java
│       │   └── IndexServlet.java
│       └── filter/                 # Security filters
│           ├── AuthFilter.java
│           └── RoleFilter.java
├── WEB-INF/
│   ├── jsp/                        # View layer
│   │   ├── login.jsp
│   │   ├── register.jsp
│   │   ├── error.jsp
│   │   ├── ta/                     # TA views
│   │   │   ├── dashboard.jsp
│   │   │   ├── positions.jsp
│   │   │   ├── applications.jsp
│   │   │   └── profile.jsp
│   │   ├── mo/                     # MO views
│   │   │   ├── dashboard.jsp
│   │   │   ├── positions.jsp
│   │   │   ├── create-position.jsp
│   │   │   ├── applications.jsp
│   │   │   └── profile.jsp
│   │   └── admin/                  # Admin views
│   │       ├── dashboard.jsp
│   │       ├── workload.jsp
│   │       └── profile.jsp
│   ├── classes/                    # Compiled Java classes (not in Git)
│   └── web.xml                     # Servlet configuration
├── data/                           # Data storage
│   ├── users.csv                   # User data
│   ├── positions.csv               # Position data
│   ├── applications.csv            # Application data
│   └── cv/                         # CV file storage
├── css/
│   └── style.css                   # Application styles
├── js/
│   └── main.js                     # Client-side scripts
├── lib/                            # External libraries (not in Git)
│   └── jakarta.servlet-api-5.0.0.jar
├── .gitignore                      # Git ignore rules
├── build.bat                       # Build script
└── README.md                       # This file
\\\

---

## 🔒 Security Features

1. **Password Security**
   - SHA-256 encryption for all passwords
   - No plain text password storage
   - Secure password validation

2. **Session Management**
   - Server-side session storage
   - Automatic session timeout
   - Secure logout functionality

3. **Access Control**
   - Authentication filter for all protected routes
   - Role-based authorization
   - Strict role separation

4. **File Security**
   - CV file access control
   - File type validation
   - Secure file naming with UUID

5. **Input Validation**
   - Email format validation
   - Required field validation
   - SQL injection prevention (CSV-based storage)

---

## 📊 Version Information

**Version**: 1.0  
**Release Date**: March 2026  
**Status**: Production Ready

### V1.0 Features
✅ Complete user authentication system  
✅ Multi-role architecture (TA/MO/ADMIN)  
✅ Position management with detailed information  
✅ Application lifecycle management  
✅ CV upload and download  
✅ Profile management  
✅ Dashboard analytics  
✅ Workload reporting  
✅ Role-based access control  

---

## 📚 Additional Documentation

- **CODE_GUIDE.md** - Detailed code structure and API documentation
- **GIT_TUTORIAL.md** - Git workflow and collaboration guide
- **DEPLOYMENT.md** - Advanced deployment configurations
- **API_REFERENCE.md** - Complete API endpoint reference

---

## 🤝 Contributing

This project is developed as part of the Software Engineering Practice course at BUPT. For contribution guidelines, please refer to the course documentation.

---

## 📝 License

This project is developed for educational purposes at Beijing University of Posts and Telecommunications (BUPT).

---

## 📧 Contact

**Course**: Software Engineering Practice  
**Institution**: Beijing University of Posts and Telecommunications (BUPT)  
**Support TA**: Norman-Ou (kelayCHEN - 2024010445@bupt.cn)  
**Team Lead**: 404lonewolf404 (231222023)

---

**Maintained by**: TA Recruitment System Development Team  
**Last Updated**: March 2026
