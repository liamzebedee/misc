<?
function find_version_long()
{
ob_start();
include_once("http://www.minecraft.net/game/getversion.jsp?user=liamzebedee&password=liam&version=999");
$html = ob_get_contents();
ob_end_clean();
$array = explode(":",$html);
$versionfull = $array[0];
return $versionfull;
}
function find_version() 
{
//Created by liamzebedee
//Hosted on http://cryptum.net
$versionfull = find_version_long();
$major = substr($versionfull,0,1);
$submajor = substr($versionfull,1,1);
$trimajor = substr($versionfull,7,1);
$patch = substr($versionfull,9,1);
$humanversion = $major.".".$submajor.".".$trimajor."_"."0".$patch;
return $humanversion;
}

function update_version()
{
$host="localhost"; // Host name 
$username="cryptum1_a"; // Mysql username 
$password="4037005819P"; // Mysql password 
$db_name="cryptum1_minecraft"; // Database name 
$tbl_name="version"; // Table name
// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$long = find_version_long();
$short = find_version();
$cachelonga = mysql_query("SELECT  `long` FROM  `version` ORDER BY  `id` DESC LIMIT 1");
$cachelongb =  mysql_fetch_assoc($cachelonga); 
$cachelongc = $cachelongb[long];

if ($long != $cachelongc) 
{
$longshort = $long ."'". "," ."'". $short;
$alongshort = "'" .$longshort. "'";
$q = "INSERT INTO `version`(`long`,`short`) VALUES ( ".$alongshort." )";
mysql_query($q);


}
else 
{
echo "NO_NEW_VERSIONS <br>";
echo "CURRENT VERSION:" . $cachelongb[long];
}
}

function echo_newest_cache() 
{
$host="localhost"; // Host name 
$username="cryptum1_a"; // Mysql username 
$password="4037005819P"; // Mysql password 
$db_name="cryptum1_minecraft"; // Database name 
$tbl_name="version"; // Table name
// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
$versiontable = mysql_query ("SELECT `short` FROM `version` ORDER BY `id` DESC LIMIT 1");
$version =  mysql_fetch_assoc($versiontable); 
return $version[long];
}
update_version();
?>