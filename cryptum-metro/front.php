<?php
/**
 * The template used for displaying front post/page related stuff
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>
			<header id="masthead" class="site-header">
				<hgroup class="<?php cryptum_metro_tile_class( "ui", true ); ?>">
					<h1 class="site-title"><a href="<?php echo home_url( '/' ); ?>" title="<?php echo esc_attr( get_bloginfo( 'name', 'display' ) ); ?>" rel="home"><?php bloginfo( 'name' ); ?></a></h1>
					<h2 class="site-description"><?php bloginfo( 'description' ); ?></h2>
				</hgroup>

				<nav role="navigation" class="site-navigation main-navigation <?php cryptum_metro_tile_class( "ui", true ); ?>">
					<?php
					// Menu
					$nav_menu_args = array( 'depth' => '1',
											'theme_location' => 'primary',
					);
					wp_nav_menu( $nav_menu_args );
					?>
				</nav>
			</header><!-- #masthead .site-header -->