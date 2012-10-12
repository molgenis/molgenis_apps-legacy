//adding css styling on click 
$("ul").delegate("li", "click", function() {
	$(this).addClass("active");
	//$(this).addClass("active").siblings().removeClass("active");"
});

//adding css styling on hover 
$("li>span").hover(function(){
	$(this).addClass("highlight");
},function() {
	$(this).removeClass("highlight");
});

$(document).ready(function(){
	
	var $scrollingDiv = $("#scrollingDiv");

	$(window).scroll(function(){			
		$scrollingDiv
		.stop()
		.animate({"marginTop": ($(window).scrollTop() + 30) + "px"}, "slow" );			
	});
	
});