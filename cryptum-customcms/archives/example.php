<?php

function downloadfile($url,$pathtosave)
{
// folder to save downloaded files to. must end with slash
$newfname = $pathtosave . basename($url);

$file = fopen ($url, "rb");
if ($file) {
  $newf = fopen ($newfname, "wb");

  if ($newf)
  while(!feof($file)) {
    fwrite($newf, fread($file, 1024 * 8 ), 1024 * 8 );
    
  }
}

if ($file) {
  fclose($file);
}

if ($newf) {
  fclose($newf);
}
}

// It may take a whils to crawl a site ...
set_time_limit(10000);

// Inculde the phpcrawl-mainclass
include("classes/phpcrawler.class.php");

// Extend the class and override the handlePageData()-method
class MyCrawler extends PHPCrawler 
{
  function handlePageData(&$page_data) 
  {
    // Here comes your code.
    // Do whatever you want with the information given in the
    // array $page_data about a page or file that the crawler actually found.
    // See a complete list of elements the array will contain in the 
    // class-refenence.
    // This is just a simple example.
    
    // Print the URL of the actual requested page or file
    echo $page_data["url"]."<br>";

    
  }
}

// Now, create an instance of the class, set the behaviour
// of the crawler (see class-reference for more methods)
// and start the crawling-process.

$crawler = &new MyCrawler();

// URL to crawl
$crawler->setURL("www.brezerd.net");

// Only receive content of files with content-type "text/html"
// (regular expression, preg)


// Ignore links to pictures, dont even request pictures
// (preg_match)
$crawler->addNonFollowMatch("/.(jpg|gif|png)$/ i");

// Store and send cookie-data like a browser does
$crawler->setCookieHandling(true);

// Set the traffic-limit to 1 MB (in bytes,
// for testing we dont want to "suck" the whole site)
$crawler->setTrafficLimit(10000000 * 1024);

// Thats enough, now here we go
$crawler->go();


// At the end, after the process is finished, we print a short
// report (see method getReport() for more information)


?>