<?php
/*
TODO:
	- $content_width
	- [cat] not [ cat ]
*/

/**
 * Cryptum functions and definitions
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */

/**
 * Set the content width based on the theme's design and stylesheet.
 *
 * @since Cryptum 1.0
 */
if ( ! isset( $content_width ) )
	$content_width = 640; /* pixels */

if ( ! function_exists( 'cryptum_setup' ) ):
/**
 * Sets up theme defaults and registers support for various WordPress features.
 *
 * Note that this function is hooked into the after_setup_theme hook, which runs
 * before the init hook. The init hook is too late for some features, such as indicating
 * support post thumbnails.
 *
 * @since Cryptum 1.0
 */
function cryptum_setup() {

	/**
	 * Custom template tags for this theme.
	 */
	require( get_template_directory() . '/inc/template-tags.php' );

	/**
	 * Custom functions that act independently of the theme templates
	 */
	require( get_template_directory() . '/inc/tweaks.php' );

	/**
	 * Custom Theme Options
	 */
	require( get_template_directory() . '/inc/theme-options/theme-options.php' );

	/**
	 * WordPress.com-specific functions and definitions
	 */
	require( get_template_directory() . '/inc/wpcom.php' );

	/**
	 * Make theme available for translation
	 * Translations can be filed in the /languages/ directory
	 * If you're building a theme based on Cryptum, use a find and replace
	 * to change 'cryptum' to the name of your theme in all the template files
	 */
	load_theme_textdomain( 'cryptum', get_template_directory() . '/languages' );

	/**
	 * Add default posts and comments RSS feed links to head
	 */
	add_theme_support( 'automatic-feed-links' );
	
	/**
	 * Implement additional post formats
	 */
	require( get_template_directory() . '/inc/post-formats.php' );

	/**
	 * This theme uses wp_nav_menu() in one location.
	 */
	register_nav_menus( array(
		'primary' => __( 'Primary Menu', 'cryptum' ),
	) );

}
endif; // cryptum_setup
add_action( 'after_setup_theme', 'cryptum_setup' );

/**
 * Register widgetized area and update sidebar with default widgets
 *
 * @since Cryptum 1.0
 */
function cryptum_widgets_init() {
	register_sidebar( array(
		'name' => __( 'Sidebar', 'cryptum' ),
		'id' => 'sidebar-1',
		'before_widget' => '<aside id="%1$s" class="widget %2$s">',
		'after_widget' => "</aside>",
		'before_title' => '<h1 class="widget-title">',
		'after_title' => '</h1>',
	) );
}
add_action( 'widgets_init', 'cryptum_widgets_init' );

/**
 * Enqueue scripts and styles
 */
function cryptum_scripts() {
	global $post;

	wp_enqueue_style( 'style', get_stylesheet_uri() );
	
	wp_enqueue_script( 'ui', get_template_directory_uri() . '/js/ui.js', array( 'jquery' ), '1', false);
	
	wp_enqueue_script( 'small-menu', get_template_directory_uri() . '/js/small-menu.js', array( 'jquery' ), '20120206', true );

	if ( is_singular() && comments_open() && get_option( 'thread_comments' ) ) {
		wp_enqueue_script( 'comment-reply' );
	}

	if ( is_singular() && wp_attachment_is_image( $post->ID ) ) {
		wp_enqueue_script( 'keyboard-image-navigation', get_template_directory_uri() . '/js/keyboard-image-navigation.js', array( 'jquery' ), '20120202' );
	}
}
add_action( 'wp_enqueue_scripts', 'cryptum_scripts' );

/**
 * Print the global seperator
 *
 * @since Cryptum 1.0
 */
function cryptum_seperator_html() {
?><span class="sep"> &minus; </span><?php
}

/**
 * Get the global seperator
 *
 * @since Cryptum 1.0
 */
function cryptum_get_seperator() {
	return '<span class="sep"> &minus; </span>';
}

/**
 * Return the classes for a tile with a specific background
 *
 * @since Cryptum 1.0
 */
