<?php
/**
 * The template for displaying the footer.
 *
 * Contains the closing of the id=main div and all content after
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>

	</div><!-- #main -->

	<footer id="colophon" class="site-footer <?php echo cryptum_metro_tile_class( "ui" ) ?>" role="contentinfo">
		<div class="site-info">
			<hgroup>
				<h1 class="site-title"><a href="<?php echo home_url( '/' ); ?>" title="<?php echo esc_attr( get_bloginfo( 'name', 'display' ) ); ?>" rel="home"><?php bloginfo( 'name' ); ?></a></h1>
				<h2 class="site-description"><?php bloginfo( 'description' ); ?></h2>
				<?php if ( cryptum_get_theme_option ( "show_footer_credits" ) ) : ?>
					<?php do_action( 'cryptum_credits' ); ?>
					<h3 id="credits">
						<a id="credits-wordpress" href="http://wordpress.org/" title="<?php esc_attr_e( 'A Semantic Personal Publishing Platform', 'cryptum' ); ?>" rel="generator"><?php printf( __( 'Proudly powered by %s', 'cryptum' ), 'WordPress' ); ?></a>
						<?php cryptum_seperator_html() ?>
						<?php printf( __( '<span id="credits-theme">%1$s theme by %2$s</span>', 'cryptum' ), '<a href="http://cryptum.net/theme/">Cryptum</a>', '<a href="http://cryptum.net/" rel="designer">liamzebedee</a>' ); ?>
					</h3>
				<?php endif; ?>
			</hgroup>
			
		</div><!-- .site-info -->
		<?php get_sidebar(); ?>
	</footer><!-- .site-footer .site-footer -->
</div><!-- #page .hfeed .site -->

<?php wp_footer(); ?>

</body>
</html>