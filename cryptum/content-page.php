<?php
/**
 * The template used for displaying page content in page.php
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>

<article id="post-<?php the_ID(); ?>" <?php post_class( 'span8' ); ?>>
	<header class="entry-header page-header">
		<h1 class="entry-title"><?php the_title(); ?></h1>
	</header><!-- .entry-header -->

	<div class="entry-content">
		<?php the_content(); ?>
	</div><!-- .entry-content -->
	
	<?php edit_post_link( __( 'Edit', 'cryptum' ), '<span class="edit-link">', '</span>' ); ?>
</article><!-- #post-<?php the_ID(); ?> -->
