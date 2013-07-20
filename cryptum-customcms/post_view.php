<?
function realpost($id)
{
$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_posts"; // Database name 

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$q = "SELECT * FROM homepage WHERE id = '$id' LIMIT 100";
$resultb = mysql_query($q);
if (mysql_num_rows($resultb) != 1)
{
//echo "Post Doesn't Exist!";
return false;
}
else
{
return true;
}
}

function is_number($number)
{ 
    $text = (string)$number;
    $textlen = strlen($text);
    if ($textlen==0) return 0;
    for ($i=0;$i < $textlen;$i++)
    { $ch = ord($text{$i});
       if (($ch<48) || ($ch>57)) return 0;
    }
    return 1;
}

function goodinput($string)
{
if(is_number($string))
{
$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_comments"; // Database name 

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$q = "SELECT * FROM homepage WHERE postid = ".$string;
$resultb = mysql_query($q);
if ($resultb)
{
return true;
}
if (!$resultb)
{
return false;
}
}
else
{
return false;
}
}


if (goodinput($_GET[id]) && realpost($_GET[id]))
{
function killsql($string)
{
$string = stripslashes($string);
$string = mysql_real_escape_string($string);
return $string;
}
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


/* if ($_GET[id] <= 76)
{
echo "Sorry but the comment system hadn't been created at the time this post was created.";

exit;
} */
$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_posts"; // Database name .

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$result = mysql_query("SELECT  * FROM  `homepage` WHERE `id` = ".killsql($_GET[id]));

$row = mysql_fetch_assoc($result);
$pagetitle = " '$row[title]'";
include("/home/cryptum1/public_html/weblib/template.php");
$title = "<h3>" . $row[title] . "</h3>";
    $a = "<div id";
    echo $a . '=' . '"' . "post" . '"' . '>' . hf("http://cryptum.net/post_view.php?id=".$row[id],$title) . "<br />";
echo "Posted by <i>" . $row[author] . "</i>"."<br />";
echo "Posted At: ".$row[time]."<br />";
echo "Topic: ".$row[topic]."<br /><br />";


echo $row[text];
echo("<br>");
echo("<p>
    
    <a class=\"spch-bub-inside\" href=\"http://cryptum.net/post_comments.php?id=".$row[id]."\">
        <span class=\"point\"></span>  
        <em>".getcom($row[id])." Comments</em>
    </a>
     
</p>");

echo "</div><br>";
echo "<br><br><br>";

$db_name="cryptum1_comments"; // Database name 

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$q = "SELECT  * FROM  `homepage` WHERE `postid` = " . killsql($_GET[id]) . " ORDER BY `commentid` DESC";
$resultb = mysql_query($q);
if (!$resultb) {
    die('Invalid query: ' . mysql_error());
}

while($rowb = mysql_fetch_assoc($resultb))
{
echo "<div id=\"comment\">";
echo "<b>".$rowb[user]."</b>";
echo $rowb[text];
echo "</div>";
}
}
else
{
include("/home/cryptum1/public_html/weblib/template.php");
echo "<div id=\"post\"><h3>Post Not Found</h3><br>The id of that post is invalid. Please enter a valid id. No hacking peoples</div>";

exit;
}
?>
<br><br>
<form method=post action=post_comment_create.php>
<textarea name="com" cols="80" rows="10" id="text"></textarea><br>
Please enter this so we can <b>hope</b> to believe your not a spammer<br>
<?
require_once('/home/cryptum1/public_html/weblib/recaptchalib.php');
  $publickey = ""; // you got this from the signup page
  echo recaptcha_get_html($publickey);
?>
<input type=submit name=Comment>
<? echo "<input type=hidden name=id value=".killsql($_GET[id])." </form>"; ?>
<? include("/home/cryptum1/public_html/weblib/footer.php"); ?>