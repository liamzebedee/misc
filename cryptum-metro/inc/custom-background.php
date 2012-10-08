<?php
/**
 * Implementation of the Custom Background feature
 * http://codex.wordpress.org/Custom_Backgrounds
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */

$custom_background_defaults = array(
	'default-color'          => 'EFEFEF',
	'default-image'          => get_template_directory_uri() . '/images/background.png'
);

add_theme_support( 'custom-background', $custom_background_defaults );

function cryptum_custom_background() {
	$custom_background_args = get_theme_support( 'custom-background' );
?>
		<div id="page-background">
			<img src="<?php echo $custom_background_args['default-image'] ?>" alt="<?php _e( 'Background Image', 'cryptum') ?>">
		</div>
<?php
}
?>