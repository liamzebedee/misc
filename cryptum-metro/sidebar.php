<?php
/**
 * The Sidebar containing the main widget areas.
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>
		<div id="secondary" class="widget-area" role="complementary">
			<?php do_action( 'before_sidebar' ); ?>
			<?php if ( ! dynamic_sidebar( 'sidebar-1' ) ) : ?>
				<?php // Default widgets go here ?>
			<?php endif; // end sidebar widget area ?>
		</div><!-- #secondary .widget-area -->
