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
        
        if (!localStorage.getItem("deviceId")) {
            localStorage.setItem(
                "deviceId",
                crypto.randomUUID()
            );
        }

        // Gọi API login
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/auth/sign-in', // Thay đổi URL API của bạn
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                username: username,
                password: password,
                deviceId: localStorage.getItem("deviceId")
            }),
            success: function(response) {
                // Kiểm tra response có code 200 và data
                if (response.code === 200 && response.data) {
                    // Lấy accessToken và refreshToken từ response.data
                    var accessToken = response.data.accessToken;
                    var refreshToken = response.data.refreshToken;
                    
                    // Lưu tokens và user thông tin vào auth storage
                    Auth.saveLoginData({
                        accessToken: accessToken,
                        refreshToken: refreshToken
                    }, username);

                    showMessage('Login successful!', 'success');

                    // Hiển thị preloader và redirect sau 3 giây
                    var preloader = $('#preloader');
                    if (preloader.length === 0) {
                        preloader = $('<div id="preloader"></div>').css({
                            'position': 'fixed',
                            'left': '0',
                            'top': '0',
                            'z-index': '99999',
                            'width': '100%',
                            'height': '100%',
                            'background-color': '#1b2654',
                            'background-image': 'url(img/logo/preloader.gif)',
                            'background-position': 'center center',
                            'background-repeat': 'no-repeat',
                            'overflow': 'visible',
                            'display': 'block'
                        });
                        $(document.body).append(preloader);
                    } else {
                        preloader.fadeIn('fast');
                    }
                    setTimeout(function() {
                        window.location.href = 'index-2.html'; // Thay đổi URL trang
                    }, 3000);
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
        var token = Auth.getAccessToken();
        if (token) {
            // User đã login, có thể redirect tới dashboard
            console.log('User already logged in');
        }
    }
    
    // Gọi kiểm tra khi trang load
    checkAuthToken();
});

// Optional async login form handler (compatibility with another layout)
$(document).ready(function() {
    $("#loginForm").submit(async function(e) {
        e.preventDefault();

        const username = $("#username").val();
        const password = $("#password").val();
        const deviceId = localStorage.getItem('deviceId');

        try {
            const response = await $.ajax({
                url: 'http://localhost:8080/auth/login',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ username, password, deviceId })
            });

            // Support responses that return token at root or under .data
            const token = response.accessToken || (response.data && response.data.accessToken);
            if (token) {
                // Keep backward-compatible key and canonical Auth storage
                localStorage.setItem('accessToken', token);
                if (window.Auth && window.Auth.saveLoginData) {
                    window.Auth.saveLoginData({ accessToken: token });
                }

                // Hiển thị preloader và redirect sau 3 giây
                var preloader = $('#preloader');
                if (preloader.length === 0) {
                    preloader = $('<div id="preloader"></div>').css({
                        'position': 'fixed',
                        'left': '0',
                        'top': '0',
                        'z-index': '99999',
                        'width': '100%',
                        'height': '100%',
                        'background-color': '#1b2654',
                        'background-image': 'url(img/logo/preloader.gif)',
                        'background-position': 'center center',
                        'background-repeat': 'no-repeat',
                        'overflow': 'visible',
                        'display': 'block'
                    });
                    $(document.body).append(preloader);
                } else {
                    preloader.fadeIn('fast');
                }
                setTimeout(function() {
                    window.location.href = 'index-2.html';
                }, 3000);
            } else {
                alert('Login did not return access token');
            }

        } catch (err) {
            var msg = (err && err.responseText) ? err.responseText : 'Login failed';
            alert(msg);
        }
    });
});


