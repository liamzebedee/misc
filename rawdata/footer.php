		    </div>
		    <div id="footer">
		        <div id="site-info">
					<div id="info-us">
						<?php if ( is_home() || is_front_page() ) { ?>
							<div id="blog-description"><b><?php bloginfo('name') ?></b> - <?php bloginfo('description'); ?></div>
						<?php } else { ?>
							<h1 id="blog-title"><?php bloginfo('name'); ?></h1>
						<?php } ?>
					</div>
					<div id="info-data">
						<?php
						$rawdata_footerstuff = false;
						if( get_option('allow_copyright_notice', false) ) {
							rawdata_copyright_notice();
							$rawdata_footerstuff = true;
						}
						if( get_option('allow_credits_notice', false) ) {
							rawdata_credits_notice();
							$rawdata_footerstuff = true;
						}
						if( !$rawdata_footerstuff )
							_e("Nothing here...yet", "rawdata"); 
						?>
					</div>
		        </div>
			</div>
		</div>
		<?php wp_footer(); ?>
	</body>
</html>