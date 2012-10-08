<?php
/**
 * The template part for displaying a message that posts cannot be found.
 *
 * Learn more: http://codex.wordpress.org/Template_Hierarchy
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>

<article id="post-0" class="post no-results not-found">
	<header class="entry-header <?php cryptum_metro_text_class(true) ?>">
		<h1 class="entry-title"><?php _e( 'Nothing Found', 'cryptum' ); ?></h1>
	</header><!-- .entry-header -->

	<div class="entry-content <?php echo cryptum_metro_tile_class("ui") ?>">
		<?php if ( is_home() ) { ?>

			<p><?php printf( __( 'Ready to publish your first post? <a href="%1$s">Get started here</a>.', 'cryptum' ), admin_url( 'post-new.php' ) ); ?></p>

		<?php } elseif ( is_search() ) { ?>

			<p><?php _e( 'Sorry, but nothing matched your search terms. Try again with some different keywords or maybe use a more advanced search engine like <a href="http://google.com">Google</a>.', 'cryptum' ); ?></p>
			<?php get_search_form(); ?>

		<?php } else { ?>

			<p><?php _e( 'It seems we can&rsquo;t find what you&rsquo;re looking for. Perhaps searching can help.', 'cryptum' ); ?></p>
			<?php get_search_form(); ?>

		<?php } ?>
	</div><!-- .entry-content -->
</article><!-- #post-0 -->
