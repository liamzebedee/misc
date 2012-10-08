<?php
function rawdata_load_scripts() {
	if (!is_admin()) {
		wp_enqueue_script('jquery');
		
		// Provides HTML5 placeholder-like functionality to browsers that don't support it 
		wp_enqueue_script(
			'input-placeholder',
			get_template_directory_uri() . '/js/input-placeholder.js',
			array('jquery', 'modernizr')
		);
		
		wp_enqueue_script(
			'modernizr',
			get_template_directory_uri() . '/js/modernizr.js'
		);
	}
}

add_action('wp_enqueue_scripts', 'rawdata_load_scripts');
?>

<?php
// Do not delete these lines
if ( !empty($_SERVER['SCRIPT_FILENAME']) && 'comments.php' == basename($_SERVER['SCRIPT_FILENAME']) )
	die ('Please do not load this page directly. Thanks!');

if ( post_password_required() ) { ?>
	<p class="nocomments"><?php _e( 'This post is password protected. Enter the password to view comments.', 'rawdata' ); ?></p><?php
	return;
}

/* Count the number of comments and trackbacks/pings */
$ping_count = $comment_count = 0;
foreach ( $comments as $comment )
	get_comment_type() == "comment" ? $comment_count++ : $ping_count++;
?>

<div id="comments" class="two-column-wrapper">
	<div id="textComments" class="two-column-column1">
		<h3><?php printf(__('Comments (%u)', 'rawdata'), $comment_count) ?></h3>
		<?php if ( have_comments() ) : ?>
			<?php echo paginate_comments_links() ?>

			<ul id="commentsList">
				<?php wp_list_comments('type=comment&callback=rawdata_comment'); ?>
			</ul>

		<?php else : // No comments so far... ?>

			<?php if ( comments_open() ) :
				// If comments are open, but there are no comments.
			?>
			
			<?php else :
				// Comments are closed
				?>
				<p class="nocomments"><?php _e( 'Comments are closed.', 'rawdata' ); ?></p>
				
			<?php endif; ?>
		<?php endif; ?>
	</div>
	
	<div id="pingComments" class="two-column-column2">
		<h3><?php printf(__('Pings/Trackbacks (%u)', 'rawdata'), $ping_count) ?></h3>
		<?php echo wp_list_comments('type=pings&callback=rawdata_pings'); ?>
	</div>
</div>

<?php if ( comments_open() ) : ?>
	<div id="respond">

		<h3><?php comment_form_title( __('Submit Comment'), __('Comment on a comment (comment-ception?) to %s' ) ); ?></h3>

		<div id="cancel-comment-reply">
			<small><?php cancel_comment_reply_link() ?></small>
		</div>

		<?php if ( get_option('comment_registration') && !is_user_logged_in() ) : ?>
			<p><?php printf(__('You must be <a href="%s">logged in</a> to comment.'), wp_login_url( get_permalink() )); ?></p>
		<?php else : ?>
			<form action="<?php echo get_option('siteurl'); ?>/wp-comments-post.php" method="post" id="commentform">
	
			<?php if ( is_user_logged_in() ) : ?>
				<p><?php printf(__('Logged in as <a href="%1$s">%2$s</a>.'), get_option('siteurl') . '/wp-admin/profile.php', $user_identity); ?> <a href="<?php echo wp_logout_url(get_permalink()); ?>" title="<?php esc_attr_e( 'Log out of this account', 'rawdata' ); ?>"><?php _e( 'Log out &raquo;', 'rawdata' ); ?></a></p>
			<?php else : ?>
				<p><input type="text" name="author" id="author" placeholder="<?php _e('Name', 'rawdata') ?>" size="22" tabindex="1" <?php if ($req) echo "aria-required='true'"; ?> /></p>
	
				<p><input type="text" name="email" id="email" placeholder="<?php _e('Mail (will not be published)', 'rawdata') ?>" size="22" tabindex="2" <?php if ($req) echo "aria-required='true'"; ?> /></p>
	
				<p><input type="text" name="url" id="url" placeholder="<?php _e( 'Website', 'rawdata' ); ?>" size="22" tabindex="3" />
			<?php endif; ?>

		<p><small><?php printf(__('<strong>XHTML:</strong> You can use these tags: <code>%s</code>'), allowed_tags()); ?></small></p>

		<p><textarea name="comment" id="comment" cols="58" rows="10" tabindex="4"></textarea></p>

		<p><input name="submit" type="submit" id="submit" tabindex="5" value="<?php esc_attr_e( 'Submit Comment', 'rawdata' ); ?>" />
			<?php comment_id_fields(); ?>
		</p>
		<?php do_action('comment_form', $post->ID); ?>
		
		</form>
		
		<?php endif; // If registration required and not logged in ?>
	</div>

<?php endif; // If you delete this the sky will fall on your head ?>
