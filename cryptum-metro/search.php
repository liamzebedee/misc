<?php
/**
 * The template for displaying Search Results pages.
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */

get_header(); ?>

		<section id="primary" class="site-content">
			<div id="content" role="main">

			<?php if ( have_posts() ) : ?>

				<header class="page-header <?php cryptum_metro_text_class(true) ?>">
					<h1 class="page-title"><?php printf( __( 'Search Results for: %s', 'cryptum' ), '<span>' . get_search_query() . '</span>' ); ?></h1>
				</header>

				<?php cryptum_content_nav( 'nav-above' ); ?>

				<?php /* Start the Loop */ ?>
				<?php while ( have_posts() ) : the_post(); ?>

					<?php get_template_part( 'content', cryptum_get_post_format() ); ?>

				<?php endwhile; ?>

				<?php cryptum_content_nav( 'nav-below' ); ?>

			<?php else : ?>

				<?php get_template_part( 'no-results', 'search' ); ?>

			<?php endif; ?>

			</div><!-- #content -->
		</section><!-- #primary .site-content -->

<?php get_footer(); ?>