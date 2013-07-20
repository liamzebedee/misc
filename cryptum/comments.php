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

	<div id="comments" class="comments-area">
	
	<?php if ( have_comments() ) : ?>
		<h3 class="comments-title">Comments</h3>
		<h4 class="comments-tip">...read them below or <a href="#respond">add one</a></h4>

		<ol class="commentlist">
			<?php
				/* Loop through and list the comments. Tell wp_list_comments()
				 * to use cryptum_comment() to format the comments.
				 * If you want to overload this in a child theme then you can
				 * define cryptum_comment() and that will be used instead.
				 * See cryptum_comment() in inc/template-tags.php for more.
				 */
				wp_list_comments( array( 'callback' => 'cryptum_comment' ) );
			?>
		</ol><!-- .commentlist -->

		<?php if ( get_comment_pages_count() > 1 && get_option( 'page_comments' ) ) : // are there comments to navigate through ?>
		<nav role="navigation" id="comment-nav-below" class="site-navigation comment-navigation">
			<h1 class="assistive-text"><?php _e( 'Comment navigation', 'cryptum' ); ?></h1>
			<div class="nav-previous"><?php previous_comments_link( __( '&larr; Older Comments', 'cryptum' ) ); ?></div>
			<div class="nav-next"><?php next_comments_link( __( 'Newer Comments &rarr;', 'cryptum' ) ); ?></div>
		</nav><!-- #comment-nav-below .site-navigation .comment-navigation -->
		<?php endif; // check for comment navigation ?>

	<?php endif; // have_comments() ?>

	<?php
		// If comments are closed and there are comments, let's leave a little note, shall we?
		if ( ! comments_open() && '0' != get_comments_number() && post_type_supports( get_post_type(), 'comments' ) ) :
	?>
		<p class="nocomments"><?php _e( 'Comments are closed.', 'cryptum' ); ?></p>
	<?php endif; ?>
	
	<?php
	$comment_form_args = array( 'title_reply' => 'Leave a comment',
								'comment_field' => '<p class="comment-form-comment"><textarea id="comment" name="comment" cols="45" rows="8" aria-required="true"></textarea></p>',
								'comment_notes_after' => '',
	);
	comment_form( $comment_form_args );
	?>

</div><!-- #comments .comments-area -->
