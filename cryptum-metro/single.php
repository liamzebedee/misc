<?php
/**
 * The Template for displaying all single posts.
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */

get_header();
?>

		<div id="primary" class="site-content">
			<div id="content" role="main">

			<?php while ( have_posts() ) : the_post(); ?>
				
				<?php
				$post_format = get_post_format();
				if ($post_format) get_template_part( 'content', $post_format );
				else get_template_part( 'content', 'single' );
				?>
				
				<?php if ( !cryptum_has_post_format_any('quote', 'link') ) : ?>
				<?php
					// If comments are open or we have at least one comment, load up the comment template
					if ( comments_open() || '0' != get_comments_number() )
						comments_template( '', true );
				?>
				<?php endif; ?>
				
				<?php cryptum_content_nav( 'nav-below' ); ?>
				
			<?php endwhile; // end of the loop. ?>

			</div><!-- #content -->
		</div><!-- #primary .site-content -->

<?php get_footer(); ?>