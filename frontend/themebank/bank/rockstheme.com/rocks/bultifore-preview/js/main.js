(function ($) {
 "use strict";

/*--------------------------
preloader
---------------------------- */	
	
	$(window).on('load',function(){
		var pre_loader = $('#preloader')
	pre_loader.fadeOut('slow',function(){$(this).remove();});
	});	
    
    
/*---------------------
 TOP Menu Stick
--------------------- */
	
var windows = $(window);
var sticky = $('#sticker');

windows.on('scroll', function() {
    var scroll = windows.scrollTop();
    if (scroll < 300) {
        sticky.removeClass('stick');
    }else{
        sticky.addClass('stick');
    }
});
    
/*----------------------------
 jQuery MeanMenu
------------------------------ */
	
    var mean_menu = $('nav#dropdown');
    mean_menu.meanmenu();
    
    function setLoginAnchorText($link, text) {
        var iconHtml = '<img src="img/icon/login.png" alt="">';
        var hasIcon = $link.find('img').length > 0;
        if (!hasIcon) {
            $link.html(iconHtml + text);
            return;
        }

        var textNode = $link.contents().filter(function() {
            return this.nodeType === 3;
        }).first();

        if (textNode.length) {
            textNode[0].nodeValue = text;
        } else {
            $link.append(text);
        }
    }

    function attachLogoutHandlers() {
        if (!window.Auth || !window.Auth.getAccessToken) {
            return;
        }

        var isLoggedIn = !!window.Auth.getAccessToken();
        
        // Handle ALL login links: both a[href="login.html"] and links with login icon
        var allLoginLinks = $('a[href="login.html"], a:has(img[src="img/icon/login.png"])').filter(function() {
            var text = $(this).text().trim();
            var $img = $(this).find('img[src="img/icon/login.png"]');
            return text.toLowerCase().includes('login') || $img.length > 0;
        });

        allLoginLinks.each(function() {
            var $link = $(this);
            var isMainButton = $link.hasClass('s-menu');
            var hasIcon = $link.find('img[src="img/icon/login.png"]').length > 0;
            
            if (isLoggedIn) {
                if (isMainButton) {
                    setLoginAnchorText($link, 'Logout');
                } else if (hasIcon) {
                    // Link with icon - update the text node after img
                    var textNode = $link.contents().filter(function() {
                        return this.nodeType === 3;
                    }).first();
                    if (textNode.length) {
                        textNode[0].nodeValue = 'Logout';
                    } else {
                        $link.append('Logout');
                    }
                } else {
                    $link.text('Logout');
                }
                $link.attr('href', '#');
                $link.attr('title', 'Logout');
                $link.addClass('auth-logout');
                $link.off('click.auth').on('click.auth', function(e) {
                    e.preventDefault();
                    if (window.Auth && window.Auth.logout) {
                        window.Auth.logout({ url: window.Auth.apiBaseUrl + '/logout', redirectUrl: 'login.html' });
                    } else {
                        localStorage.removeItem('access_token');
                        localStorage.removeItem('refresh_token');
                        localStorage.removeItem('user');
                        window.location.href = 'login.html';
                    }
                });
            } else if ($link.hasClass('auth-logout')) {
                if (isMainButton) {
                    setLoginAnchorText($link, 'Login');
                } else if (hasIcon) {
                    // Link with icon - update the text node after img
                    var textNode = $link.contents().filter(function() {
                        return this.nodeType === 3;
                    }).first();
                    if (textNode.length) {
                        textNode[0].nodeValue = 'Login';
                    }
                } else {
                    $link.text('Login');
                }
                $link.attr('href', 'login.html');
                $link.attr('title', 'Login');
                $link.removeClass('auth-logout');
                $link.off('click.auth');
            }
        });
    }

    // Delay to allow meanmenu to build the mobile menu, then update login/logout links.
    setTimeout(attachLogoutHandlers, 100);

// Nice Select JS
  $('select').niceSelect();
    
/*---------------------
 wow .js
--------------------- */
    function wowAnimation(){
        new WOW({
            offset: 100,          
            mobile: true
        }).init()
    }
    wowAnimation()	
    
/*--------------------------
 scrollUp
---------------------------- */
	
	$.scrollUp({
		scrollText: '<i class="ti-angle-up"></i>',
		easingType: 'linear',
		scrollSpeed: 900,
		animation: 'fade'
	});
    
	
/*--------------------------
 collapse
---------------------------- */
	
	var panel_test = $('.panel-heading a');
	panel_test.on('click', function(){
		panel_test.removeClass('active');
		$(this).addClass('active');
	});

/*--------------------------
 MagnificPopup
---------------------------- */	
	
    $('.video-play').magnificPopup({
        type: 'iframe'
    }); 
/*--------------------------
 Parallax
---------------------------- */	
    var parallaxeffect = $(window);
    parallaxeffect.stellar({
        responsive: true,
        positionProperty: 'position',
        horizontalScrolling: false
    });

/*---------------------
 Testimonial carousel
---------------------*/
	
    var review = $('.testimonial-carousel');
    review.owlCarousel({
		loop:true,
		nav:true,
        margin:20,
		dots:false,
        navText: ["<i class='ti-angle-left'></i>","<i class='ti-angle-right'></i>"],
		autoplay:false,
		responsive:{
			0:{
				items:1
			},
			768:{
				items:2
			},
			1000:{
				items:4
			}
		}
	});
/*--------------------------
     Payments carousel
---------------------------- */
	var payment_carousel = $('.payment-carousel');
	payment_carousel.owlCarousel({
        loop:true,
        nav:false,		
        autoplay:false,
        margin:30,
        dots:false,
        responsive:{
            0:{
                items:2
            },
            700:{
                items:4
            },
            1000:{
                items:6
            }
        }
    });

/*----------------------------
    Contact form
------------------------------ */
	$("#contactForm").on("submit", function (event) {
		if (event.isDefaultPrevented()) {
			formError();
			submitMSG(false, "Did you fill in the form properly?");
		} else {
			event.preventDefault();
			submitForm();
		}
	});
	function submitForm(){
		var name = $("#name").val();
		var email = $("#email").val();
		var msg_subject = $("#msg_subject").val();
		var message = $("#message").val();


		$.ajax({
			type: "POST",
			url: "assets/contact.php",
			data: "name=" + name + "&email=" + email + "&msg_subject=" + msg_subject + "&message=" + message,
			success : function(text){
				if (text === "success"){
					formSuccess();
				} else {
					formError();
					submitMSG(false,text);
				}
			}
		});
	}

	function formSuccess(){
		$("#contactForm")[0].reset();
		submitMSG(true, "Message Submitted!")
	}

	function formError(){
		$("#contactForm").removeClass().addClass('shake animated').one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(){
			$(this).removeClass();
		});
	}

	function submitMSG(valid, msg){
		if(valid){
			var msgClasses = "h3 text-center tada animated text-success";
		} else {
			var msgClasses = "h3 text-center text-danger";
		}
		$("#msgSubmit").removeClass().addClass(msgClasses).text(msg);
	}
    

    // Check for token and update login/logout links once the DOM is ready.
    $(document).ready(function() {
        attachLogoutHandlers();
    });

})(jQuery); 