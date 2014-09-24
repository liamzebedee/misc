<?php
/**
 * The template for displaying 404 pages (Not Found).
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */

get_header(); ?>

	<div id="primary" class="content-area">
		<div id="content" class="site-content" role="main">

			<article id="post-0" class="post error404 not-found">
				<header class="entry-header">
					<h1 class="entry-title"><?php _e( '404', 'cryptum' ); ?></h1>
				</header><!-- .entry-header -->

				<div class="entry-content">
					<p><?php _e( "I'm terribly sorry for this inconvenience, however nothing was found. At all.", 'cryptum' ); ?></p>
					<p><?php _e( "You may want to try searching for it using <a href='//google.com'>Google</a> or seeing if the <a href='//archive.org'>Internet Archive</a> may of archived it.", 'cryptum' ); ?></p> 
				</div><!-- .entry-content -->
			</article><!-- #post-0 .post .error404 .not-found -->

		</div><!-- #content .site-content -->
	</div><!-- #primary .content-area -->

<?php get_footer(); ?>