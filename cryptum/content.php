<?php
/**
 * This is the default template that content will resolve to (in the case of archives)
 * @package Cryptum
 * @since Cryptum 1.0
 */
?>

<?php
if( has_post_thumbnail() ) {
	$image = wp_get_attachment_image_src( get_post_thumbnail_id($post->ID), array(300, 170) );
}
?>

<article id="post-<?php the_ID(); ?>" <?php post_class( ); ?>>
	<a href="<?php the_permalink(); ?>" rel="bookmark">
		<div class="entry-thumbnail" style="background: url('<?php echo $image[0]; ?>') center center no-repeat"></div>
		
		<header class="entry-header">
			<h1 class="entry-title"><?php the_title(); ?></h1>
		</header>
	</a>
</article>
