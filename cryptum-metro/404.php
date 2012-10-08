<?php
/**
 * The template for displaying 404 pages (Not Found).
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */

get_header(); ?>

	<div id="primary" class="site-content">
		<div id="content" role="main">

			<article id="post-0" class="post error404 not-found ">
				<header class="entry-header <?php cryptum_metro_text_class(true) ?>">
					<h1 class="entry-title"><?php _e( '404! Page not found.', 'cryptum' ); ?></h1>
				</header>

				<div class="entry-content <?php cryptum_metro_tile_class( "ui", true ) ?>">
					<p><?php _e( 'Nothing was found at this location. Perhaps the content was moved or deleted? Try searching for it:', 'cryptum' ); ?></p>
					<?php get_search_form(); ?>
					<p><?php _e( 'If it has been removed, it\'s possible it has been archived in the <a href="http://archive.org/">Wayback Machine</a> or cached by Google', 'cryptum' ); ?></p>
				</div><!-- .entry-content -->
			</article><!-- #post-0 -->

		</div><!-- #content -->
	</div><!-- #primary .site-content -->

<?php get_footer(); ?>