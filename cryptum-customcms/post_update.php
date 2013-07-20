<?
  $doc = new DOMDocument();
  $doc->load('http://giveawayoftheday.com/feed/');
  $arrFeeds = array();
$i = 0;
  foreach ($doc->getElementsByTagName('item') as $node) {
if ($i <= 0)
{
    $itemRSS = array ( 
      'title' => $node->getElementsByTagName('title')->item(0)->nodeValue,
      'desc' => $node->getElementsByTagName('description')->item(0)->nodeValue,
      'link' => $node->getElementsByTagName('link')->item(0)->nodeValue,
      'date' => $node->getElementsByTagName('pubDate')->item(0)->nodeValue
      );
   echo $itemRSS['title'];
echo $itemRSS['desc'];
$i++;
}
else{exit;}
  }
?>