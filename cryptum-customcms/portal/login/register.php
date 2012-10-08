<?
function redirecta($url)
{
echo "<meta http-equiv=\"refresh\" content=\"1; URL=".$url."\">";
}
if ($_POST['myusername'] != null)
{
require_once('/home/cryptum1/public_html/recaptchalib.php');
$privatekey = "6LcTa8ASAAAAANWrs227JBUcqH_yPV80JqXae7Aq";
  $resp = recaptcha_check_answer ($privatekey,
                                $_SERVER["REMOTE_ADDR"],
                                $_POST["recaptcha_challenge_field"],
                                $_POST["recaptcha_response_field"]);

  if (!$resp->is_valid) {
    // What happens when the CAPTCHA was entered incorrectly
echo "Account created Successfully!";
    redirecta("http://login.cryptum.net");
  } else {
    // Your code here to handle a successful verification
  }
}
include("/home/cryptum1/public_html/template.php");
?>



<script type=text/javascript>
function checkpwd()
{
var a = document.getElementById('mypassword');
var b = document.getElementById('confirmpassword');
if (a.value != b.value)
{
document.('<br>Passwords do not match!');
}
else
{
document.write('<br>Passwords match!');
}
}
</script>


<form method=POST action="register.php">
<td width="78">Username</td>
<td width="6">:</td>
<td width="294"><input name="myusername" type="text" id="myusername"></td><br>
</tr>
<tr>
<td>Password</td>
<td>:</td>
<td><input name="mypassword" type="password" id="mypassword"></td><br>
Confirm Password: <input name="confirmpassword" type="password" id="confirmpassword" onblur=checkpwd();></td>
<div name=pwdver></div>
<!--
INSERT INTO  `cryptum1_users`.`members` (
`id` ,
`username` ,
`password`
)
VALUES (
NULL ,  $_GET['myusername'],  $_GET['mypassword']
);
-->
<div align=center>
<?
require_once('/home/cryptum1/public_html/recaptchalib.php');
  $publickey = "6LcTa8ASAAAAAIpf6L7o0nuzdMquEbmzwJZlahZe"; // you got this from the signup page
  echo recaptcha_get_html($publickey);
?>
<input type="submit" />
</div></form>