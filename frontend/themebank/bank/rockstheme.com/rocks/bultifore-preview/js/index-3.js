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
                    
                    $.ajax({
                        type: 'POST',
                        url: 'http://localhost:8080/auth/logout',
                        headers: {
                            'Authorization': 'Bearer ' + accessToken,
                            'Content-Type': 'application/json'
                        },
                        dataType: 'json',
                        data: JSON.stringify({
                            refreshToken: localStorage.getItem('refresh_token')
                        }),
                        complete: function() {
                            localStorage.removeItem('access_token');
                            localStorage.removeItem('refresh_token');
                            localStorage.removeItem('user');
                            localStorage.removeItem('username');

                            location.href = 'login.html';
                        }
                    });
                });
            });
        }
    }
});
