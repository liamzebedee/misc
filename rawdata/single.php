<?php 
wp_enqueue_script( 'comment-reply' );
get_header();
?>

<div id="container">
	<div id="content">
		<?php the_post(); ?>
		<div id="post-<?php the_ID(); ?>" <?php post_class(); ?>>
			<div class="entry-meta">
				<span class="meta-prep meta-prep-author"><?php _e( 'By ', 'rawdata' ); ?></span>
				<span class="author vcard"><a class="url fn n" href="<?php echo get_author_posts_url( $authordata->ID, $authordata->user_nicename ); ?>" title="<?php printf( __( 'View all posts by %s', 'rawdata' ), $authordata->display_name ); ?>"><?php the_author(); ?></a></span>
				<span class="meta-sep"> | </span>
				<span class="meta-prep meta-prep-entry-date"><?php _e( 'Published ', 'rawdata' ); ?></span>
				<span class="entry-date"><abbr class="published" title="<?php the_time('Y-m-d\TH:i:sO') ?>"><?php the_time( get_option( 'date_format' ) ); ?></abbr></span>
				<?php edit_post_link( __( 'Edit', 'rawdata' ), '<span class="edit-link">| ', '</span>' ) ?>
			</div>
			
			<h1 class="entry-title"><?php the_title(); ?></h1>
			<div class="entry-content"><?php the_content(); ?></div>
				<div class="entry-utility">
					<span class="entry-category cat-links">[<?php echo get_the_category_list('] ['); ?>] </span>
					<span class="entry-tags tag-links"><?php echo get_the_tag_list('#', ', #', ''); ?></span>
					<br><a href="<?php get_post_comments_feed_link(); ?>" title="<?php _e( 'RSS Feed', 'rawdata' ) ?>"><?php _e( 'RSS Feed', 'rawdata' ) ?></a>
					<?php if (('open' == $post->ping_status)) { ?>
						 | <a href="<?php bloginfo('pingback_url'); ?>" title="<?php _e( 'PingBack', 'rawdata' ) ?>">PingBack</a>
						 | <a href="<?php echo get_trackback_url(); ?>" title="<?php _e( 'TrackBack', 'rawdata' ) ?>">TrackBack</a>
					<?php } ?>
				</div>	
			<?php comments_template('', true); ?>
		</div>
				
	</div>
</div>

<?php get_footer(); ?>