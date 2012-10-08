<?php
/**
 * The default template for displaying content
 * This should NEVER be used alone, instead a content-xxx template should be loaded
 * Learn more: http://codex.wordpress.org/Template_Hierarchy
 * 
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>

<article id="post-<?php the_ID(); ?>" <?php post_class( cryptum_metro_tile_class( cryptum_metro_get_color() ) ); ?>>
	<header class="entry-header">
		<h1 class="entry-title"><a href="<?php the_permalink(); ?>" title="<?php printf( esc_attr__( 'Permalink to %s', 'cryptum' ), the_title_attribute( 'echo=0' ) ); ?>" rel="bookmark"><?php the_title(); ?></a></h1>
	</header><!-- .entry-header -->
	
	<?php if ( is_search() ) : // Only display Excerpts for Search ?>
	<div class="entry-summary">
		<?php the_excerpt(); ?>
	</div><!-- .entry-summary -->
	<?php endif; ?>
	
	<?php if ( !is_front_page() ) : ?>
	<div class="entry-content">
		<?php if ( 'post' == get_post_type() ) : ?>
		<div class="entry-meta">
			<?php cryptum_posted_on(); ?>
		</div><!-- .entry-meta -->
		<?php endif; ?>
		
		<?php the_content( __( 'Continue reading <span class="meta-nav">&rarr;</span>', 'cryptum' ) ); ?>
		<?php wp_link_pages( array( 'before' => '<div class="page-links">' . __( 'Pages:', 'cryptum' ), 'after' => '</div>' ) ); ?>
	</div><!-- .entry-content -->
	
	<footer class="entry-meta">
		<?php if ( 'post' == get_post_type() ) : // Hide category and tag text for pages on Search ?>
			<?php
				/* translators: used between list items, there is a space after the comma */
				$categories_list = cryptum_get_categories();
				if ( $categories_list && cryptum_categorized_blog() ) :
			?>
			<span class="cat-links">
				<?php
				$categories_string = 'Posted in %1$s';
				if ( cryptum_get_theme_option("short_text") ) { $tag_string = '%1$s'; }
				printf( __( $categories_string, 'cryptum' ), $categories_list );
				?>
			</span>
			<?php endif; // End if categories ?>

			<?php
				/* translators: used between list items, there is a space after the comma */
				$tags_list = cryptum_get_tags();
				if ( $tags_list ) :
			?>
			<?php cryptum_seperator_html() ?>
			<span class="tag-links">
				<?php
				$tag_string = 'Tagged %1$s'; 
				if ( cryptum_get_theme_option("short_text") ) { $tag_string = '%1$s'; }
				printf( __( $tag_string, 'cryptum' ), $tags_list );
				?>
			</span>
			<?php endif; // End if $tags_list ?>
		<?php endif; // End if 'post' == get_post_type() ?>

		<?php if ( ! post_password_required() && ( comments_open() || '0' != get_comments_number() ) ) : ?>
		<?php cryptum_seperator_html() ?>
		<span class="comments-link"><?php comments_popup_link( __( 'Leave a comment', 'cryptum' ), __( '1 Comment', 'cryptum' ), __( '% Comments', 'cryptum' ) ); ?></span>
		<?php edit_post_link( __( 'Edit', 'cryptum' ), cryptum_get_seperator() . '<span class="edit-link">', '</span>' ); ?>

		</footer><!-- #entry-meta -->
		<?php endif; ?>

	<?php endif; // End if !is_front_page() ?>
</article><!-- #post-<?php the_ID(); ?> -->
