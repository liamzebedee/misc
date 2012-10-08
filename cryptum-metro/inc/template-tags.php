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
function cryptum_content_nav( $nav_id, $nav_class="" ) {
	global $wp_query;
	
	$nav_class .= ' site-navigation paging-navigation';
	if ( is_single() )
		$nav_class .= ' site-navigation post-navigation';
	
	/*
	 * The below code ain't pretty, so I'll explain the general function of it.
	 * Basically, we want to get the links, and display them with custom classes. 
	 * This isn't possible with previous/next_post_link, so we instead use get_adjacent_post/some-other-custom-stuff to get the post object
	 * and go from there.
	 */
	?>
	<nav role="navigation" id="<?php echo $nav_id; ?>" class="<?php echo $nav_class; ?>">
	
	<?php if ( is_single() ) : 
	// navigation links for single posts
		$previous_post = get_adjacent_post(false, '', true);
		$next_post = get_adjacent_post(false, '', false);
		
		if ( $previous_post ) : ?>
		<a class="nav-previous <?php echo cryptum_metro_tile_class( "ui" ) ?>" href="<?php echo get_permalink($previous_post) ?>" rel="prev">
			<span class="meta-nav"><?php echo _x( '&larr;', 'Previous post link', 'cryptum' ) ?></span>
			<?php echo $previous_post->post_title ?>
		</a>
		<?php endif; ?>
		
		<?php if ( $next_post ) : ?>
		<a class="nav-next <?php echo cryptum_metro_tile_class( "ui" ) ?>" href="<?php echo get_permalink($next_post) ?>" rel="next">
			<span class="meta-nav"><?php echo _x( '&rarr;', 'Next post link', 'cryptum' ) ?></span>
			<?php echo $next_post->post_title ?>
		</a>
		<?php endif; ?>
		
	<?php elseif ( $wp_query->max_num_pages > 1 && ( is_home() || is_archive() || is_search() ) ) : 
	// navigation links for home, archive, and search pages ?>
		<?php 
			$next_posts = cryptum_get_next_posts_link_checked(0);
			$next_posts_attr = apply_filters( 'next_posts_link_attributes', '' );
			$next_posts_full = 'href="'.$next_posts.'" '.$next_posts_attr;
			$prev_posts = cryptum_get_previous_posts_link_checked(0);
			$prev_posts_attr = apply_filters( 'previous_posts_link_attributes', '' );
			$prev_posts_full = 'href="'.$prev_posts.'" '.$prev_posts_attr;
		?>
		
		<?php if ( $next_posts ) : ?>
		<a class="nav-previous <?php echo cryptum_metro_tile_class( "ui" ) ?>" <?php echo $next_posts_full ?>>
			<?php _e( 'Older posts ', 'cryptum' ) ?>
			<span class="meta-nav">&larr;</span>
		</a>
		<?php endif; ?>

		<?php if ( $prev_posts ) : ?>
		<a class="nav-next <?php echo cryptum_metro_tile_class( "ui" ) ?>" <?php echo $prev_posts_full ?>>
			<?php _e( 'Newer posts ', 'cryptum' ) ?>
			<span class="meta-nav">&rarr;</span>
		</a>
		<?php endif; ?>

	<?php endif; ?>

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
		<article id="comment-<?php comment_ID(); ?>" class="comment <? cryptum_metro_tile_class( cryptum_metro_get_color(), true) ?>">
			<header>
				<div class="comment-author vcard">
					<?php echo get_avatar( $comment, 32 ); ?>
					<?php printf( __( '%s', 'cryptum' ), sprintf( '<cite class="fn">%s</cite>'.cryptum_get_seperator(), get_comment_author_link() ) ); ?>
				</div><!-- .comment-author .vcard -->
				<?php if ( $comment->comment_approved == '0' ) : ?>
					<em><?php _e( cryptum_get_seperator().'Your comment is awaiting moderation.', 'cryptum' ); ?></em>
					<br />
				<?php endif; ?>

				<div class="comment-meta commentmetadata">
					<a href="<?php echo esc_url( get_comment_link( $comment->comment_ID ) ); ?>"><time pubdate datetime="<?php comment_time( 'c' ); ?>">
					<?php
						/* translators: 1: date, 2: time */
						printf( __( '%1$s at %2$s', 'cryptum' ), get_comment_date(), get_comment_time() ); ?>
					</time></a>
					<?php edit_comment_link( __('Edit', 'cryptum' ), cryptum_get_seperator() );
					?>
				</div><!-- .comment-meta .commentmetadata -->
			</header>

			<div class="comment-content"><?php comment_text(); ?></div>

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
	$text = __( 'Posted on <a href="%1$s" title="%2$s" rel="bookmark"><time class="entry-date" datetime="%3$s" pubdate>%4$s</time></a><span class="byline"> by <span class="author vcard"><a class="url fn n" href="%5$s" title="%6$s" rel="author">%7$s</a></span></span>', 'cryptum' );
	if ( cryptum_get_theme_option("short_text") ) { $text = __( '<a href="%1$s" title="%2$s" rel="bookmark"><time class="entry-date" datetime="%3$s" pubdate>%4$s</time></a><span class="byline"> &minus; <span class="author vcard"><a class="url fn n" href="%5$s" title="%6$s" rel="author">@%7$s</a></span></span>', 'cryptum' ); }
	printf( $text,
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

if ( ! function_exists( 'cryptum_comment_form' ) ) :
/**
 * Custom comment form for Cryptum so we can style our form
 *
 * @since Cryptum 1.0
 */
function cryptum_comment_form( $args = array(), $post_id = null ) {
	global $id;

	if ( null === $post_id )
		$post_id = $id;
	else
		$id = $post_id;

	$commenter = wp_get_current_commenter();
	$user = wp_get_current_user();
	$user_identity = $user->exists() ? $user->display_name : '';

	$req = get_option( 'require_name_email' );
	$aria_req = ( $req ? " aria-required='true'" : '' );
	$fields =  array(
		'author' => '<p class="comment-form-author">' . '<label for="author">' . __( 'Name' ) . '</label> ' . ( $req ? '<span class="required">*</span>' : '' ) .
		            '<input id="author" name="author" type="text" value="' . esc_attr( $commenter['comment_author'] ) . '" size="30"' . $aria_req . ' /></p>',
		'email'  => '<p class="comment-form-email"><label for="email">' . __( 'Email' ) . '</label> ' . ( $req ? '<span class="required">*</span>' : '' ) .
		            '<input id="email" name="email" type="text" value="' . esc_attr(  $commenter['comment_author_email'] ) . '" size="30"' . $aria_req . ' /></p>',
		'url'    => '<p class="comment-form-url"><label for="url">' . __( 'Website' ) . '</label>' .
		            '<input id="url" name="url" type="text" value="' . esc_attr( $commenter['comment_author_url'] ) . '" size="30" /></p>',
	);

	$required_text = sprintf( ' ' . __('Required fields are marked %s'), '<span class="required">*</span>' );
	$defaults = array(
		'fields'               => apply_filters( 'comment_form_default_fields', $fields ),
		'comment_field'        => '<p class="comment-form-comment"><label for="comment">' . _x( 'Comment '.cryptum_get_seperator(), 'noun' ) . '</label><textarea id="comment" name="comment" cols="45" rows="8" aria-required="true"></textarea></p>',
		'must_log_in'          => '<p class="must-log-in">' . sprintf( __( 'You must be <a href="%s">logged in</a> to post a comment.' ), wp_login_url( apply_filters( 'the_permalink', get_permalink( $post_id ) ) ) ) . '</p>',
		'logged_in_as'         => '<p class="logged-in-as">' . sprintf( __( 'Logged in as <a href="%1$s">%2$s</a>. <a href="%3$s" title="Log out of this account">Log out?</a>' ), admin_url( 'profile.php' ), $user_identity, wp_logout_url( apply_filters( 'the_permalink', get_permalink( $post_id ) ) ) ) . '</p>',
		'comment_notes_before' => '<p class="comment-notes">' . __( 'Your email address will not be published.' ) . ( $req ? $required_text : '' ) . '</p>',
		'comment_notes_after'  => '<p class="form-allowed-tags">' . sprintf( __( 'You may use these <abbr title="HyperText Markup Language">HTML</abbr> tags and attributes: %s' ), ' <code>' . allowed_tags() . '</code>' ) . '</p>',
		'id_form'              => 'commentform',
		'id_submit'            => 'submit',
		'title_reply'          => __( 'Leave a Reply' ),
		'title_reply_to'       => __( 'Leave a Reply to %s' ),
		'cancel_reply_link'    => __( 'Cancel reply' ),
		'label_submit'         => __( 'Post Comment' ),
	);

	$args = wp_parse_args( $args, apply_filters( 'comment_form_defaults', $defaults ) );

	?>
		<?php if ( comments_open( $post_id ) ) : ?>
			<?php do_action( 'comment_form_before' ); ?>
			<div id="respond" class="<?php echo $args['cryptum_form_class'] ?>">
				<h3 id="reply-title"><?php comment_form_title( $args['title_reply'], $args['title_reply_to'] ); ?> <small><?php cancel_comment_reply_link( $args['cancel_reply_link'] ); ?></small></h3>
				<?php if ( get_option( 'comment_registration' ) && !is_user_logged_in() ) : ?>
					<?php echo $args['must_log_in']; ?>
					<?php do_action( 'comment_form_must_log_in_after' ); ?>
				<?php else : ?>
					<form action="<?php echo site_url( '/wp-comments-post.php' ); ?>" method="post" id="<?php echo esc_attr( $args['id_form'] ); ?>">
						<?php do_action( 'comment_form_top' ); ?>
						<?php if ( is_user_logged_in() ) : ?>
							<?php echo apply_filters( 'comment_form_logged_in', $args['logged_in_as'], $commenter, $user_identity ); ?>
							<?php do_action( 'comment_form_logged_in_after', $commenter, $user_identity ); ?>
						<?php else : ?>
							<?php echo $args['comment_notes_before']; ?>
							<?php
							do_action( 'comment_form_before_fields' );
							foreach ( (array) $args['fields'] as $name => $field ) {
								echo apply_filters( "comment_form_field_{$name}", $field ) . "\n";
							}
							do_action( 'comment_form_after_fields' );
							?>
						<?php endif; ?>
						<?php echo apply_filters( 'comment_form_field_comment', $args['comment_field'] ); ?>
						<?php echo $args['comment_notes_after']; ?>
						<p class="form-submit">
							<input name="submit" type="submit" id="<?php echo esc_attr( $args['id_submit'] ); ?>" value="<?php echo esc_attr( $args['label_submit'] ); ?>" />
							<?php comment_id_fields( $post_id ); ?>
						</p>
						<?php do_action( 'comment_form', $post_id ); ?>
					</form>
				<?php endif; ?>
			</div><!-- #respond -->
			<?php do_action( 'comment_form_after' ); ?>
		<?php else : ?>
			<?php do_action( 'comment_form_comments_closed' ); ?>
		<?php endif; ?>
	<?php
}
endif;

if ( ! function_exists( 'cryptum_get_tags' ) ) :
/**
 * Get the formatted list of tags
 *
 * @since Cryptum 1.0
 */
function cryptum_get_tags() {
	$tag_list = get_the_tag_list( '', ', ' );
	if ( cryptum_get_theme_option( "short_text" ) ) { $tag_list = get_the_tag_list('#', ' #', ''); }
	return $tag_list;
}
endif;

if ( ! function_exists( 'cryptum_get_categories' ) ) :
/**
 * Get the formatted list of categories
 *
 * @since Cryptum 1.0
 */
function cryptum_get_categories() {
	$category_list = get_the_category_list( __( ', ', 'cryptum' ) );
	if ( cryptum_get_theme_option( "short_text" ) ) { $category_list = '[' . get_the_category_list ( '] [' ) . ']'; }
	return $category_list;
}
endif;