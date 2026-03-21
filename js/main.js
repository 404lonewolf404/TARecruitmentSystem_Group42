// TA Recruitment System - Main JavaScript

// Form validation
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            isValid = false;
            input.style.borderColor = '#e74c3c';
        } else {
            input.style.borderColor = '#ddd';
        }
    });
    
    return isValid;
}

// Email validation
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

// Password validation
function validatePassword(password) {
    return password.length >= 6;
}

// Confirm dialog for delete/withdraw actions
function confirmAction(message) {
    return confirm(message);
}

// Show/hide elements
function toggleElement(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.classList.toggle('hidden');
    }
}

// Display alert message
function showAlert(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = message;
    
    const container = document.querySelector('.container');
    if (container) {
        container.insertBefore(alertDiv, container.firstChild);
        
        // Auto-hide after 5 seconds
        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    }
}

// Form submission with validation
function submitFormWithValidation(formId, validationFn) {
    const form = document.getElementById(formId);
    if (!form) return;
    
    form.addEventListener('submit', function(e) {
        if (validationFn && !validationFn()) {
            e.preventDefault();
            showAlert('请填写所有必填字�?, 'error');
            return false;
        }
    });
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Add validation to login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            const email = document.getElementById('email');
            const password = document.getElementById('password');
            
            if (!email || !password) return;
            
            if (!validateEmail(email.value)) {
                e.preventDefault();
                showAlert('请输入有效的邮箱地址', 'error');
                return false;
            }
            
            if (!validatePassword(password.value)) {
                e.preventDefault();
                showAlert('密码至少需�?个字�?, 'error');
                return false;
            }
        });
    }
    
    // Add validation to register form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            const email = document.getElementById('email');
            const password = document.getElementById('password');
            const name = document.getElementById('name');
            
            if (!email || !password || !name) return;
            
            if (!name.value.trim()) {
                e.preventDefault();
                showAlert('请输入姓�?, 'error');
                return false;
            }
            
            if (!validateEmail(email.value)) {
                e.preventDefault();
                showAlert('请输入有效的邮箱地址', 'error');
                return false;
            }
            
            if (!validatePassword(password.value)) {
                e.preventDefault();
                showAlert('密码至少需�?个字�?, 'error');
                return false;
            }
        });
    }
    
    // Add confirmation to delete buttons
    const deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirmAction('确定要删除吗？此操作无法撤销�?)) {
                e.preventDefault();
                return false;
            }
        });
    });
    
    // Add confirmation to withdraw buttons
    const withdrawButtons = document.querySelectorAll('.btn-withdraw');
    withdrawButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirmAction('确定要撤回申请吗�?)) {
                e.preventDefault();
                return false;
            }
        });
    });
    
    // Add confirmation to select buttons
    const selectButtons = document.querySelectorAll('.btn-select');
    selectButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirmAction('确定要选择此申请者吗？其他申请将被拒绝�?)) {
                e.preventDefault();
                return false;
            }
        });
    });
});

// Utility function to format date
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

// Utility function to sort table
function sortTable(tableId, columnIndex) {
    const table = document.getElementById(tableId);
    if (!table) return;
    
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    rows.sort((a, b) => {
        const aValue = a.cells[columnIndex].textContent.trim();
        const bValue = b.cells[columnIndex].textContent.trim();
        
        // Try to parse as number
        const aNum = parseFloat(aValue);
        const bNum = parseFloat(bValue);
        
        if (!isNaN(aNum) && !isNaN(bNum)) {
            return aNum - bNum;
        }
        
        return aValue.localeCompare(bValue);
    });
    
    rows.forEach(row => tbody.appendChild(row));
}

// Real-time form validation
function setupRealtimeValidation(formId) {
    const form = document.getElementById(formId);
    if (!form) return;
    
    const inputs = form.querySelectorAll('input, select, textarea');
    
    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });
        
        input.addEventListener('input', function() {
            if (this.classList.contains('invalid')) {
                validateField(this);
            }
        });
    });
}

// Validate individual field
function validateField(field) {
    let isValid = true;
    let errorMessage = '';
    
    // Check required fields
    if (field.hasAttribute('required') && !field.value.trim()) {
        isValid = false;
        errorMessage = '此字段为必填�?;
    }
    
    // Check email format
    if (field.type === 'email' && field.value && !validateEmail(field.value)) {
        isValid = false;
        errorMessage = '请输入有效的邮箱地址';
    }
    
    // Check password length
    if (field.type === 'password' && field.value && !validatePassword(field.value)) {
        isValid = false;
        errorMessage = '密码至少需�?个字�?;
    }
    
    // Check number fields
    if (field.type === 'number' && field.value) {
        const num = parseFloat(field.value);
        if (isNaN(num) || num < 0) {
            isValid = false;
            errorMessage = '请输入有效的数字';
        }
    }
    
    // Update field styling
    if (isValid) {
        field.style.borderColor = '#27ae60';
        field.classList.remove('invalid');
        removeFieldError(field);
    } else {
        field.style.borderColor = '#e74c3c';
        field.classList.add('invalid');
        showFieldError(field, errorMessage);
    }
    
    return isValid;
}

