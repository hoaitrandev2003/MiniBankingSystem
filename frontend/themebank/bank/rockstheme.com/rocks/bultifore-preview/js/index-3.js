$(document).ready(function() {
    if (window.Auth && Auth.getAccessToken && Auth.getAccessToken()) {
        // Find all login links: both a[href="login.html"] and links with login icon
        var loginLinks = $('a[href="login.html"], a:has(img[src="img/icon/login.png"])').filter(function() {
            var text = $(this).text().trim();
            var $img = $(this).find('img[src="img/icon/login.png"]');
            return text.toLowerCase().includes('login') || $img.length > 0;
        });
        
        loginLinks.each(function() {
            var $link = $(this);
            var isMainButton = $link.hasClass('s-menu');
            var hasIcon = $link.find('img[src="img/icon/login.png"]').length > 0;
            
            if (isMainButton) {
                // Main button - preserve icon
                var textNode = $link.contents().filter(function() {
                    return this.nodeType === 3;
                }).first();
                if (textNode.length) {
                    textNode[0].nodeValue = 'Logout';
                } else {
                    if ($link.find('img').length === 0) {
                        $link.html('<img src="img/icon/login.png" alt="">Logout');
                    } else {
                        $link.append('Logout');
                    }
                }
            } else if (hasIcon) {
                // Link with icon - update text node after img
                var textNode = $link.contents().filter(function() {
                    return this.nodeType === 3;
                }).first();
                if (textNode.length) {
                    textNode[0].nodeValue = 'Logout';
                } else {
                    $link.append('Logout');
                }
            } else {
                // Simple menu items
                $link.text('Logout');
            }
            
            $link.attr('href', '#');
            $link.off('click.auth').on('click.auth', function(e) {
                e.preventDefault();
                Auth.logout({ url: Auth.apiBaseUrl + '/logout', redirectUrl: 'login.html' });
            });
        });
    }
});
