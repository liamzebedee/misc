<?
session_start();
if (isset($_POST['submit']))
{

$_SESSION['chatusername'] = stripslashes($_POST['username']);
echo "<meta http-equiv=\"refresh\" content=\"1;url=http://secure.cryptum.net/\">";
echo "Your username has been set to ".$_SESSION['chatusername']."<br><hr> Redirecting to main...";
}
echo "Current USERNAME:".$_SESSION['chatusername']."<br><br><br>";
?>
<form method="POST" action="set.php">
<input type=hidden name="submit">
USERNAME: <input type=text name="username"><br>
Colour: <input type=text name="colour"><br>
<br /><input type=submit name=SET>
</form>