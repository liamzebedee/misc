<?
$pagetitle = "Post List";
include("/home/cryptum1/public_html/template.php");
echo "<b>Post List</b>";

$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_posts"; // Database name 

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");

$result = mysql_query("SELECT  `id` FROM  `homepage` LIMIT 9999999");
$i = 0;



while($row = mysql_fetch_assoc($result))
{
$a = mysql_query("SELECT `title` FROM `homepage` WHERE `id`=".$row[id]);
$b = mysql_fetch_assoc($a);
$title = $b[title];
echo "<li>"."<a "."href=\"http://cryptum.net/post_view.php?id=".$row[id].'"'.">".$title."</a>";
echo "<br>";
$i++;
}
echo "<br>";
echo $i." posts<br>";
?>