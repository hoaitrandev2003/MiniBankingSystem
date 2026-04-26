$(document).ready(function() {
    // Xử lý submit form login
    $('#contactForm').on('submit', function(e) {
        e.preventDefault();
        
        // Lấy giá trị từ form
        var username = $('#name').val();
        var password = $('#msg_subject').val();
        
        // Validate form
        if (!username || !password) {
            showMessage('Please enter username and password', 'error');
            return false;
        }
        
        // Tắt nút submit trong quá trình gửi
        var $submitBtn = $('#submit');
        $submitBtn.prop('disabled', true).text('Loading...');
        
        // Gọi API login
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/auth/sign-in', // Thay đổi URL API của bạn
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                username: username,
                password: password
            }),
            success: function(response) {
                // Kiểm tra response có code 200 và data
                if (response.code === 200 && response.data) {
                    // Lấy accessToken và refreshToken từ response.data
                    var accessToken = response.data.accessToken;
                    var refreshToken = response.data.refreshToken;
                    
                    // Lưu tokens vào localStorage
                    localStorage.setItem('access_token', accessToken);
                    localStorage.setItem('refresh_token', refreshToken);
                    
                    // Decode JWT để lấy thông tin user
                    try {
                        var payload = JSON.parse(atob(accessToken.split('.')[1]));
                        var user = JSON.parse(payload.sub);
                        // Lưu user object
                        localStorage.setItem('user', JSON.stringify(user));
                        // Lưu username riêng để dùng cho logout
                        localStorage.setItem('username', username);
                    } catch (e) {
                        console.error('Lỗi khi decode token:', e);
                        // Nếu decode thất bại, vẫn lưu username
                        localStorage.setItem('username', username);
                    }
                    
                    showMessage('Login successful!', 'success');

                    // Redirect sau 1.5 giây
                    setTimeout(function() {
                        window.location.href = 'index-3.html'; // Thay đổi URL trang
                    }, 1500);
                } else {
                    showMessage('Login failed', 'error');
                }
            },
            error: function(xhr, status, error) {
                // Xử lý lỗi
                var errorMsg = 'Login failed';
                
                if (xhr.status === 401) {
                    errorMsg = 'Invalid username or password';
                } else if (xhr.status === 400) {
                    errorMsg = 'Invalid request';
                } else if (xhr.status === 500) {
                    errorMsg = 'Server error. Please try again later';
                } else if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                } else if (error) {
                    errorMsg = error;
                }
                
                showMessage(errorMsg, 'error');
                console.error('AJAX Error:', status, error);
            },
            complete: function() {
                // Bật lại nút submit
                $submitBtn.prop('disabled', false).text('Login');
            }
        });
        
        return false;
    });
    
    // Hàm hiển thị message
    function showMessage(message, type) {
        var $msgSubmit = $('#msgSubmit');
        $msgSubmit.removeClass('hidden');
        $msgSubmit.removeClass('text-success text-danger');
        
        if (type === 'success') {
            $msgSubmit.addClass('text-success');
        } else {
            $msgSubmit.addClass('text-danger');
        }
        
        $msgSubmit.text(message);
        
        // Ẩn message sau 5 giây (nếu là error)
        if (type === 'error') {
            setTimeout(function() {
                $msgSubmit.addClass('hidden');
            }, 5000);
        }
    }
    
    // Kiểm tra nếu user đã login trước đó
    function checkAuthToken() {
        var token = localStorage.getItem('access_token');
        if (token) {
            // User đã login, có thể redirect tới dashboard
            console.log('User already logged in');
        }
    }
    
    // Gọi kiểm tra khi trang load
    checkAuthToken();
});

// Hàm helper lấy token từ localStorage
function getAuthToken() {
    return localStorage.getItem('access_token');
}

// Hàm helper lấy user từ localStorage
function getUser() {
    var user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

// Hàm helper lấy refresh token từ localStorage
function getRefreshToken() {
    return localStorage.getItem('refresh_token');
}

// Hàm helper xóa token (logout)
function logout() {
    var token = localStorage.getItem('access_token');
    var username = localStorage.getItem('username');
    
    // Gọi API logout
    $.ajax({
        type: 'POST',
        url: 'http://localhost:8080/auth/sign-out', // Endpoint logout
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify({
            username: username
        }),
        success: function(response) {
            // Nếu nhận được code 200 thì xóa tokens
            if (response.code === 200) {
                localStorage.removeItem('access_token');
                localStorage.removeItem('refresh_token');
                localStorage.removeItem('user');
                localStorage.removeItem('username');
                window.location.href = 'login.html';
            } else {
                alert('Logout failed. Please try again.');
            }
        },
        error: function(xhr, status, error) {
            console.error('Logout error:', error);
            // Tất cả lỗi đều alert thất bại, không xóa localStorage
            alert('Logout failed. Please try again.');
        }
    });
}
