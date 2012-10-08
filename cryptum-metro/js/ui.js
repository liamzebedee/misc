jQuery( document ).ready(function($) {
	/* =Unsaved Submission Protection Measure
	----------------------------------------------- */
	// Don't allow us to navigate away from a page on which we're changed
    // values on any control without a warning message.
	$('input:text,input:checkbox,input:radio,textarea,select').one('change', function() {
		$(window).bind('beforeunload', function() {
			return 'Leaving this page will cause any unsaved data to be lost.'
		});
	});
	// If the user is submitting the form, it's ok to leave
	$('form').submit(function() {
		window.onbeforeunload = null;
	});
	
	/* =Comments
	----------------------------------------------- */
	// Because of toggles, when a page is loaded, the comments are hidden by default
	// When linking to a comment, we need to unhide comments first so we can scroll to it
	// IMPORTANT: This must go before Toggle Initialization
	var hash = window.location.hash;
	if (hash) {
		$('#comment-toggle').attr('visibility', '1');
		window.location.hash = hash;
	}
	
	/* =Toggle Plugin
	----------------------------------------------- */
	// Toggle Initialization
	$('.toggle').each(function() {
		visibility = $(this).attr('visibility');
		ref = $(this).attr('ref');
		if(visibility == 'hidden' || visibility == '0') {
			$(ref).hide();
		} else {
			$(ref).show();
		}
		
		toggle_refresh(this);
	});
	
	function toggle_refresh(toggle) {
		ref = $(toggle).attr('ref');
		visible = $(ref).is(':visible');
		
		if (visible) {
			// Only show the close buttons
			$(toggle).find('.open').each(function() { $(this).hide() }); 
			$(toggle).find('.close').each(function() { $(this).show() }); 
		} else {
			// Only show the open buttons
			$(toggle).find('.close').each(function() { $(this).hide() });
			$(toggle).find('.open').each(function() { $(this).show() });
		}
	}
	
	$('.toggle').click(function() {
		element = $(this).attr('ref');
		toggle = this;
		$(element).slideToggle("fast", function() { toggle_refresh(toggle); } );
	});
	
});