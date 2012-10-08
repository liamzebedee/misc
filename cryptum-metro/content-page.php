<?php
/**
 * The template used for displaying page content in page.php
 * Learn more: http://codex.wordpress.org/Template_Hierarchy
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>

<?php if ( is_front_page() || is_archive() ) : ?>

<a id="post-<?php the_ID(); ?>" <?php post_class( cryptum_metro_tile_class( cryptum_metro_get_color_current() ) ); ?> href="<?php the_permalink(); ?>" title="<?php printf( esc_attr__( 'Permalink to %s', 'cryptum' ), the_title_attribute( 'echo=0' ) ); ?>" rel="bookmark">
	<h1 class="entry-title"><?php the_title(); ?></h1>
</article><!-- #post-<?php the_ID(); ?> -->

<?php else: ?>

<article id="post-<?php the_ID(); ?>" <?php post_class( cryptum_metro_tile_class( cryptum_metro_get_color_current() ) ); ?>>
	<header class="entry-header">
		<h1 class="entry-title"><?php the_title(); ?></h1>
	</header><!-- .entry-header -->

	<div class="entry-content">
		<?php the_content(); ?>
		<?php wp_link_pages( array( 'before' => '<div class="page-links">' . __( 'Pages:', 'cryptum' ), 'after' => '</div>' ) ); ?>
		<?php edit_post_link( __( 'Edit', 'cryptum' ), '<span class="edit-link">', '</span>' ); ?>
	</div><!-- .entry-content -->
</article><!-- #post-<?php the_ID(); ?> -->

<?php endif; ?>