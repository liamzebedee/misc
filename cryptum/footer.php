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

	</div><!-- #main .site-main -->
	<?php if(! cryptum_is_static_front_page()): ?>
	<footer id="colophon" class="site-footer" role="contentinfo">
		<div class="site-info">
			<?php //bloginfo( 'name' ) ?>
		</div><!-- .site-info -->
		<?php //get_sidebar() ?>
	</footer><!-- #colophon .site-footer -->
	
	<?php endif; // !cryptum_is_static_front_page ?>
</div><!-- #page .hfeed .site -->

<?php wp_footer(); ?>

</body>
</html>