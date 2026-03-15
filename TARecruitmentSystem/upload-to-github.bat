@echo off
chcp 65001 >nul
echo ========================================
echo Upload TARecruitmentSystem to GitHub
echo ========================================
echo.

echo This script will help you upload ONLY the TARecruitmentSystem project to GitHub
echo without affecting your local Tomcat structure.
echo.

echo Step 1: Initialize Git repository (if not already done)
echo --------------------------------------------------------
cd /d "%~dp0"
if not exist ".git" (
    echo Initializing Git repository...
    git init
    echo Git repository initialized!
) else (
    echo Git repository already exists.
)
echo.

echo Step 2: Add all files to Git
echo --------------------------------------------------------
git add .
echo.

echo Step 3: Commit changes
echo --------------------------------------------------------
set /p commit_msg="Enter commit message (or press Enter for default): "
if "%commit_msg%"=="" set commit_msg=Initial commit - TA Recruitment System

git commit -m "%commit_msg%"
echo.

echo Step 4: Add GitHub remote repository
echo --------------------------------------------------------
echo Please create a new repository on GitHub first, then enter the repository URL.
echo Example: https://github.com/yourusername/ta-recruitment-system.git
echo.
set /p github_url="Enter GitHub repository URL: "

if "%github_url%"=="" (
    echo ERROR: GitHub URL cannot be empty!
    pause
    exit /b 1
)

git remote add origin %github_url% 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Remote 'origin' already exists. Updating URL...
    git remote set-url origin %github_url%
)
echo.

echo Step 5: Push to GitHub
echo --------------------------------------------------------
echo Pushing to GitHub...
git branch -M main
git push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS! Project uploaded to GitHub!
    echo ========================================
    echo.
    echo Your project is now on GitHub: %github_url%
    echo.
    echo Your local Tomcat structure remains unchanged.
    echo The project is still at: E:\Tomcat\apache-tomcat-10.1.28\webapps\TARecruitmentSystem
    echo.
) else (
    echo.
    echo ========================================
    echo Upload FAILED!
    echo ========================================
    echo.
    echo Please check:
    echo 1. GitHub repository URL is correct
    echo 2. You have permission to push to this repository
    echo 3. You are logged in to GitHub (use 'git config' to set credentials)
    echo.
)

pause
