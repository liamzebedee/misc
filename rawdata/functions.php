<?php
/* rawdata - Theme Options */
require_once ( get_stylesheet_directory() . '/theme-options.php' );

/* rawdata - Theme Support */
add_theme_support( 'automatic-feed-links' );
add_theme_support( 'custom-header', 
	array(
		'default-image'          => get_template_directory_uri() . '/images/header.png',
		'width'                  => 900,
		'height'				 => 200,
		'flex-height'            => true,
		'header-text'            => true,
		'uploads'                => true,
		'wp-head-callback'       => 'rawdata_header_style',
	)
);

/* Add some automatic functions to improve SEO */
add_action( 'wp_head', 'feed_links_extra', 3 );             // Display the links to the extra feeds such as category feeds
add_action( 'wp_head', 'feed_links', 2 );                   // Display the links to the general feeds: Post and Comment Feed
add_action( 'wp_head', 'rsd_link' );                        // Display the link to the Really Simple Discovery service endpoint, EditURI link
add_action( 'wp_head', 'wlwmanifest_link' );                // Display the link to the Windows Live Writer manifest file.
add_action( 'wp_head', 'adjacent_posts_rel_link', 10, 0 );  // Display relational links for the posts adjacent to the current post.
add_action( 'wp_head', 'wp_generator' );                    // Display the XHTML generator that is generated on the wp_head hook, WP version
add_action( 'wp_head', 'rawdata_pingback_link' );			// Display PingBack
add_action( 'wp_head', 'rawdata_trackback_link' );			// Display TrackBack

/* rawdata - Stylesheets */
add_action( 'wp_enqueue_scripts', 'rawdata_include_stylesheet' );

/* rawdata - Sidebars */
if (function_exists('register_sidebar')) {
	register_sidebar(
		array(
			'name' => 'Left Sidebar',
			'before_widget' => '<li id="%1$s" class="widget %2$s">',
			'after_widget' => '</li>',
			'before_title' => '<h2 class="widgettitle">',
			'after_title' => '</h2>',)
	);
	
	register_sidebar(
		array(
			'name' => 'Right Sidebar',
			'before_widget' => '<li id="%1$s" class="widget %2$s">',
			'after_widget' => '</li>',
			'before_title' => '<h2 class="widgettitle">',
			'after_title' => '</h2>',)
	);
	
	register_sidebar(
		array(
			'name' => 'Top-Center',
			'before_widget' => '<li id="%1$s" class="widget %2$s">',
			'after_widget' => '</li>',
			'before_title' => '<h2 class="widgettitle">',
			'after_title' => '</h2>',)
		);

	register_sidebar(
		array(
			'name' => 'Column1',
			'before_widget' => '<li id="%1$s" class="widget %2$s">',
			'after_widget' => '</li>',
			'before_title' => '<h2 class="widgettitle">',
			'after_title' => '</h2>',)
		);
		
	register_sidebar(
		array(
			'name' => 'Column2',
			'before_widget' => '<li id="%1$s" class="widget %2$s">',
			'after_widget' => '</li>',
			'before_title' => '<h2 class="widgettitle">',
			'after_title' => '</h2>',)
		);
}


/* rawdata - u18l */
load_theme_textdomain('rawdata', TEMPLATEPATH . '/languages'); // Make theme available for translation
 
$locale = get_locale();
$locale_file = TEMPLATEPATH . "/languages/$locale.php"; // Translations can be filed in the /languages/ directory
if ( is_readable($locale_file) )
    require_once($locale_file);

/* rawdata - Functions */



// Includes default stylesheet
function rawdata_include_stylesheet() {
	wp_enqueue_style( 'rawdata-style', get_theme_root_uri().'/'.get_template().'/style.css', false, '1.0', 'all'  );
}

// Print the page number if we are pageinated
function rawdata_page_number() {
    if ( get_query_var('paged') ) {
        print ' | ' . __( 'Page ' , 'rawdata') . get_query_var('paged');
    }
}

