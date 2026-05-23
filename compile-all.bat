@echo off
cd webapps\TARecruitmentSystem

echo Compiling model layer...
javac -encoding UTF-8 -d WEB-INF/classes -cp "../../lib/*" src/com/bupt/tarecruitment/model/ApplicationStatus.java src/com/bupt/tarecruitment/model/PositionStatus.java src/com/bupt/tarecruitment/model/UserRole.java src/com/bupt/tarecruitment/model/NotificationType.java
javac -encoding UTF-8 -d WEB-INF/classes -cp "../../lib/*;WEB-INF/classes" src/com/bupt/tarecruitment/model/Application.java src/com/bupt/tarecruitment/model/Position.java src/com/bupt/tarecruitment/model/User.java src/com/bupt/tarecruitment/model/Notification.java src/com/bupt/tarecruitment/model/Message.java src/com/bupt/tarecruitment/model/Favorite.java

echo Compiling util layer...
javac -encoding UTF-8 -d WEB-INF/classes -cp "../../lib/*;WEB-INF/classes" src/com/bupt/tarecruitment/util/ValidationUtil.java

echo Compiling dao layer...
javac -encoding UTF-8 -d WEB-INF/classes -cp "../../lib/*;WEB-INF/classes" src/com/bupt/tarecruitment/dao/CSVDataStore.java src/com/bupt/tarecruitment/dao/UserDAO.java src/com/bupt/tarecruitment/dao/PositionDAO.java src/com/bupt/tarecruitment/dao/ApplicationDAO.java src/com/bupt/tarecruitment/dao/NotificationDAO.java src/com/bupt/tarecruitment/dao/MessageDAO.java src/com/bupt/tarecruitment/dao/FavoriteDAO.java

echo Compiling service layer...
javac -encoding UTF-8 -d WEB-INF/classes -cp "../../lib/*;WEB-INF/classes" src/com/bupt/tarecruitment/service/AuthService.java src/com/bupt/tarecruitment/service/PositionService.java src/com/bupt/tarecruitment/service/ApplicationService.java src/com/bupt/tarecruitment/service/SearchService.java src/com/bupt/tarecruitment/service/StatisticsService.java src/com/bupt/tarecruitment/service/WorkloadService.java src/com/bupt/tarecruitment/service/NotificationService.java src/com/bupt/tarecruitment/service/MessageService.java src/com/bupt/tarecruitment/service/FavoriteService.java

echo Compiling filter layer...
javac -encoding UTF-8 -d WEB-INF/classes -cp "../../lib/*;WEB-INF/classes" src/com/bupt/tarecruitment/filter/AuthFilter.java src/com/bupt/tarecruitment/filter/RoleFilter.java

echo Compiling servlet layer...
javac -encoding UTF-8 -d WEB-INF/classes -cp "../../lib/*;WEB-INF/classes" src/com/bupt/tarecruitment/servlet/AuthServlet.java src/com/bupt/tarecruitment/servlet/DashboardServlet.java src/com/bupt/tarecruitment/servlet/ProfileServlet.java src/com/bupt/tarecruitment/servlet/PositionServlet.java src/com/bupt/tarecruitment/servlet/ApplicationServlet.java src/com/bupt/tarecruitment/servlet/AdminServlet.java src/com/bupt/tarecruitment/servlet/CVServlet.java src/com/bupt/tarecruitment/servlet/NotificationServlet.java src/com/bupt/tarecruitment/servlet/MessageServlet.java src/com/bupt/tarecruitment/servlet/FavoriteServlet.java src/com/bupt/tarecruitment/servlet/IndexServlet.java src/com/bupt/tarecruitment/servlet/TestServlet.java

echo Done! Restarting Tomcat...
cd ..\..
call restart-tomcat.bat
