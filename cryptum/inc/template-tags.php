<?php
/**
 * Custom template tags for this theme.
 *
 * Eventually, some of the functionality here could be replaced by core features
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */

if ( ! function_exists( 'cryptum_content_nav' ) ):
/**
 * Display navigation to next/previous pages when applicable
 *
 * @since Cryptum 1.0
 */
function cryptum_content_nav( $nav_id ) {
	global $wp_query;

	$nav_class = 'site-navigation paging-navigation';
	if ( is_single() )
		$nav_class = 'site-navigation post-navigation';

	?>
	<nav role="navigation" id="<?php echo $nav_id; ?>" class="<?php echo $nav_class; ?>">
		<h1 class="assistive-text"><?php _e( 'Post navigation', 'cryptum' ); ?></h1>
		<ul class="pager">
		<?php if ( is_single() ) : // navigation links for single posts ?>

			<?php previous_post_link( '<li class="previous nav-previous">%link</li>', _x( '&larr;', 'Previous post link', 'cryptum' ) . '%title' ); ?>
			<?php next_post_link( '<li class="next nav-next">%link</li>', '%title' . _x( '&rarr;', 'Next post link', 'cryptum' ) ); ?>

		<?php elseif ( $wp_query->max_num_pages > 1 && ( is_home() || is_archive() || is_search() ) ) : // navigation links for home, archive, and search pages ?>

			<?php if ( get_next_posts_link() ) : ?>
			<li class="previous nav-previous"><?php next_posts_link( __( '<span class="meta-nav">&larr;</span> Older posts', 'cryptum' ) ); ?></li>
			<?php endif; ?>

			<?php if ( get_previous_posts_link() ) : ?>
			<li class="next nav-next"><?php previous_posts_link( __( 'Newer posts <span class="meta-nav">&rarr;</span>', 'cryptum' ) ); ?></li>
			<?php endif; ?>
		
		<?php endif; ?>
		</ul>
	</nav><!-- #<?php echo $nav_id; ?> -->
	<?php
}
endif; // cryptum_content_nav

if ( ! function_exists( 'cryptum_comment' ) ) :
/**
 * Template for comments and pingbacks.
 *
 * Used as a callback by wp_list_comments() for displaying the comments.
 *
 * @since Cryptum 1.0
 */
function cryptum_comment( $comment, $args, $depth ) {
	$GLOBALS['comment'] = $comment;
	switch ( $comment->comment_type ) :
		case 'pingback' :
		case 'trackback' :
	?>
	<li class="post pingback">
		<p><?php _e( 'Pingback:', 'cryptum' ); ?> <?php comment_author_link(); ?><?php edit_comment_link( __( '(Edit)', 'cryptum' ), ' ' ); ?></p>
	<?php
			break;
		default :
	?>
	<li <?php comment_class(); ?> id="li-comment-<?php comment_ID(); ?>">
		<article id="comment-<?php comment_ID(); ?>" class="comment comment-single">
			<footer>
				<div class="comment-author">
					<?php echo get_comment_author_link() ?>
				</div><!-- .comment-author -->
				<?php if ( $comment->comment_approved == '0' ) : ?>
					<em><?php _e( 'Your comment is awaiting moderation.', 'cryptum' ); ?></em>
					<br />
				<?php endif; ?>

				<small class="comment-meta commentmetadata">
					<a href="<?php echo esc_url( get_comment_link( $comment->comment_ID ) ); ?>" class="comment-time">
						<time pubdate datetime="<?php comment_time( 'c' ); ?>">
						<?php
							/* translators: 1: date, 2: time */
							printf( __( '%1$s at %2$s', 'cryptum' ), get_comment_date(), get_comment_time() ); ?>
						</time>
					</a>
					<?php edit_comment_link( __( '(Edit)', 'cryptum' ), ' ' );
					?>
				</small><!-- .comment-meta .commentmetadata .vcard -->
			</footer>

			<div class="comment-content">
				<div class="vcard"><?php echo get_avatar( $comment, 40 ); ?></div>
				<?php comment_text(); ?>
			</div>

			<div class="reply">
				<?php comment_reply_link( array_merge( $args, array( 'depth' => $depth, 'max_depth' => $args['max_depth'] ) ) ); ?>
			</div><!-- .reply -->
		</article><!-- #comment-## -->

	<?php
			break;
	endswitch;
}
endif; // ends check for cryptum_comment()

if ( ! function_exists( 'cryptum_posted_on' ) ) :
/**
 * Prints HTML with meta information for the current post-date/time and author.
 *
 * @since Cryptum 1.0
 */
function cryptum_posted_on() {
	printf( __( 'Posted on <time class="entry-date" datetime="%3$s" pubdate>%4$s</time><span class="byline"> by <span class="author vcard"><a class="url fn n" href="%5$s" title="%6$s" rel="author">%7$s</a></span></span>', 'cryptum' ),
		esc_url( get_permalink() ),
		esc_attr( get_the_time() ),
		esc_attr( get_the_date( 'c' ) ),
		esc_html( get_the_date() ),
		esc_url( get_author_posts_url( get_the_author_meta( 'ID' ) ) ),
		esc_attr( sprintf( __( 'View all posts by %s', 'cryptum' ), get_the_author() ) ),
		esc_html( get_the_author() )
	);
}
endif;

/**
 * Returns true if a blog has more than 1 category
 *
 * @since Cryptum 1.0
 */
function cryptum_categorized_blog() {
	if ( false === ( $all_the_cool_cats = get_transient( 'all_the_cool_cats' ) ) ) {
		// Create an array of all the categories that are attached to posts
		$all_the_cool_cats = get_categories( array(
			'hide_empty' => 1,
		) );

		// Count the number of categories that are attached to the posts
		$all_the_cool_cats = count( $all_the_cool_cats );

		set_transient( 'all_the_cool_cats', $all_the_cool_cats );
	}

	if ( '1' != $all_the_cool_cats ) {
		// This blog has more than 1 category so cryptum_categorized_blog should return true
		return true;
	} else {
		// This blog has only 1 category so cryptum_categorized_blog should return false
		return false;
	}
}

/**
 * Flush out the transients used in cryptum_categorized_blog
 *
 * @since Cryptum 1.0
 */
function cryptum_category_transient_flusher() {
	// Like, beat it. Dig?
	delete_transient( 'all_the_cool_cats' );
}
add_action( 'edit_category', 'cryptum_category_transient_flusher' );
add_action( 'save_post', 'cryptum_category_transient_flusher' );