// Print the pingback link
function rawdata_pingback_link() {
 	?><link rel="pingback" href="<?php bloginfo('pingback_url'); ?>" /><?php
}

// Print the trackback link, if applicable
function rawdata_trackback_link() {
	if( is_single() ) ?>
		<link rel="trackback" href="<?php echo get_trackback_url(); ?>" /><?php
}

// If enabled in options, displays the copyright notice
function rawdata_copyright_notice() {
	$custom = ""; // Replace this with your own custom copyright notice
	if($custom != null) {
		echo $custom;
		return;
	}
	?><span id="copyright">Copyright &copy; <?php $the_year = date("Y"); echo $the_year; ?> <a href="<?php bloginfo('url'); ?>" title="<?php bloginfo('url'); ?>"><?php bloginfo('url'); ?></a> All Rights Reserved.</span><?php
}

// If enabled in options, displays the credits notice
function rawdata_credits_notice() {
	$custom = ""; // Replace this with your own custom copyright notice
	if($custom != null) {
		echo $custom;
		return;
	}
	$rawdata_themedata = rawdata_get_theme_data();
	?>
	<div id="credits">
		<a href="http://wordpress.org/" title="WordPress">WordPress <?php bloginfo('version') ?> </a>
		<span> &middot </span>
		<a href="<?php echo $rawdata_themedata->get_template_directory_uri() ?>" title="<?php echo (string) $rawdata_themedata ?> WordPress Theme"><?php echo (string) $rawdata_themedata ?></a>
	</div><?php 
}

/* rawdata - Custom comments callback */
function rawdata_comment($comment, $args, $depth) {
	$GLOBALS['comment'] = $comment; ?>
	<li <?php comment_class(); ?> id="li-comment-<?php comment_ID() ?>">
	<div id="comment-<?php comment_ID(); ?>">
		<div class="comment-author vcard">
			<?php echo get_avatar( $comment->comment_author_email, 16 ); ?>
			<?php printf(__('<cite class="fn">%s</cite>'), get_comment_author_link()) ?>
			 | <a class="comment-meta commentmetadata" href="<?php echo esc_url( get_comment_link( $comment->comment_ID ) ) ?>"><?php printf(__('%1$s'), get_comment_date()) ?></a>
			<div class="reply" style="display: inline-block;"> | 
				<?php
					echo comment_reply_link(array_merge( $args, array('depth' => $depth, 'max_depth' => $args['max_depth']))) .
						edit_comment_link(__(' Edit'),'  ','');
				?>
			</div>
		</div>
		<?php if ($comment->comment_approved == '0') : ?>
			<em><?php _e( 'Your comment is awaiting moderation.', 'rawdata' ) ?></em>
			<br />
		<?php endif; ?>

		<span><?php echo get_comment_text() ?></span>	
	</div>
<?php
}

/* rawdata - Custom pings/trackbacks callback */
function rawdata_pings($comment, $args, $depth) {
	$GLOBALS['comment'] = $comment;
	?>
	<li id="comment-<?php comment_ID() ?>" <?php comment_class() ?>>
		<div class="comment-author">
			<?
				echo get_comment_author_link() . " | " . get_comment_date(); 
				edit_comment_link('Edit', '<span> | ', '</span>'); 
			?>
		</div>
    <?php
	if ($comment->comment_approved == '0') _e( '\t\t\t\t\t<span class="unapproved">Your trackback is awaiting moderation.</span>\n', 'rawdata' ); 
}

function rawdata_get_theme_data() {
	return wp_get_theme( get_theme_root() . '/' . "rawdata" . '/style.css' );
}

function rawdata_header_style() {
	if( is_home() || is_front_page() ) {
		if(!get_header_image()) return
		?><style type="text/css">
        	#masthead {
            	background: url(<?php header_image(); ?>);
            	height: <?php echo get_custom_header()->height ?>px;
        	}
    	</style><?php
	}
}

?>