<?php get_header(); ?>

<div id="container">
	<div id="content">
		<?php the_post(); ?>
		<div id="post-<?php the_ID(); ?>" <?php post_class(); ?>>
			<h1 class="entry-title"><?php the_title(); ?></h1>
			<div class="entry-content">
				<?php the_content(); ?>
				<?php wp_link_pages('before=<div class="page-link">' . __( 'Pages:', 'rawdata' ) . '&after=</div>') ?>
				<?php edit_post_link( __( 'Edit', 'rawdata' ), '<span class="edit-link">| ', '</span>' ) ?>
            </div>
		</div>        
 
		<?php if ( get_post_custom_values('comments') ) comments_template() // Add a custom field with Name and Value of "comments" to enable comments on this page ?>            
 
	</div>
</div>

<?php get_footer(); ?>