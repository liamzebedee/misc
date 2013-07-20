<?php
/**
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>

<article id="post-<?php the_ID(); ?>" <?php post_class( 'span8' ); ?>>
	<header class="entry-header">
		<h1 class="entry-title"><?php the_title(); ?></h1>

		<div class="entry-meta">
			<?php echo wp_get_attachment_link( get_post_thumbnail_id($post->ID), '' , false, false, 'Thumbnail.' ); ?>
			<?php cryptum_posted_on(); ?>
		</div><!-- .entry-meta -->
	</header><!-- .entry-header -->

	<div class="entry-content">
		<?php the_content(); ?>
		<?php wp_link_pages( array( 'before' => '<div class="page-links">' . __( 'Pages:', 'cryptum' ), 'after' => '</div>' ) ); ?>
	</div><!-- .entry-content -->

	<footer class="entry-meta">
		<?php
			/* translators: used between list items, space between each category */
			$category_list = get_the_category_list( ' ' );

			/* translators: used between list items, space between each tag */
			$tag_list = get_the_tag_list( '', ' ' );

			if ( ! cryptum_categorized_blog() ) {
				// This blog only has 1 category so we just need to worry about tags in the meta text
				if ( '' != $tag_list ) {
					$meta_text = '%2$s';
				} else {
					$meta_text = '';
				}

			} else {
				// But this blog has loads of categories so we should probably display them here
				if ( '' != $tag_list ) {
					$meta_text = '%1$s %2$s';
				} else {
					$meta_text = '%1$s';
				}

			} // end check for categories on this blog
			
			printf(
				$meta_text,
				$category_list,
				$tag_list
			);
		?>
		
		<?php edit_post_link( __( 'Edit', 'cryptum' ), '<span class="edit-link">', '</span>' ); ?>
	</footer><!-- .entry-meta -->
</article><!-- #post-<?php the_ID(); ?> -->
