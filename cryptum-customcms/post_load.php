<?
function hf($url,$text)
{
return "<a href=\"".$url."\">".$text."</a>";
}
function getcom($id)
{
$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_comments"; // Database name 

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$q = "SELECT * FROM homepage WHERE postid = ".$id;
$resultb = mysql_query($q);
return mysql_num_rows($resultb);
}
$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_posts"; // Database name 

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$result = mysql_query("SELECT  * FROM  `homepage` ORDER BY  `id` DESC LIMIT 8");
//echo "<div id=postdata>";

?><?

while ($row = mysql_fetch_assoc($result)) 
{


//$title = "<h3>" . $row[title] . "</h3>";
$title = $row[title];
    $a = "<div id";
    echo $a . '=' . '"' . "post" . '"' . '>' . "<h3>" . hf("http://cryptum.net/post_view.php?id=".$row[id],$title) ."</h3>". "<br />";
//echo "<div id=\"postMeta\">";
echo "Posted by <i>" . $row[author] . "</i>"."<br>";
echo "Posted At: ".$row[time]."<br />";
echo "Topic: ".$row[topic]."<br /><br />";
//echo "</div>";
//echo "<div id=\"postText\">";
//echo "<p>";

echo $row[text];
//echo "</p>";
//echo "</div>";
echo("<br />");
echo("<p>
    
    <a class=\"spch-bub-inside\" href=\"http://cryptum.net/post_view.php?id=".$row[id]."\">
        <span class=\"point\"></span>  
        <em>".getcom($row[id])." Comments</em>
    </a>
     
</p>");

echo "</div><br />";

}
//echo "</div>";
?>

