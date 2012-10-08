<?
header("Content-Type: application/rss+xml");

$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_posts"; // Database name 

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$result = mysql_query("SELECT  * FROM  `homepage` ORDER BY id DESC LIMIT 99999999999");
echo "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>";
?>

<rss version="2.0">

<channel> 
      <title>Cryptum Post Feed</title>     
    <link>http://www.cryptum.net/</link> 
    <description>An RSS feed created to ensure other platforms/technology can access the cryptum posts in a familiar and globally defined way.</description>
<language>en-us</language>
<?

while($row = mysql_fetch_array($result))
{
echo "<item>\n";
echo "<title>".$row[title]."</title>\n";
echo "<author>admin@cryptum.net (Liam)</author>\n";
echo "<category>".$row[topic]."</category>\n";
//echo "<description>"."Author:".$row[author]."\n".$row[text]."</description>";
//echo "<description>"."<![CDATA[".$row[text]."]]>"."</description>";

echo "<description>".$row[text]."</description>";
echo "<link>"."http://cryptum.net/post_view.php?id=".$row[id]."</link>";
echo "<guid isPermaLink=\"true\">"."http://cryptum.net/post_view.php?id=".$row[id]."</guid>";
echo "</item>";
echo "\n";

}
?>

</channel>
</rss>