// Show field error message
function showFieldError(field, message) {
    removeFieldError(field);
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'field-error';
    errorDiv.style.color = '#e74c3c';
    errorDiv.style.fontSize = '12px';
    errorDiv.style.marginTop = '5px';
    errorDiv.textContent = message;
    
    field.parentNode.appendChild(errorDiv);
}

// Remove field error message
function removeFieldError(field) {
    const existingError = field.parentNode.querySelector('.field-error');
    if (existingError) {
        existingError.remove();
    }
}

// Filter table rows
function filterTable(tableId, searchInputId) {
    const input = document.getElementById(searchInputId);
    const table = document.getElementById(tableId);
    
    if (!input || !table) return;
    
    input.addEventListener('keyup', function() {
        const filter = this.value.toLowerCase();
        const rows = table.querySelectorAll('tbody tr');
        
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            if (text.includes(filter)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });
}

// Highlight workload levels
function highlightWorkload() {
    const workloadCells = document.querySelectorAll('.workload-cell');
    
    workloadCells.forEach(cell => {
        const hours = parseInt(cell.textContent);
        const row = cell.parentElement;
        
        if (hours >= 20) {
            row.classList.add('high-workload');
        } else if (hours >= 10) {
            row.classList.add('medium-workload');
        } else {
            row.classList.add('low-workload');
        }
    });
}

// Auto-save form data to localStorage
function setupAutoSave(formId) {
    const form = document.getElementById(formId);
    if (!form) return;
    
    const inputs = form.querySelectorAll('input, select, textarea');
    
    // Load saved data
    inputs.forEach(input => {
        const savedValue = localStorage.getItem(`${formId}_${input.name}`);
        if (savedValue && !input.value) {
            input.value = savedValue;
        }
    });
    
    // Save on input
    inputs.forEach(input => {
        input.addEventListener('input', function() {
            localStorage.setItem(`${formId}_${this.name}`, this.value);
        });
    });
    
    // Clear on submit
    form.addEventListener('submit', function() {
        inputs.forEach(input => {
            localStorage.removeItem(`${formId}_${input.name}`);
        });
    });
}

// Character counter for textarea
function setupCharacterCounter(textareaId, maxLength) {
    const textarea = document.getElementById(textareaId);
    if (!textarea) return;
    
    const counter = document.createElement('div');
    counter.className = 'character-counter';
    counter.style.textAlign = 'right';
    counter.style.fontSize = '12px';
    counter.style.color = '#7f8c8d';
    counter.style.marginTop = '5px';
    
    textarea.parentNode.appendChild(counter);
    
    function updateCounter() {
        const length = textarea.value.length;
        counter.textContent = `${length}${maxLength ? '/' + maxLength : ''} 字符`;
        
        if (maxLength && length > maxLength) {
            counter.style.color = '#e74c3c';
        } else {
            counter.style.color = '#7f8c8d';
        }
    }
    
    textarea.addEventListener('input', updateCounter);
    updateCounter();
}

// Smooth scroll to element
function scrollToElement(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}

// Copy to clipboard
function copyToClipboard(text) {
    if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(() => {
            showAlert('已复制到剪贴�?, 'success');
        }).catch(() => {
            showAlert('复制失败', 'error');
        });
    } else {
        // Fallback for older browsers
        const textarea = document.createElement('textarea');
        textarea.value = text;
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand('copy');
        document.body.removeChild(textarea);
        showAlert('已复制到剪贴�?, 'success');
    }
}

// Debounce function for performance
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Enhanced initialization
document.addEventListener('DOMContentLoaded', function() {
    // Setup real-time validation for all forms
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        if (form.id) {
            setupRealtimeValidation(form.id);
        }
    });
    
    // Highlight workload if on workload page
    if (document.querySelector('.workload-cell')) {
        highlightWorkload();
    }
    
    // Setup character counters for textareas
    const textareas = document.querySelectorAll('textarea');
    textareas.forEach(textarea => {
        if (textarea.id) {
            const maxLength = textarea.getAttribute('maxlength');
            setupCharacterCounter(textarea.id, maxLength);
        }
    });
    
    // Add smooth scrolling to all anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href').substring(1);
            scrollToElement(targetId);
        });
    });
    
    // Add loading state to forms on submit
    forms.forEach(form => {
        form.addEventListener('submit', function() {
            const submitBtn = this.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.textContent = '提交�?..';
            }
        });
    });
});
