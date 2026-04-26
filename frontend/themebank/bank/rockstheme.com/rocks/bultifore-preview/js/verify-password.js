$(document).ready(function() {
    
    // Xử lý submit form verify password
    $('#verifyPasswordForm').on('submit', function(e) {
        e.preventDefault(); // Ngăn chặn submit mặc định
        
        // Collect form data
        var formData = {
            email: $('#email').val(),
            userName: $('#username').val(),
            OldPassword: $('#currentPassword').val(),
            NewPassword: $('#newPassword').val()
        };
        
        // Validate dữ liệu
        if (!formData.email || !formData.userName || !formData.OldPassword || !formData.NewPassword || !$('#confirmPassword').val()) {
            $('#msgSubmit').removeClass('hidden').addClass('alert alert-danger').text('Vui lòng điền đầy đủ thông tin!');
            return;
        }
        
        // Validate email format
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(formData.email)) {
            $('#msgSubmit').removeClass('hidden').addClass('alert alert-danger').text('Email không hợp lệ!');
            return;
        }
        
        // Validate password length
        if (formData.NewPassword.length < 8) {
            $('#msgSubmit').removeClass('hidden').addClass('alert alert-danger').text('Mật khẩu mới phải có ít nhất 8 ký tự!');
            return;
        }
        
        // Check if passwords match
        if (formData.NewPassword !== $('#confirmPassword').val()) {
            $('#msgSubmit').removeClass('hidden').addClass('alert alert-danger').text('Mật khẩu xác nhận không khớp!');
            return;
        }
        
        // Check if new password is different from current password
        if (formData.NewPassword === formData.OldPassword) {
            $('#msgSubmit').removeClass('hidden').addClass('alert alert-danger').text('Mật khẩu mới phải khác mật khẩu hiện tại!');
            return;
        }
        
        // Check if agree terms
        if (!$('#agreeTerms').is(':checked')) {
            $('#msgSubmit').removeClass('hidden').addClass('alert alert-danger').text('Vui lòng xác nhận tài khoản của bạn!');
            return;
        }
        
        // Gửi POST request tới API
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/auth/verify', // Thay đổi URL API của bạn ở đây
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function(response, status, xhr) {
                console.log('Success:', response);
                // Kiểm tra status code 200
                if (xhr.status === 200) {
                    var successMessage = response.message || 'Cập nhật mật khẩu thành công!';
                    $('#msgSubmit').removeClass('hidden alert-danger').addClass('alert alert-success').text(successMessage);
                    // Reset form
                    $('#verifyPasswordForm')[0].reset();
                    // Chuyển hướng sau vài giây
                    setTimeout(function() {
                        window.location.href = 'login.html';
                    }, 2000);
                } else {
                    // Nếu không phải status 200, lấy message từ response hoặc dùng mặc định
                    var errorMessage = response.message || 'Cập nhật mật khẩu thất bại. Vui lòng thử lại!';
                    $('#msgSubmit').removeClass('hidden alert-success').addClass('alert alert-danger').text(errorMessage);
                }
            },
            error: function(xhr, status, error) {
                console.log('Error:', error);
                let errorMessage = 'Có lỗi xảy ra. Vui lòng thử lại!';
                
                // Xử lý riêng cho lỗi 401 (Unauthorized)
                if (xhr.status === 401) {
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMessage = xhr.responseJSON.message;
                    } else {
                        errorMessage = 'Xác thực thất bại. Vui lòng kiểm tra thông tin đăng nhập!';
                    }
                } else if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                }
                
                $('#msgSubmit').removeClass('hidden alert-success').addClass('alert alert-danger').text(errorMessage);
            }
        });
    });
    
});
