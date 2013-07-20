<?
if ($_GET[channelId] != null)
{

$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_chat"; // Database name .

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$result = mysql_query("SELECT  * FROM  `".$_GET['channelId']."` ORDER BY `id` DESC LIMIT 999999999999");


echo mysql_num_rows($result);
}

else
{
echo "Invalid Channel Id";
}
?>