function cryptum_metro_tile_class($background, $echo = false) {
	$class = 'tile ' . $background .' smallMargin';
	if ( $echo ) echo $class;
	else return $class;
}

/**
 * Return the classes for a toggler control
 *
 * A toggler is a special class made for this theme used for toggling the visibility of elements. 
 */
function cryptum_metro_toggler_class( $echo = false ) {
	$class = 'toggle';
	if ( $echo ) echo $class;
	else return $class;
}

/**
 * Return the classes for a tile containing only text content
 *
 * @since Cryptum 1.0
 */
function cryptum_metro_text_class( $echo = false ) {
	$class = 'tile largeMargin text';
	if ( $echo ) echo $class;
	return $class;
}

/* List of CSS defined colors that suit Metro color scheme */
$cryptum_metro_colors = array('blue', 'green', 'red', 'purple', 'orange');
/* Global counter for current color - to be used with color array so we don't have all same color tiles :) */
$cryptum_metro_color_i = 0;

/**
 * Gets the name of the next color for a tile
 *
 * @since Cryptum 1.0
 */
function cryptum_metro_get_color() {
	global $cryptum_metro_colors, $cryptum_metro_color_i;
	$index = ($cryptum_metro_color_i++ % count($cryptum_metro_colors)); // Gets the next element in the array
	return $cryptum_metro_colors[$index];
}

/**
 * Gets the current color
 *
 * @since Cryptum 1.0
 */
function cryptum_metro_get_color_current() {
	global $cryptum_metro_colors, $cryptum_metro_color_i;
	return $cryptum_metro_colors[$cryptum_metro_color_i];
}

/**
 * Gets the previous posts link, checked against whether the page exists or not
 *
 * @since Cryptum 1.0
 */
function cryptum_get_next_posts_link_checked($max_page = 0) {
	global $paged, $wp_query;

	if ( !$max_page )
		$max_page = $wp_query->max_num_pages;

	if ( !$paged )
		$paged = 1;

	$nextpage = intval($paged) + 1;
	
	if ( !is_single() && ( $nextpage <= $max_page ) ) {
		return next_posts( $max_page, false );
	}
}

/**
 * Gets the previous posts link, checked against whether the page exists or not
 *
 * @since Cryptum 1.0
 */
function cryptum_get_previous_posts_link_checked($max_page = 0) {
	global $paged;

	if ( !is_single() && $paged > 1 ) {
		return previous_posts( false );
	}
}

/**
 * Displays the hidden message in a comment in the header (see theoatmeal.com source code)
 *
 * @since Cryptum 1.0
 */
function cryptum_display_hidden_message() {
	$comment = cryptum_get_theme_option( 'hidden_message' );
	if ($comment):
?>
<!--

<?php echo $comment ?>

-->
<?php
	endif;
}

/**
 * Provides code for loading more content via AJAX
 *
 * @since Cryptum 1.0
 */
function cryptum_content_loader() {
	
}

/**
 * Gets the link attributes for the link post type
 *
 * @since Cryptum 1.0
 */
function cryptum_content_link_attr( $echo = true ) {
    $link_href = strip_tags(get_the_content());
	$link_text = get_the_title();
	$link_attr = 'href="'.$link_href.'" title="'.$link_text.'"';
	if ($echo) echo $link_attr;
	return $link_attr;
}

/**
 * Returns true if the post is any of the specified post formats
 *
 * @since Cryptum 1.0
 */
function cryptum_has_post_format_any () {
	$numargs = func_num_args();
	$arg_list = func_get_args();
	for ($i = 0; $i < $numargs; $i++) {
		if (has_post_format($arg_list[$i])) return true;
	}
	return false;
}

/**
 * Returns the post format (including single/page)
 * Needed because get_post_format only returns single-specific formats (quote, link etc.)
 *
 * @since Cryptum 1.0
 */
function cryptum_get_post_format() {
	$format = get_post_format();
	if ($format) return $format;
	return 'single';
}