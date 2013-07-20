<?php
/*
Template Name: Home (spiffy)
*/
/**
 * This is the default view for the homepage, containing the super slick UI
 * This UI is based on the wonderful christopermeeks.com
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
get_header();
?>

<div id="primary" class="content-area row-fluid">
	<div id="content" class="site-content" role="main">
		<div class="hero-unit">
			<h1><?php bloginfo( 'name' ); ?></h1>
			<p><? bloginfo( 'description' ) ?></p>
			
			<nav role="navigation" class="site-navigation main-navigation row-fluid">
				<h1 class="assistive-text"><?php _e( 'Menu', 'cryptum' ); ?></h1>
				<?php
				$args = array(
					'menu_class' => 'nav nav-pills',
				);
				wp_nav_menu( $args );
				?>
			</nav>
		</div>
	</div><!-- #content .site-content -->
</div><!-- #primary .content-area -->

<?php get_footer(); ?>