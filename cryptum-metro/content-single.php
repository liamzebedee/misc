<?php
/**
 * The template for displaying posts
 * Learn more: http://codex.wordpress.org/Template_Hierarchy
 * 
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>

<?php if ( is_front_page() || is_archive() ) : ?>

<a id="post-<?php the_ID(); ?>" <?php post_class( cryptum_metro_tile_class( cryptum_metro_get_color() ) ); ?> href="<?php the_permalink(); ?>" title="<?php printf( esc_attr__( 'Permalink to %s', 'cryptum' ), the_title_attribute( 'echo=0' ) ); ?>" rel="bookmark">
	<h1 class="entry-title"><?php the_title(); ?></h1>
</a><!-- #post-<?php the_ID(); ?> -->

<?php else: ?>

<article id="post-<?php the_ID(); ?>" <?php post_class( cryptum_metro_tile_class( cryptum_metro_get_color() ) ); ?>>
	<header class="entry-header">
		<h1 class="entry-title"><?php the_title(); ?></h1>
	</header><!-- .entry-header -->
	
	<div class="entry-content">
		<div class="entry-meta">
			<?php cryptum_posted_on(); ?>
		</div><!-- .entry-meta -->
		<?php the_content(); ?>
		<?php wp_link_pages( array( 'before' => '<div class="page-links">' . __( 'Pages:', 'cryptum' ), 'after' => '</div>' ) ); ?>
	</div><!-- .entry-content -->

	<footer class="entry-meta">
		<?php
			$category_list = cryptum_get_categories();

			$tag_list = cryptum_get_tags();

			if ( ! cryptum_categorized_blog() ) {
				// This blog only has 1 category so we just need to worry about tags in the meta text
				if ( cryptum_get_theme_option( "short_text" ) ) {
					if ( '' != $tag_list ) {
						$meta_text = __( '%2$s '.cryptum_get_seperator().' <a href="%3$s" title="Permalink to %4$s" rel="bookmark">Permalink</a>', 'cryptum' );
					} else {
						$meta_text = __( '<a href="%3$s" title="Permalink to %4$s" rel="bookmark">Permalink</a>', 'cryptum' );
					}
				} else {
					if ( '' != $tag_list ) {
						$meta_text = __( 'This entry was tagged %2$s. Bookmark the <a href="%3$s" title="Permalink to %4$s" rel="bookmark">permalink</a>.', 'cryptum' );
					} else {
						$meta_text = __( 'Bookmark the <a href="%3$s" title="Permalink to %4$s" rel="bookmark">permalink</a>.', 'cryptum' );
					}
				}
			} else {
				// But this blog has loads of categories so we should probably display them here
				if ( cryptum_get_theme_option( "short_text" ) ) {
					if ( '' != $tag_list ) {
						$meta_text = __( '%1$s '.cryptum_get_seperator().' %2$s '.cryptum_get_seperator().' <a href="%3$s" title="Permalink to %4$s" rel="bookmark">Permalink</a>', 'cryptum' );
					} else {
						$meta_text = __( '%1$s '.cryptum_get_seperator().' <a href="%3$s" title="Permalink to %4$s" rel="bookmark">Permalink</a>', 'cryptum' );
					}
				} else {
					// But this blog has loads of categories so we should probably display them here
					if ( '' != $tag_list ) {
						$meta_text = __( 'This entry was posted in %1$s and tagged %2$s. Bookmark the <a href="%3$s" title="Permalink to %4$s" rel="bookmark">permalink</a>.', 'cryptum' );
					} else {
						$meta_text = __( 'This entry was posted in %1$s. Bookmark the <a href="%3$s" title="Permalink to %4$s" rel="bookmark">permalink</a>.', 'cryptum' );
					}
				}
			} // end check for categories on this blog

			printf(
				$meta_text,
				$category_list,
				$tag_list,
				get_permalink(),
				the_title_attribute( 'echo=0' )
			);
		?>

		<?php edit_post_link( __( 'Edit', 'cryptum' ), '<span class="edit-link">'.cryptum_get_seperator().' ', '</span>' ); ?>
	</footer><!-- .entry-meta -->
	
</article><!-- #post-<?php the_ID(); ?> -->

<?php endif; ?>