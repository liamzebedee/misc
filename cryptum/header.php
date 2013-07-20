<?php
/**
 * The Header for our theme.
 *
 * Displays all of the <head> section and everything up till <div id="main">
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
?><!DOCTYPE html>
<html <?php language_attributes(); ?>>
<!--
Each iteration, Cryptum becomes more minimalistic, until it's final form, when there are no words, only pure emotion.
~ Liam (21/10/2012 - The day of Bootstrap)
-->
<head>
<meta charset="<?php bloginfo( 'charset' ); ?>" />
<meta name="viewport" content="width=device-width" />
<title><?php
	/*
	 * Print the <title> tag based on what is being viewed.
	 */
	global $page, $paged;
	
	if ( is_front_page() ) {
		// {blog} - {description}
		bloginfo( 'name' );
		echo ' - ';
		bloginfo( 'description' );
		
	} else if ( is_home() ) {
		// {title} | {blog}
		wp_title( '|', true, 'right' );
		bloginfo( 'name' );
		
	} else if ( is_page() ) {
		// {title} | {blog}
		wp_title( '', true, 'right' );
		
	} else if ( is_single() ) {
		// {title}
		wp_title( '', true );
		
	} else if ( is_category() ) {
		// Category Archives: {category} | {blog}
		_e( 'Category Archives: ', 'cryptum' );
		wp_title( '|', true, 'right' );
		bloginfo( 'name' );
		
	} else if ( is_tag() ) {
		// Tag archives: {tag} | {blog}
		_e( 'Tag Archives: ', 'cryptum' );
		wp_title( '|', true, 'right' );
		bloginfo( 'name' );
		
	} else if ( is_year() ) {
		// Archives for {year} | {blog}
		// SEO Plugin does this
		
	} else if ( is_month() ) {
		// Archives for {month} {year} | {blog}
		_e( 'Archives for ', 'cryptum' );
		// SEO Plugin does this
		
	} else if ( is_day() ) {
		// Archives for {month} {day}, {year} | {blog}
		_e( 'Archives for ', 'cryptum' );
		// SEO Plugin does this
		
	} else if ( is_404() ) {
		// 404 Not Found | {blog}
		_e( '404 Not Found', 'cryptum' );
		echo ' | ';
		bloginfo( 'name' );
		
	}
	
	if ( is_paged() ) { // NOTE: This refers to an archive or the main page being split up over several pages
		// {title} - Page {num}
		// Add a page number if necessary:
		echo ' - ' . sprintf( __( 'Page %s', 'cryptum' ), max( $paged, $page ) );
	}
	?></title>
<link rel="profile" href="http://gmpg.org/xfn/11" />
<link rel="pingback" href="<?php bloginfo( 'pingback_url' ); ?>" />
<!--[if lt IE 9]>
<script src="<?php echo get_template_directory_uri(); ?>/js/html5.js" type="text/javascript"></script>
<![endif]-->

<?php wp_head(); ?>
</head>

<body <?php body_class(); ?>>
<div id="page" class="hfeed site">
	<?php do_action( 'before' ); ?>
	<?php if ( !cryptum_is_static_front_page() ) : ?>
		<header id="masthead" class="site-header row-fluid" role="banner">
			<nav role="navigation" class="site-navigation main-navigation offset1 span10">
				<div class="span4" href="<?php echo home_url( '/' ); ?>" rel="home">
					<h1 class="site-info">
						<a href="<?php echo home_url( '/' ); ?>" rel="home" class="site-title"><?php bloginfo( 'name' ); ?></a>
						<small class="site-description"><? bloginfo( 'description' ) ?></small>
					</h1>
				</div>
				<?php wp_nav_menu( array( 'theme_location' => 'primary', 'container_class' => 'span8' ) ); ?>
			</nav>
			
		</header>
	<?php endif; // cryptum_is_static_front_page ?>
	
	<div id="main" class="site-main row-fluid">