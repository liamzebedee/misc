<?php
/**
 * The template for displaying search forms in Cryptum
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>
	<form method="get" id="searchform" action="<?php echo esc_url( home_url( '/' ) ); ?>" role="search" class="<?php echo cryptum_metro_text_class(  ) ?>">
		<label for="s" class="assistive-text"><?php _e( 'Search', 'cryptum' ); ?></label>
		<input type="text" class="field" name="s" id="s" placeholder="<?php esc_attr_e( 'Search &hellip;', 'cryptum' ); ?>" />
		<input type="submit" class="submit" name="submit" id="searchsubmit" value="<?php esc_attr_e( 'Search', 'cryptum' ); ?>" />
	</form>
