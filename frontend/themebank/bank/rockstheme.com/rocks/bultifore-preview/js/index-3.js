$(document).ready(function() {
    // Check for token and update login/logout
    if (localStorage.getItem('access_token')) {
        var loginLinks = $('a[href="login.html"]');
        if (loginLinks.length) {
            loginLinks.each(function() {
                $(this).text('Logout');
                $(this).attr('href', '#');
                $(this).on('click', function(e) {
                    e.preventDefault();
                    
                    // Gọi API logout
                    var accessToken = localStorage.getItem('access_token');
                    var username = localStorage.getItem('username');
                    
                    $.ajax({
                        type: 'POST',
                        url: 'http://localhost:8080/auth/logout',
                        headers: {
                            'Authorization': 'Bearer ' + accessToken,
                            'Content-Type': 'application/json'
                        },
                        dataType: 'json',
                        data: JSON.stringify({
                            username: username
                        }),
                        success: function(response) {
                            // Chỉ xóa thông tin khi nhận được code 200
                            if (response.code === 200) {
                                localStorage.removeItem('access_token');
                                localStorage.removeItem('refresh_token');
                                localStorage.removeItem('user');
                                localStorage.removeItem('username');
                                // Redirect về trang chủ
                                location.href = 'login.html';
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
                });
            });
        }
    }
});
