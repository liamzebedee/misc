<?php

add_action( 'admin_init', 'theme_options_init' );
add_action( 'admin_menu', 'theme_options_add_page' );

/**
 * Init plugin options to white list our options
 */
function theme_options_init() {
	register_setting( 'rawdata_options', 'allow_copyright_notice', 'theme_options_validate' );
	register_setting( 'rawdata_options', 'allow_credits_notice', 'theme_options_validate' );
}

/**
 * Load up the menu page
 */
function theme_options_add_page() {
	add_theme_page( __( 'Theme Options', 'rawdata' ), __( 'Theme Options', 'rawdata' ), 'edit_theme_options', 'theme_options', 'theme_options_do_page' );
}

/**
 * Create the options page
 */
function theme_options_do_page() {

	if ( ! isset( $_REQUEST['settings-updated'] ) )
		$_REQUEST['settings-updated'] = false;

	?>
	<div class="wrap">
		<?php screen_icon(); echo "<h2>" . get_current_theme() . __( ' - Theme Options', 'rawdata' ) . "</h2>"; ?>

		<?php if ( false !== $_REQUEST['settings-updated'] ) : ?>
		<div class="updated fade"><p><strong><?php _e( 'Options saved', 'rawdata' ); ?></strong></p></div>
		<?php endif; ?>

		<form method="post" action="options.php">
			<?php settings_fields( 'rawdata_options' ); ?>

			<table class="form-table">
				<tr valign="top"><th scope="row"><?php _e( 'Show copyright notice', 'rawdata' ); ?></th>
					<td>
						<input id="allow_copyright_notice" name="allow_copyright_notice" type="checkbox" value="1" <?php checked( '1', get_option('allow_copyright_notice') ); ?> />
						<label class="description" for="allow_copyright_notice"><?php _e( 'Show the copyright notice at the footer', 'rawdata' ); ?></label>
					</td>
				</tr>
				<tr valign="top"><th scope="row"><?php _e( 'Show credits', 'rawdata' ); ?></th>
					<td>
						<input id="allow_credits_notice" name="allow_credits_notice" type="checkbox" value="1" <?php checked( '1', get_option('allow_credits_notice') ); ?> />
						<label class="description" for="allow_credits_notice"><?php _e( 'Show the credits for this theme at the footer', 'rawdata' ); ?></label>
					</td>
				</tr>
				
			</table>

			<p class="submit">
				<input type="submit" class="button-primary" value="<?php _e( 'Save Options', 'rawdata' ); ?>" />
			</p>
		</form>
	</div>
	<?php
}

/**
 * Sanitize and validate input. Accepts an array, return a sanitized array.
 */
function theme_options_validate( $input ) {
	return $input;
}

// adapted from http://planetozh.com/blog/2009/05/handling-plugins-options-in-wordpress-28-with-register_setting/