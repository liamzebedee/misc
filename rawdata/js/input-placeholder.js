var $j = jQuery.noConflict();
$j(document).ready(function() {
	if(!Modernizr.input.placeholder){
		$j("input").each(
			function(){
				if($j(this).val()=="" && $j(this).attr("placeholder")!=""){
					$j(this).val($j(this).attr("placeholder"));
					$j(this).focus(function(){
						if($j(this).val()==$j(this).attr("placeholder")) $j(this).val("");
					});
					$j(this).blur(function(){
						if($j(this).val()=="") $j(this).val($j(this).attr("placeholder"));
					});
				}
		});
	}
	
});