<?
if ($_GET["page"] != null)
{
$num = $_GET["page"];
$l = 8*$num;
$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_posts"; // Database name 

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$result = mysql_query("SELECT  * FROM  `homepage` ORDER BY  `id` DESC LIMIT ".$l);
$i = 0;
$q = $l - 8;
function hf($url,$text)
{
return "<a href=".$url.">".$text."</a>";
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
while ($row = mysql_fetch_assoc($result)) 
{
if ($i > $q)
{
$title = "<b>" . $row[title] . "</b>";
    $a = "<div class";
    echo $a . '=' . '"' . "post" . '"' . '>' . hf("http://cryptum.net/post_view.php?id=".$row[id],$title) . "<br />";
echo "Posted by <i>" . $row[author] . "</i>"."<br>";
echo "Topic: ".$row[topic]."<br><br>";


echo $row[text];
echo("<br>");
echo("<p>
    
    <a class=\"spch-bub-inside\" href=\"http://cryptum.net/post_comments.php?id=".$row[id]."\">
        <span class=\"point\"></span>  
        <em>".getcom($row[id])." Comments</em>
    </a>
     
</p>");
$i++;
}
$i++;
}

} 
else 
{
echo "Page = null. Program Exit.";
}
?>