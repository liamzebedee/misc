<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 TRANSITIONAL//EN" "HTTP://WWW.W3.ORG/TR/XHTML1/DTD/XHTML1-TRANSITIONAL.DTD">
<html xmlns="http://www.w3.org/1999/xhtml" <?php language_attributes(); ?>>
	<head profile="http://gmpg.org/xfn/11">
		<?php wp_head(); ?>
		<!-- End wp_head() -->
	    <title><?php
	        if ( is_single() ) { single_post_title(); echo " - "; bloginfo('name'); }
	        elseif ( is_home() || is_front_page() ) { bloginfo('name'); print ' | '; bloginfo('description'); rawdata_page_number(); }
	        elseif ( is_page() ) { single_post_title(''); echo " - "; bloginfo('name'); }
	        elseif ( is_404() ) { bloginfo('name'); print ' | Not Found'; }
	        elseif ( is_category() ) { echo ' [' . trim(wp_title('', false)) . "] - "; bloginfo('name'); }
	        elseif ( is_tag() ) { echo '#' . trim(wp_title('', false)) . ' - '; bloginfo('name'); }
	        elseif ( is_author() ) { echo '@' . trim(wp_title('', false)) . ' - '; bloginfo('name'); }
	        else { bloginfo('name'); wp_title(' | '); rawdata_page_number(); }
	    ?></title>
	 	<meta http-equiv="content-type" content="<?php bloginfo('html_type'); ?>; charset=<?php bloginfo('charset'); ?>" />
	</head>
	
	<body <?php body_class(); ?> >
		<div id="sidebars" class="two-column-wrapper">
			<div id="left-sidebar" class="widget-area two-column-column1 sidebar">
				<?php if ( !function_exists('dynamic_sidebar') || !dynamic_sidebar('Left-Sidebar') ) { ?>
				<?php } ?>
			</div>
			<div id="right-sidebar" class="widget-area two-column-column2 sidebar">
				<?php if ( !function_exists('dynamic_sidebar') || !dynamic_sidebar('Right-Sidebar') ) { ?>
				<?php } ?>
			</div>
		</div>
		<div id="wrapper" class="hfeed">
		    <div id="header">
		        <div id="masthead">
					<?php if ( is_home() || is_front_page() ) { ?>
						<div id="branding">
							<h1 id="blog-title"><?php echo bloginfo('title') ?></h1>
						</div>
					<?php } ?>
		        </div>
				<?php if ( is_home() || is_front_page() ) { ?>
					<div id="top-widget-container" class="widget-area">
						<?php if ( !function_exists('dynamic_sidebar') || !dynamic_sidebar('Top-Center') ) { ?>
						<?php } ?>
					</div>
				<?php } ?>
		    </div>
			<div id="main">