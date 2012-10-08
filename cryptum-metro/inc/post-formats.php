<?php
/**
 * Implementation of the ... post formats
 * http://codex.wordpress.org/Post_Formats
 *
 * @package Cryptum
 * @since Cryptum 1.0
 */
// 'aside', 'chat', 'gallery', 'image', 'status', 'video', 'audio'
$supported_post_formats = array( 'link', 'quote' );

add_theme_support( 'post-formats', $supported_post_formats );
?>