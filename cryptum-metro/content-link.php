<?php
/**
 * The template for displaying links
 * Links are displayed in the format:
 *  Post Title   := Link Text/Title
 *  Post Content := Link URL
 * Learn more: http://codex.wordpress.org/Template_Hierarchy & http://codex.wordpress.org/Post_Formats
 * 
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>

<?php if ( is_front_page() || is_archive() ) : ?>
<a id="post-<?php the_ID(); ?>" <?php post_class( cryptum_metro_tile_class( cryptum_metro_get_color() ) ); ?> <?php cryptum_content_link_attr() ?>>
	<?php the_title() ?>
</a><!-- #post-<?php the_ID(); ?> -->

<?php else : ?>
<article id="post-<?php the_ID(); ?>" <?php post_class( ); ?>">
	<a class="entry-content <?php cryptum_metro_tile_class( cryptum_metro_get_color(), true ) ?>" <?php cryptum_content_link_attr() ?>>
		<h1 class="entry-title"><?php the_title(); ?></h1>
	</a><!-- .entry-content -->
	
	<footer class="entry-meta <?php cryptum_metro_tile_class( cryptum_metro_get_color(), true ) ?>">
		<div class="entry-meta">
			<?php cryptum_posted_on(); ?>
		</div><!-- .entry-meta -->
		<?php wp_link_pages( array( 'before' => '<div class="page-links">' . __( 'Pages:', 'cryptum' ), 'after' => '</div>' ) ); ?>
		
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
		<?php cryptum_seperator_html(); ?><a href="<?php echo get_post_format_link( 'link' ) ?>"><?php _e('See all links', 'cryptum' ) ?></a>
	</footer><!-- .entry-meta -->
</article><!-- #post-<?php the_ID(); ?> -->

<?php endif; ?>