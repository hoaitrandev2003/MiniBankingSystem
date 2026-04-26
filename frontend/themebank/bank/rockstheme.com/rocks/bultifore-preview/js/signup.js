$(document).ready(function() {
        // Xử lý submit form
    $('#contactForm').on('submit', function(e) {
        e.preventDefault(); // Ngăn chặn submit mặc định
        
        // Collect form data
        var formData = {
            email: $('#email').val(),
            fullName: $('#fullName').val(),
            phone: $('#phone').val(),
            gender: $('#gender').val(),
            dateOfBirth: $('#datetime-local').val(),
            address: $('#address').val(),
            identityNumber: $('#identityNumber').val()
        };
        
        // Validate dữ liệu
        if (!formData.email || !formData.fullName || !formData.phone || !formData.gender || 
            !formData.dateOfBirth || !formData.address || !formData.identityNumber) {
            $('#msgSubmit').removeClass('hidden').addClass('alert alert-danger').text('Vui lòng điền đầy đủ thông tin!');
            return;
        }
        
        // Gửi POST request tới API
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/auth/sign-up', // Thay đổi URL API của bạn ở đây
            contentType: 'application/json',
            data: JSON.stringify(formData),
            dataType: 'json',
            success: function(response, status, xhr) {
                console.log('Success:', response);
                // Kiểm tra status code 200
                if (xhr.status === 200) {
                    var successMessage = (xhr.responseJSON && xhr.responseJSON.message) || response.message || 'Đăng ký thành công!';
                    $('#msgSubmit').removeClass('hidden alert-danger').addClass('alert alert-success').text(successMessage);
                    // Reset form
                    $('#contactForm')[0].reset();
                    // Có thể chuyển hướng sau vài giây
                    setTimeout(function() {
                        window.location.href = 'verify-password.html';
                    }, 2000);
                } else {
                    // Nếu không phải status 200, lấy message từ xhr.responseJSON hoặc response hoặc dùng mặc định
                    var errorMessage = (xhr.responseJSON && xhr.responseJSON.message) || response.message || 'Đăng ký thất bại. Vui lòng thử lại!';
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
                        errorMessage = 'Xác thực thất bại. Vui lòng kiểm tra thông tin!';
                    }
                } else if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                }
                
                $('#msgSubmit').removeClass('hidden alert-success').addClass('alert alert-danger').text(errorMessage);
            }
        });
    });
    });