<? 
ini_set('session.cookie_domain',
substr($_SERVER['SERVER_NAME'],strpos($_SERVER['SERVER_NAME'],"."),100));

session_start();
$username = $_SESSION['myusername'];
echo "<b>"."Welcome ". $username."</b>";
if(!session_is_registered(myusername)){
header("location:index.php");
}
?>

<html>
<body>
<br>
Login Successful!<br>
<?
function redirecta($url)
{
echo "<meta http-equiv=\"refresh\" content=\"1; URL=".$url."\">";
}
echo "Redirecting to main in 1...";
redirecta("http://portal.cryptum.net");
?>
</body>
</html>