<?php
/**
 * Cryptum functions and definitions
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */

// Set the content width based on the theme's design and stylesheet.
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

	// Custom template tags for this theme.
	require( get_template_directory() . '/inc/template-tags.php' );

	// Theme Options
	add_theme_support('options-framework'); // We use the Options Framework
	require( get_template_directory() . '/options.php' );
	
	/**
	 * Make theme available for translation
	 * Translations can be filed in the /languages/ directory
	 * If you're building a theme based on Cryptum, use a find and replace
	 * to change 'cryptum' to the name of your theme in all the template files
	 */
	load_theme_textdomain( 'cryptum', get_template_directory() . '/languages' );

	// Add default posts and comments RSS feed links to head
	add_theme_support( 'automatic-feed-links' );

	// Enable support for Post Thumbnails
	add_theme_support( 'post-thumbnails' );

	// This theme uses wp_nav_menu() in one location.
	register_nav_menus( array(
		'primary' => __( 'Primary Menu', 'cryptum' ),
	) );
	
	add_theme_support( 'post-thumbnails' );
	set_post_thumbnail_size( 300, 170, true );
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
		'after_widget' => '</aside>',
		'before_title' => '<h1 class="widget-title">',
		'after_title' => '</h1>',
	) );
}
add_action( 'widgets_init', 'cryptum_widgets_init' );

/**
 * Enqueue scripts and styles
 */
function cryptum_scripts() {
	wp_enqueue_style( 'style', get_stylesheet_uri() );
	
	wp_enqueue_script( 'bootstrap', get_template_directory_uri() . '/js/bootstrap.min.js', array( 'jquery' ), '20120927', true );
	// wp_enqueue_script( 'fitText', get_template_directory_uri() . '/js/fitText.js', array( 'jquery' ), '201209093', true );

	if ( is_singular() && comments_open() && get_option( 'thread_comments' ) ) {
		wp_enqueue_script( 'comment-reply' );
	}

	if ( is_singular() && wp_attachment_is_image() ) {
		wp_enqueue_script( 'keyboard-image-navigation', get_template_directory_uri() . '/js/keyboard-image-navigation.js', array( 'jquery' ), '20120202' );
	}
}
add_action( 'wp_enqueue_scripts', 'cryptum_scripts' );

/* 
 * Helper function to return the theme option value. If no value has been saved, it returns $default.
 * Needed because options are saved as serialized strings.
 *
 */
if ( !function_exists( 'of_get_option' ) ) {
function of_get_option($name, $default = false) {
	
	$optionsframework_settings = get_option('optionsframework');
	
	// Gets the unique option id
	$option_name = $optionsframework_settings['id'];
	
	if ( get_option($option_name) ) {
		$options = get_option($option_name);
	}
		
	if ( isset($options[$name]) ) {
		return $options[$name];
	} else {
		return $default;
	}
}
}

// Allows us to upload SVGs, for scalable logos etc.
add_filter('upload_mimes', 'custom_upload_mimes');
function custom_upload_mimes ( $existing_mimes = array() ) {
	// add the file extension to the array
	$existing_mimes['svg'] = 'mime/type';
	// call the modified list of extensions
	return $existing_mimes;
}

// Returns true for a static front page
function cryptum_is_static_front_page() {
	return (!is_home() && is_front_page());
}

add_filter( 'the_category', 'cryptum_fix_cat' );
function cryptum_fix_cat( $text ) { 
	// DIRTY FIX
	// Wordpress has an issue with abusing the rel tag, which also breaks styling with Chrome
	// @see http://wordpress.org/support/topic/wordpress-abuses-rel-tag
	// 'category' is not a defined tag, so instead I replace it with a class 'category' while keeping the 'rel="tag"'
	// @see http://blog.whatwg.org/the-road-to-html-5-link-relations#rel-tag
	$text = str_replace('rel="category tag"', 'rel="tag" class="category"', $text);
	return $text; 
}

add_filter( 'the_tags', 'cryptum_fix_tag' );
function cryptum_fix_tag( $text ) { 
	// Just so we are standard with cryptum_fix_cat
	$text = str_replace('rel="tag"', 'rel="tag" class="tag"', $text);
	return $text; 
}