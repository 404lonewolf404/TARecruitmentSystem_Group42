<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            margin: 0;
            padding: 20px 0;
        }
        
        .register-wrapper {
            width: 100%;
            max-width: 600px;
            padding: 20px;
        }
        
        .form-container {
            position: relative;
            overflow: hidden;
        }
        
        .form-container::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: linear-gradient(45deg, transparent, rgba(255,255,255,0.1), transparent);
            transform: rotate(45deg);
            animation: shine 3s infinite;
        }
        
        @keyframes shine {
            0%, 100% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
            50% { transform: translateX(100%) translateY(100%) rotate(45deg); }
        }
        
        .logo-container {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .logo-icon {
            font-size: 64px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 10px;
        }
        
        .form-container h1 {
            text-align: center;
            font-size: 28px;
            margin-bottom: 10px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        
        .form-container h2 {
            text-align: center;
            font-size: 18px;
            color: #7f8c8d;
            font-weight: 400;
            margin-bottom: 30px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }
        
        .input-group {
            position: relative;
            margin-bottom: 20px;
        }
        
        .input-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #555;
            font-size: 14px;
        }
        
        .input-group i {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #95a5a6;
            font-size: 16px;
            transition: color 0.3s;
            pointer-events: none;
        }
        
        .input-group.has-label i {
            top: calc(50% + 14px);
        }
        
        .input-group input,
        .input-group select {
            width: 100%;
            padding-left: 45px;
            height: 45px;
            font-size: 14px;
        }
        
        .input-group textarea {
            width: 100%;
            padding-left: 45px;
            padding-top: 12px;
            padding-right: 15px;
            font-size: 14px;
            height: auto;
            min-height: 100px;
            resize: vertical;
        }
        
        .input-group input:focus + i,
        .input-group select:focus + i {
            color: #667eea;
        }
        
        .role-cards {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 10px;
            margin-bottom: 20px;
        }
        
        .role-card {
            border: 2px solid #e0e0e0;
            border-radius: 12px;
            padding: 20px 15px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            background: white;
            position: relative;
        }
        
        .role-card:hover {
            border-color: #667eea;
            transform: translateY(-4px);
            box-shadow: 0 6px 16px rgba(102, 126, 234, 0.25);
        }
        
        .role-card.selected {
            border-color: #667eea;
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
        }
        
        .role-card i {
            font-size: 36px;
            margin-bottom: 10px;
            display: block;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            opacity: 0;
            transition: opacity 0.3s ease;
        }
        
        .role-card:hover i {
            opacity: 1;
        }
        
        .role-card .role-name {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 5px;
            font-size: 16px;
        }
        
        .role-card .role-desc {
            font-size: 12px;
            color: #7f8c8d;
            line-height: 1.3;
        }
        
        .btn-primary {
            height: 50px;
            font-size: 16px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .form-footer {
            text-align: center;
            margin-top: 25px;
        }
        
        .form-footer a {
            font-weight: 600;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        
        .password-strength {
            margin-top: 5px;
            font-size: 12px;
        }
        
        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
            }
            
            .role-cards {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body class="auth-page">
    <div class="register-wrapper">
        <div class="form-container">
            <div class="logo-container">
                <i class="fas fa-user-plus logo-icon"></i>
            </div>
            <h1>TA Recruitment System</h1>
            <h2>Create Your Account</h2>
            
            <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
            <% String successMessage = (String) request.getAttribute("successMessage"); %>
            
            <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i> <%= errorMessage %>
                </div>
            <% } %>
            
            <% if (successMessage != null && !successMessage.isEmpty()) { %>
                <div class="success-message">
                    <i class="fas fa-check-circle"></i> <%= successMessage %>
                </div>
            <% } %>
            
            <form action="<%= request.getContextPath() %>/auth/register" method="post" class="register-form">
                <div class="form-row">
                    <div class="input-group has-label">
                        <label for="name">Full Name <span class="required">*</span></label>
                        <input type="text" id="name" name="name" required 
                               placeholder="Enter your full name"
                               value="<%= request.getParameter("name") != null ? request.getParameter("name") : "" %>">
                        <i class="fas fa-user"></i>
                    </div>
                    
                    <div class="input-group has-label">
                        <label for="email">Email Address <span class="required">*</span></label>
                        <input type="email" id="email" name="email" required 
                               placeholder="your.email@example.com"
                               value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
                        <i class="fas fa-envelope"></i>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="input-group has-label">
                        <label for="password">Password <span class="required">*</span></label>
                        <input type="password" id="password" name="password" required 
                               placeholder="Min. 8 characters"
                               minlength="8">
                        <i class="fas fa-lock"></i>
                        <div id="password-strength" class="password-strength"></div>
                    </div>
                    
                    <div class="input-group has-label">
                        <label for="confirmPassword">Confirm Password <span class="required">*</span></label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required 
                               placeholder="Re-enter password"
                               minlength="8">
                        <i class="fas fa-lock"></i>
                    </div>
                </div>
                
                <div class="input-group">
                    <label>Select Your Role <span class="required">*</span></label>
                    <input type="hidden" id="role" name="role" required>
                    <div class="role-cards">
                        <div class="role-card" data-role="TA">
                            <i class="fas fa-chalkboard-teacher"></i>
                            <div class="role-name">TA</div>
                            <div class="role-desc">Teaching Assistant</div>
                        </div>
                        <div class="role-card" data-role="MO">
                            <i class="fas fa-user-tie"></i>
                            <div class="role-name">MO</div>
                            <div class="role-desc">Module Owner</div>
                        </div>
                        <div class="role-card" data-role="ADMIN">
                            <i class="fas fa-user-shield"></i>
                            <div class="role-name">Admin</div>
                            <div class="role-desc">Administrator</div>
                        </div>
                    </div>
                </div>

                <div class="input-group has-label" id="adminKeyGroup" style="display: none;">
                    <label for="adminRegisterKey">Admin Registration Key <span class="required">*</span></label>
                    <input type="password" id="adminRegisterKey" name="adminRegisterKey"
                           placeholder="Enter admin registration key"
                           value="<%= request.getParameter("adminRegisterKey") != null ? request.getParameter("adminRegisterKey") : "" %>">
                    <i class="fas fa-key"></i>
                </div>
                
                <div class="input-group has-label" id="skillsGroup" style="display: none;">
                    <label for="skills">Skills & Experience (Optional)</label>
                    <textarea id="skills" name="skills" 
                              placeholder="e.g., Java, Python, Database Management, Web Development..."><%= request.getParameter("skills") != null ? request.getParameter("skills") : "" %></textarea>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-full">
                        <i class="fas fa-user-plus"></i> Create Account
                    </button>
                </div>
            </form>
            
            <div class="form-footer">
                <p>Already have an account? <a href="<%= request.getContextPath() %>/auth/login"><i class="fas fa-sign-in-alt"></i> Login Now</a></p>
            </div>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
    <script>
        // Role selection
        document.querySelectorAll('.role-card').forEach(card => {
            card.addEventListener('click', function() {
                document.querySelectorAll('.role-card').forEach(c => c.classList.remove('selected'));
                this.classList.add('selected');
                document.getElementById('role').value = this.dataset.role;
                toggleAdminKeyField(this.dataset.role);
                toggleSkillsField(this.dataset.role);
            });
        });
        
        // Pre-select role if exists
        const preSelectedRole = '<%= request.getParameter("role") != null ? request.getParameter("role") : "" %>';
        if (preSelectedRole) {
            document.querySelector(`.role-card[data-role="${preSelectedRole}"]`)?.classList.add('selected');
            document.getElementById('role').value = preSelectedRole;
            toggleAdminKeyField(preSelectedRole);
            toggleSkillsField(preSelectedRole);
        }

        function toggleAdminKeyField(role) {
            var keyGroup = document.getElementById('adminKeyGroup');
            var keyInput = document.getElementById('adminRegisterKey');
            if (role === 'ADMIN') {
                keyGroup.style.display = 'block';
                keyInput.required = true;
            } else {
                keyGroup.style.display = 'none';
                keyInput.required = false;
                keyInput.value = '';
            }
        }

        function toggleSkillsField(role) {
            var skillsGroup = document.getElementById('skillsGroup');
            var skillsInput = document.getElementById('skills');
            if (role === 'TA') {
                skillsGroup.style.display = 'block';
            } else {
                skillsGroup.style.display = 'none';
                skillsInput.value = '';
            }
        }
        
        // Password strength check
        document.getElementById('password').addEventListener('input', function() {
            var password = this.value;
            var strengthDiv = document.getElementById('password-strength');
            
            if (password.length === 0) {
                strengthDiv.innerHTML = '';
                return;
            }
            
            var strength = checkPasswordStrength(password);
            var strengthText = '';
            var strengthClass = '';
            
            switch(strength) {
                case 'Very Weak':
                    strengthText = '<i class="fas fa-times-circle"></i> Very Weak';
                    strengthClass = 'strength-very-weak';
                    break;
                case 'Weak':
                    strengthText = '<i class="fas fa-exclamation-circle"></i> Weak';
                    strengthClass = 'strength-weak';
                    break;
                case 'Medium':
                    strengthText = '<i class="fas fa-check-circle"></i> Medium';
                    strengthClass = 'strength-medium';
                    break;
                case 'Strong':
                    strengthText = '<i class="fas fa-check-circle"></i> Strong';
                    strengthClass = 'strength-strong';
                    break;
            }
            
            strengthDiv.innerHTML = '<span class="' + strengthClass + '">' + strengthText + '</span>';
        });
        
        function checkPasswordStrength(password) {
            if (password.length < 6) return 'Very Weak';
            if (password.length < 8) return 'Weak';
            
            var score = 0;
            if (/[a-z]/.test(password)) score++;
            if (/[A-Z]/.test(password)) score++;
            if (/\d/.test(password)) score++;
            if (/[!@#$%^&*]/.test(password)) score++;
            
            if (score < 2) return 'Weak';
            if (score < 3) return 'Medium';
            return 'Strong';
        }
        
        // Form validation
        document.querySelector('.register-form').addEventListener('submit', function(e) {
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            var role = document.getElementById('role').value;
            var adminRegisterKey = document.getElementById('adminRegisterKey').value;
            
            if (!role) {
                e.preventDefault();
                alert('Please select a role!');
                return false;
            }
            
            if (password.length < 8) {
                e.preventDefault();
                alert('Password must be at least 8 characters!');
                return false;
            }
            
            if (!/[a-zA-Z]/.test(password) || !/\d/.test(password)) {
                e.preventDefault();
                alert('Password must contain both letters and numbers!');
                return false;
            }
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Passwords do not match!');
                return false;
            }

            if (role === 'ADMIN' && (!adminRegisterKey || adminRegisterKey.trim() === '')) {
                e.preventDefault();
                alert('Admin registration key is required!');
                return false;
            }
        });
    </script>
</body>
</html>
