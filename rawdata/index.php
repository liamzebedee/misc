<?php get_header(); ?>

<div id="container">
 
    <div id="content" class="two-columns-wrapper">
		
		<div id="column1-widget-container" class="two-column-column1 widget-area">
			<?php if ( !function_exists('dynamic_sidebar') || !dynamic_sidebar('Column1') ) { ?>
			<?php } ?>
		</div>
		<div id="column2-widget-container" class="two-column-column2 widget-area">
			<?php if ( !function_exists('dynamic_sidebar') || !dynamic_sidebar('Column2') ) { ?>
			<?php } ?>
		</div>
		
		
    </div>
 
</div>

<?php get_footer(); ?>