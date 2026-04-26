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
    
    // Delay to allow meanmenu to create mobile menu, then update login/logout
    setTimeout(function() {
        if (localStorage.getItem('access_token')) {
            var loginLinks = $('a[href="login.html"]');
            loginLinks.each(function() {
                $(this).html('<img src="img/icon/login.png" alt="">Logout');
                $(this).attr('href', '#');
                $(this).on('click', function(e) {
                    e.preventDefault();
                    localStorage.removeItem('access_token');
                    localStorage.removeItem('refresh_token');
                    localStorage.removeItem('user');
                    location.reload();
                });
            });
        } else {
            // Ensure login links are set back if no token (optional, but for consistency if token is removed elsewhere)
            var logoutLinks = $('a[href="#"]');
            logoutLinks.each(function() {
                if ($(this).text().includes('Logout')) {
                    $(this).html('<img src="img/icon/login.png" alt="">Login');
                    $(this).attr('href', 'login.html');
                    $(this).off('click'); // Remove logout handler
                }
            });
        }
    }, 100);
    
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
    

    // Check for token and update login/logout on all pages
    $(document).ready(function() {
        if (localStorage.getItem('access_token')) {
            var loginLink = $('.topbar-right a[href="login.html"]');
            if (loginLink.length) {
                loginLink.html('<img src="img/icon/login.png" alt="">Logout');
                loginLink.attr('href', '#');
                loginLink.on('click', function(e) {
                    e.preventDefault();
                    localStorage.removeItem('access_token');
                    localStorage.removeItem('refresh_token');
                    localStorage.removeItem('user');
                    location.reload();
                });
            }
        }
    });

    // Check for token and update login/logout on all pages
    $(document).ready(function() {
        if (localStorage.getItem('access_token')) {
            var loginLink = $('.topbar-right a[href="login.html"]');
            if (loginLink.length) {
                loginLink.html('<img src="img/icon/login.png" alt="">Logout');
                loginLink.attr('href', '#');
                loginLink.on('click', function(e) {
                    e.preventDefault();
                    localStorage.removeItem('access_token');
                    localStorage.removeItem('refresh_token');
                    localStorage.removeItem('user');
                    location.reload();
                });
            }
        }
    });

})(jQuery); 