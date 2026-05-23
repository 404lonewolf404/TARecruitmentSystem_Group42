<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="auth-page">
<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    String email = (String) request.getAttribute("email");
    Boolean showResetForm = (Boolean) request.getAttribute("showResetForm");
    Integer expiresInSeconds = (Integer) request.getAttribute("expiresInSeconds");
    if (showResetForm == null) {
        showResetForm = false;
    }
%>
    <div class="form-container" style="max-width: 560px; margin: 40px auto;">
        <h1>TA Recruitment System</h1>
        <h2>Forgot Password</h2>

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="error-message"><i class="fas fa-exclamation-circle"></i> <%= errorMessage %></div>
        <% } %>
        <% if (successMessage != null && !successMessage.isEmpty()) { %>
            <div class="success-message"><i class="fas fa-check-circle"></i> <%= successMessage %></div>
        <% } %>

        <div class="card">
            <h3><i class="fas fa-unlock-alt"></i> Password Recovery</h3>
            <form action="<%= request.getContextPath() %>/auth/reset-password" method="post">
                <div class="form-group">
                    <label for="resetEmail">Email Address</label>
                    <input type="email" id="resetEmail" name="email" required
                           value="<%= email != null ? email : "" %>">
                </div>
                <div class="form-group">
                    <label for="code">Verification Code</label>
                    <div style="display: flex; gap: 10px; align-items: center;">
                        <input type="text" id="code" name="code" maxlength="6" required placeholder="6-digit code" style="flex: 1;">
                        <button type="button" id="sendCodeBtn" class="btn btn-secondary" style="white-space: nowrap;">
                            Get Code
                        </button>
                    </div>
                    <p id="codeHint" style="margin-top: 8px; color: #666; font-size: 14px;">
                        Demo mode: code is printed in the server console.
                    </p>
                </div>
                <div class="form-group">
                    <label for="newPassword">New Password</label>
                    <input type="password" id="newPassword" name="newPassword" required minlength="8">
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm New Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required minlength="8">
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-full">
                        <i class="fas fa-save"></i> Reset Password
                    </button>
                </div>
            </form>
        </div>

        <div class="form-footer" style="text-align: center; margin-top: 20px;">
            <a href="<%= request.getContextPath() %>/auth/login"><i class="fas fa-arrow-left"></i> Back to Login</a>
        </div>
    </div>

    <script>
        (function () {
            var sendCodeBtn = document.getElementById('sendCodeBtn');
            var emailInput = document.getElementById('resetEmail');
            var codeHint = document.getElementById('codeHint');
            var timer = null;

            function startCountdown(seconds) {
                var left = seconds;
                sendCodeBtn.disabled = true;
                sendCodeBtn.textContent = left + "s";
                timer = setInterval(function () {
                    left -= 1;
                    if (left <= 0) {
                        clearInterval(timer);
                        timer = null;
                        sendCodeBtn.disabled = false;
                        sendCodeBtn.textContent = "Get Code";
                    } else {
                        sendCodeBtn.textContent = left + "s";
                    }
                }, 1000);
            }

            sendCodeBtn.addEventListener('click', function () {
                var email = (emailInput.value || '').trim();
                if (!email) {
                    codeHint.style.color = "#dc2626";
                    codeHint.textContent = "Please enter your email first.";
                    return;
                }

                sendCodeBtn.disabled = true;
                sendCodeBtn.textContent = "Sending...";
                codeHint.style.color = "#666";
                codeHint.textContent = "Requesting verification code...";

                fetch('<%= request.getContextPath() %>/auth/send-reset-code', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'email=' + encodeURIComponent(email)
                })
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    if (data.ok) {
                        codeHint.style.color = "#166534";
                        codeHint.textContent = data.message + " Countdown: " + data.expiresInSeconds + "s";
                        startCountdown(data.expiresInSeconds || 60);
                    } else {
                        codeHint.style.color = "#dc2626";
                        codeHint.textContent = data.message || "Failed to get verification code.";
                        sendCodeBtn.disabled = false;
                        sendCodeBtn.textContent = "Get Code";
                    }
                })
                .catch(function () {
                    codeHint.style.color = "#dc2626";
                    codeHint.textContent = "Network error. Please try again.";
                    sendCodeBtn.disabled = false;
                    sendCodeBtn.textContent = "Get Code";
                });
            });

            <% if (expiresInSeconds != null && expiresInSeconds > 0) { %>
            startCountdown(<%= expiresInSeconds %>);
            <% } %>
        })();
    </script>
</body>
</html>
