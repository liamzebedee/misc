<?php
/**
 * Cryptum Theme Options
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */

/**
 * Register the form setting for our cryptum_options array.
 *
 * This function is attached to the admin_init action hook.
 *
 * This call to register_setting() registers a validation callback, cryptum_theme_options_validate(),
 * which is used when the option is saved, to ensure that our option values are properly
 * formatted, and safe.
 *
 * @since Cryptum 1.0
 */
function cryptum_theme_options_init() {
	register_setting(
		'cryptum_options',       // Options group, see settings_fields() call in cryptum_theme_options_render_page()
		'cryptum_theme_options', // Database option, see cryptum_get_theme_options()
		'cryptum_theme_options_validate' // The sanitization callback, see cryptum_theme_options_validate()
	);

	// Register our settings field group
	add_settings_section(
		'general', // Unique identifier for the settings section
		'', // Section title (we don't want one)
		'__return_false', // Section callback (we don't want anything)
		'theme_options' // Menu slug, used to uniquely identify the page; see cryptum_theme_options_add_page()
	);

	// Register our individual settings fields
	add_settings_field(
		'short_text', // Unique identifier for the field for this section
		__( 'Short text', 'cryptum' ), // Setting field label
		'cryptum_settings_field_short_text', // Function that renders the settings field
		'theme_options', // Menu slug, used to uniquely identify the page; see cryptum_theme_options_add_page()
		'general' // Settings section. Same as the first argument in the add_settings_section() above
	);
	add_settings_field(
		'show_footer_credits',
		__( 'Show footer credits', 'cryptum' ),
		'cryptum_settings_field_show_footer_credits',
		'theme_options',
		'general'
	);
	add_settings_field(
		'hidden_message',
		__( 'Hidden message', 'cryptum' ),
		'cryptum_settings_field_hidden_message',
		'theme_options',
		'general'
	);
}
add_action( 'admin_init', 'cryptum_theme_options_init' );

/**
 * Change the capability required to save the 'cryptum_options' options group.
 *
 * @see cryptum_theme_options_init() First parameter to register_setting() is the name of the options group.
 * @see cryptum_theme_options_add_page() The edit_theme_options capability is used for viewing the page.
 *
 * @param string $capability The capability used for the page, which is manage_options by default.
 * @return string The capability to actually use.
 */
function cryptum_option_page_capability( $capability ) {
	return 'edit_theme_options';
}
add_filter( 'option_page_capability_cryptum_options', 'cryptum_option_page_capability' );

/**
 * Add our theme options page to the admin menu.
 *
 * This function is attached to the admin_menu action hook.
 *
 * @since Cryptum 1.0
 */
function cryptum_theme_options_add_page() {
	$theme_page = add_theme_page(
		__( 'Theme Options', 'cryptum' ),   // Name of page
		__( 'Theme Options', 'cryptum' ),   // Label in menu
		'edit_theme_options',                    // Capability required
		'theme_options',                         // Menu slug, used to uniquely identify the page
		'cryptum_theme_options_render_page' // Function that renders the options page
	);
}
add_action( 'admin_menu', 'cryptum_theme_options_add_page' );

/**
 * Returns the options array for Cryptum.
 *
 * @since Cryptum 1.0
 */
function cryptum_get_theme_options() {
	$saved = (array) get_option( 'cryptum_theme_options' );
	$defaults = array(
		'short_text'       => '',
		'show_footer_credits'      => '',
		'hidden_message'   => '',
	);

	$defaults = apply_filters( 'cryptum_default_theme_options', $defaults );

	$options = wp_parse_args( $saved, $defaults );
	$options = array_intersect_key( $options, $defaults );

	return $options;
}

/**
 * Returns an option from the options array for Cryptum.
 *
 * @since Cryptum 1.0
 */
function cryptum_get_theme_option($option) {
	$options = cryptum_get_theme_options();
	return $options[$option];
}
 
/**
 * Renders the short text setting field.
 */
function cryptum_settings_field_short_text() {
	$options = cryptum_get_theme_options();
	?>
	<label for="short_text">
		<input type="checkbox" name="cryptum_theme_options[short_text]" id="short_text" <?php checked( 'on', $options['short_text'] ); ?> />
		<?php _e( 'Enable short text explanations for [categories], #tags and @authors', 'cryptum' );  ?>
	</label>
	<?php
}

/**
 * Renders the show footer setting field.
 */
function cryptum_settings_field_show_footer_credits() {
	$options = cryptum_get_theme_options();
	?>
	<label for="show_footer_credits">
		<input type="checkbox" name="cryptum_theme_options[show_footer_credits]" id="show_footer_credits" <?php checked( 'on', $options['show_footer_credits'] ); ?> />
		<?php _e( 'Show credits in the footer (such as WordPress and theme credits)', 'cryptum' );  ?>
	</label>
	<?php
}

function cryptum_settings_field_hidden_message() {
	$options = cryptum_get_theme_options();
	?>
	<textarea class="large-text" type="text" name="cryptum_theme_options[hidden_message]" id="hidden_message" cols="50" rows="10" /><?php echo esc_textarea( $options['hidden_message'] ); ?></textarea>
	<label class="description" for="hidden_message"> <?php _e( 'A hidden message in the form of a HTML comment to show in the header', 'cryptum' );  ?> </label>
	<?php
}

/**
 * Renders the Theme Options administration screen.
 *
 * @since Cryptum 1.0
 */
function cryptum_theme_options_render_page() {
	?>
	<div class="wrap">
		<?php screen_icon(); ?>
		<h2><?php printf( __( '%s Theme Options', 'cryptum' ), wp_get_theme() ); ?></h2>
		<?php settings_errors(); ?>

		<form method="post" action="options.php">
			<?php
				settings_fields( 'cryptum_options' );
				do_settings_sections( 'theme_options' );
				submit_button();
			?>
		</form>
	</div>
	<?php
}

/**
 * Sanitize and validate form input. Accepts an array, return a sanitized array.
 *
 * @see cryptum_theme_options_init()
 * @todo set up Reset Options action
 *
 * @param array $input Unknown values.
 * @return array Sanitized theme options ready to be stored in the database.
 *
 * @since Cryptum 1.0
 */
function cryptum_theme_options_validate( $input ) {
	$output = array();

	// Checkboxes will only be present if checked.
	if ( isset( $input['short_text'] ) )
		$output['short_text'] = 'on';
	if ( isset( $input['show_footer_credits'] ) )
		$output['show_footer_credits'] = 'on';
	if ( isset( $input['hidden_message'] ) ) 
		$output['hidden_message'] = $input['hidden_message'];
	return apply_filters( 'cryptum_theme_options_validate', $output, $input );
}
