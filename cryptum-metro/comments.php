<?php
/**
 * The template for displaying Comments.
 *
 * The area of the page that contains both current comments
 * and the comment form. The actual display of comments is
 * handled by a callback to cryptum_comment() which is
 * located in the functions.php file.
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>

<?php
/*
 * If the current post is protected by a password and
 * the visitor has not yet entered the password we will
 * return early without loading the comments.
 */
if ( post_password_required() )
	return;
?>

<a class="<?php echo cryptum_metro_toggler_class() . ' ' . cryptum_metro_tile_class("ui"); ?>" ref="#comments" visibility="hidden"  title="Comment Toggle" id="comment-toggle">
	<span class="open"><?php _e( '<b>Show</b> Comments &crarr;', 'cryptum' ) ?></span>
	<span class="close"><?php _e( '<b>Hide</b> Comments &crarr;', 'cryptum' ) ?></span>
</a>
	
<div id="comments" class="comments-area">
	
	<?php
	$comment_form_args = array( 'title_reply' => 'Leave a comment',
								//'comment_field' => '<p class="comment-form-comment '.cryptum_metro_tile_class("ui").'"><label for="comment">' . _x( 'Comment', 'noun' ) . '</label><textarea id="comment" name="comment" cols="45" rows="8" aria-required="true"></textarea></p>',
								//'must_log_in' => '<p class="must-log-in '.cryptum_metro_tile_class ( "ui" ).'">' .  sprintf( __( 'You must be <a href="%s">logged in</a> to post a comment.' ), wp_login_url( apply_filters( 'the_permalink', get_permalink( ) ) ) ) . '</p>',
								//'logged_in_as' => '<p class="logged-in-as '.cryptum_metro_tile_class ( "ui" ).'">' . sprintf( __( 'Logged in as <a href="%1$s">%2$s</a>. <a href="%3$s" title="Log out of this account">Log out?</a>' ), admin_url( 'profile.php' ), $user_identity, wp_logout_url( apply_filters( 'the_permalink', get_permalink( ) ) ) ) . '</p>',
								'comment_notes_after' => '',
								'cryptum_form_class' => cryptum_metro_tile_class("ui"),
	);
	?>
	<?php cryptum_comment_form( $comment_form_args ); ?>
	
	<?php if ( have_comments() ) : ?>
		<h2 class="comments-title <?php cryptum_metro_text_class(true) ?>">
			<?php
				$comment_strings = array('One comment on &ldquo;%2$s&rdquo;', '%1$s comments on &ldquo;%2$s&rdquo;');
				if ( cryptum_get_theme_option("short_text") ) {
					$comment_strings[0] = 'Comments (%1$s)';
					$comment_strings[1] = 'Comments (%1$s)';
				}
				printf( _n( $comment_strings[0], $comment_strings[1], get_comments_number(), 'cryptum' ),
					number_format_i18n( get_comments_number() ), '<span>' . get_the_title() . '</span>' );
				
			?>
		</h2>

		<?php if ( get_comment_pages_count() > 1 && get_option( 'page_comments' ) ) : // are there comments to navigate through ?>
		<nav role="navigation" id="comment-nav-above" class="site-navigation comment-navigation">
			<h1 class="assistive-text"><?php _e( 'Comment navigation', 'cryptum' ); ?></h1>
			<div class="nav-previous <?php cryptum_metro_tile_class ( "ui", true ) ?>"><?php previous_comments_link( __( '&larr; Older Comments', 'cryptum' ) ); ?></div>
			<div class="nav-next <?php cryptum_metro_tile_class ( "ui", true ) ?>"><?php next_comments_link( __( 'Newer Comments &rarr;', 'cryptum' ) ); ?></div>
		</nav>
		<?php endif; // check for comment navigation ?>

		<ol class="commentlist">
			<?php
				/* Loop through and list the comments. Tell wp_list_comments()
				 * to use cryptum_comment() to format the comments.
				 * If you want to overload this in a child theme then you can
				 * define cryptum_comment() and that will be used instead.
				 * See cryptum_comment() in functions.php for more.
				 */
				wp_list_comments( array( 'callback' => 'cryptum_comment' ) );
			?>
		</ol>

		<?php if ( get_comment_pages_count() > 1 && get_option( 'page_comments' ) ) : // are there comments to navigate through ?>
		<nav role="navigation" id="comment-nav-below" class="site-navigation comment-navigation">
			<h1 class="assistive-text"><?php _e( 'Comment navigation', 'cryptum' ); ?></h1>
			<div class="nav-previous"><?php previous_comments_link( __( '&larr; Older Comments', 'cryptum' ) ); ?></div>
			<div class="nav-next"><?php next_comments_link( __( 'Newer Comments &rarr;', 'cryptum' ) ); ?></div>
		</nav>
		<?php endif; // check for comment navigation ?>

	<?php endif; // have_comments() ?>

	<?php
		// If comments are closed and there are comments, let's leave a little note, shall we?
		if ( ! comments_open() && '0' != get_comments_number() && post_type_supports( get_post_type(), 'comments' ) ) :
	?>
		<p class="nocomments"><?php _e( 'Comments are closed.', 'cryptum' ); ?></p>
	<?php endif; ?>
	
</div><!-- #comments .comments-area -->
	