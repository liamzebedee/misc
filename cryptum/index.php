<?php
/**
 * The main template file.
 *
 * This is the most generic template file in a WordPress theme
 * and one of the two required files for a theme (the other being style.css).
 * It is used to display a page when nothing more specific matches a query.
 * E.g., it puts together the home page when no home.php file exists.
 * Learn more: http://codex.wordpress.org/Template_Hierarchy
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */

/*
<?php $splash_image = of_get_option("splash_image_large");
if ( ! empty( $splash_image ) ): ?>
	<img src="<?php echo $splash_image; ?>" width="50" height="50" id="logo" class="img-rounded" rel="home" />
<?php endif; ?>
*/
 
get_header(); ?>

		<div id="primary" class="content-area offset1 span10">
			<div id="content" class="site-content" role="main">

			<?php if ( have_posts() ) : ?>

				<?php cryptum_content_nav( 'nav-above' ); ?>

				<?php /* Start the Loop */ ?>
				<?php while ( have_posts() ) : the_post(); ?>

					<?php
						/* Include the Post-Format-specific template for the content.
						 * If you want to overload this in a child theme then include a file
						 * called content-___.php (where ___ is the Post Format name) and that will be used instead.
						 */
						get_template_part( 'content', get_post_format() );
					?>

				<?php endwhile; ?>
				
				<?php cryptum_content_nav( 'nav-below' ); ?>

			<?php else: // !have_posts ?>
				
				<header class="entry-header">
					<h1 class="entry-title"><?php _e( 'No posts.', 'cryptum' ); ?></h1>
				</header><!-- .entry-header -->
			
			<?php endif; ?>

			</div><!-- #content .site-content -->
		</div><!-- #primary .content-area -->

<?php get_footer(); ?>