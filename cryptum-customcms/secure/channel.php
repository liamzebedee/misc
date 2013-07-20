<!DOCTYPE HTML>
<html>
<head></head>
<body>

<div id="messageData">
<?
function encrypt($string, $key) {
  $result = '';
  for($i=0; $i<strlen($string); $i++) {
    $char = substr($string, $i, 1);
    $keychar = substr($key, ($i % strlen($key))-1, 1);
    $char = chr(ord($char)+ord($keychar));
    $result.=$char;
  }
  return base64_encode($result);
}
function decrypt($string, $key) {
  $result = '';
  $string = base64_decode($string);
  for($i=0; $i<strlen($string); $i++) {
    $char = substr($string, $i, 1);
    $keychar = substr($key, ($i % strlen($key))-1, 1);
    $char = chr(ord($char)-ord($keychar));
    $result.=$char;
  }
  return $result;
}


// Secret key to encrypt/decrypt with 
$key='hsd834mf04mufjr9rkniG&TGjmdoi'; // 8-32 characters without spaces 

if ($_GET['channelId'] != null)
{
$channelId = stripslashes($_GET['channelId']);
// Secret key to encrypt/decrypt with 
$key='hsd834mf04mufjr9rkniG&TGjmdoi'; // 8-32 characters without spaces 
$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_chat"; // Database name .

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$result = mysql_query("SELECT  * FROM  `".$channelId."` ORDER BY `id` DESC");
//gets all messages
$i = 0;
echo "<input type=\"hidden\" name=\"numMsg\" value=\"".mysql_num_rows($result)."\">";
while($row = mysql_fetch_assoc($result))
{
if ($i < 12)
{
$user = decrypt($row[user],$key);
$text = decrypt($row[text],$key);
echo "<div class=\"message\">";
echo "<b>".$user."</b><br>";
echo "<i>MSG ".$row[id]."</i><br>";
echo $text."<br>";
echo "<hr>";
echo "</div>";
$i++;
}
}

}

else
{
echo "Channel Id is unspecified";
}
?>


</body>
</html>
Developed by Cryptum Technologies