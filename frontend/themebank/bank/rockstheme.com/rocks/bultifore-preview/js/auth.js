(function(window, $) {
    var apiBaseUrl = 'http://localhost:8080/auth';
    var apiBaseUrlV2 = 'http://localhost:8080';
    var storageKeys = {
        accessToken: 'access_token',
        refreshToken: 'refresh_token',
        user: 'user',
        username: 'username'
    };

    function getItem(key) {
        return localStorage.getItem(storageKeys[key]);
    }

    function setItem(key, value) {
        if (value !== undefined && value !== null) {
            localStorage.setItem(storageKeys[key], value);
        }
    }

    function removeItem(key) {
        localStorage.removeItem(storageKeys[key]);
    }

    function clearSession() {
        removeItem('accessToken');
        removeItem('refreshToken');
        removeItem('user');
        removeItem('username');
    }

    function parseJwt(token) {
        if (!token) {
            return null;
        }

        try {
            var payload = token.split('.')[1];
            return JSON.parse(atob(payload));
        } catch (e) {
            console.error('Auth.parseJwt error:', e);
            return null;
        }
    }

    function parseUserFromToken(accessToken) {
        var jwt = parseJwt(accessToken);
        if (!jwt || !jwt.sub) {
            return null;
        }

        try {
            return JSON.parse(jwt.sub);
        } catch (e) {
            console.warn('Auth.parseUserFromToken failed:', e);
            return null;
        }
    }

    function saveLoginData(data, username) {
        if (!data) {
            return;
        }

        if (data.accessToken) {
            setItem('accessToken', data.accessToken);
        }

        if (data.refreshToken) {
            setItem('refreshToken', data.refreshToken);
        }

        if (username) {
            setItem('username', username);
        }

        var userObj = parseUserFromToken(data.accessToken);
        if (userObj) {
            setItem('user', JSON.stringify(userObj));
        }
    }

    function getAuthHeader() {
        var token = getItem('accessToken');
        return token ? 'Bearer ' + token : '';
    }

    function logout(opts) {
        opts = opts || {};
        var url = opts.url || apiBaseUrl + '/logout';
        var payload = {};
        var username = getItem('username');
        var refreshToken = getItem('refreshToken');

        if (username) {
            payload.username = username;
        }

        if (refreshToken) {
            payload.refreshToken = refreshToken;
        }

        return $.ajax({
            type: 'POST',
            url: url,

            contentType: 'application/json',

            headers: {
                'Authorization': getAuthHeader()
            },

            dataType: 'json',
            data: JSON.stringify(payload)
        }).always(function() {
            clearSession();
            if (opts.redirectUrl !== false) {
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
                    window.location.href = opts.redirectUrl || 'login.html';
                }, 3000);
            }
        });
    }

    function refreshAccessToken(opts) {
        opts = opts || {};
        var url = opts.url || apiBaseUrl + '/refresh-token';
        var refreshToken = getItem('refreshToken');

        if (!refreshToken) {
            return $.Deferred().reject('missing_refresh_token').promise();
        }

        return $.ajax({
            type: 'POST',
            url: url,
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({ refreshToken: refreshToken })
        }).done(function(response) {
            if (response && response.data) {
                saveLoginData(response.data);
            }
        });
    }

    window.Auth = {
        apiBaseUrl: apiBaseUrl,
        storageKeys: storageKeys,
        getAccessToken: function() {
            return getItem('accessToken');
        },
        getRefreshToken: function() {
            return getItem('refreshToken');
        },
        getUser: function() {
            var userJson = getItem('user');
            return userJson ? JSON.parse(userJson) : null;
        },
        getUsername: function() {
            return getItem('username');
        },
        clearSession: clearSession,
        parseJwt: parseJwt,
        saveLoginData: saveLoginData,
        getAuthHeader: getAuthHeader,
        logout: logout,
        refreshAccessToken: refreshAccessToken
    };
})(window, jQuery);

// Ensure a persistent deviceId for this browser
if (!localStorage.getItem('deviceId')) {
    if (window.crypto && crypto.randomUUID) {
        localStorage.setItem('deviceId', crypto.randomUUID());
    } else {
        localStorage.setItem('deviceId', 'dev-' + Date.now() + '-' + Math.random().toString(36).slice(2, 10));
    }
}

// Compatibility globals used by some pages/snippets
window.getAccessToken = function() {
    return window.Auth && window.Auth.getAccessToken ? window.Auth.getAccessToken() : null;
};

window.logout = function() {
    if (window.Auth && window.Auth.logout) {
        return window.Auth.logout({ redirectUrl: 'login.html' });
    }
    // fallback: clear and navigate
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user');
    localStorage.removeItem('username');
    window.location.href = 'login.html';
};

// Attach Authorization header to all jQuery AJAX calls
if (window.jQuery) {
    $.ajaxSetup({
        beforeSend: function(xhr) {
            var token = window.getAccessToken();
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }
        }
    });

    // Auto logout on 401 responses (session revoked/expired)
// =====================================================
// AUTO LOGOUT WHEN TOKEN REVOKED / EXPIRED
// =====================================================

$(document).ajaxError(function(event, xhr, settings) {

    // bỏ qua request logout để tránh loop vô hạn
    if (
        settings &&
        settings.url &&
        settings.url.includes('/logout')
    ) {
        return;
    }

    // chỉ xử lý 401
    if (xhr && xhr.status === 401) {

        // tránh logout nhiều lần liên tục
        if (window.__loggingOut) {
            return;
        }

        window.__loggingOut = true;

        try {

            alert(
                'Phiên đăng nhập đã hết hạn hoặc tài khoản đã đăng nhập ở thiết bị khác'
            );

        } catch (e) {}

        // gọi logout chuẩn
        if (window.Auth && window.Auth.logout) {

            window.Auth.logout({
                redirectUrl: 'login.html'
            });

        } else {

            // fallback
            localStorage.removeItem('access_token');
            localStorage.removeItem('refresh_token');
            localStorage.removeItem('user');
            localStorage.removeItem('username');

            window.location.href = 'login.html';
        }
    }
});
}
