<?
 
function redirecta($url)
{
echo "<meta http-equiv=\"refresh\" content=\"1; URL=".$url."\">";
}

$istillwantposts = true;
if ($istillwantposts) 
{
ini_set('session.cookie_domain',
substr($_SERVER['SERVER_NAME'],strpos($_SERVER['SERVER_NAME'],"."),100));


session_start();
$arrayBBCode=array(
    ''=>         array('type'=>BBCODE_TYPE_ROOT, ),
    'i'=>        array('type'=>BBCODE_TYPE_NOARG, 'open_tag'=>'<i>',
                    'close_tag'=>'</i>', 'childs'=>'b'),
    'url'=>      array('type'=>BBCODE_TYPE_OPTARG,
                    'open_tag'=>'<a href="{PARAM}">', 'close_tag'=>'</a>',
                    'default_arg'=>'{CONTENT}',
                    'childs'=>'b,i'),
    'img'=>      array('type'=>BBCODE_TYPE_NOARG,
                    'open_tag'=>'<img src="', 'close_tag'=>'" />',
                    'childs'=>''),
    'b'=>        array('type'=>BBCODE_TYPE_NOARG, 'open_tag'=>'<b>',
                    'close_tag'=>'</b>'),
'br'=>        array('type'=>BBCODE_TYPE_NOARG, 'open_tag'=>'<br>',
                    'close_tag'=>''),
);

$BBHandler=bbcode_create($arrayBBCode);
$text = strip_tags($_POST[text], "");
$title = strip_tags($_POST[title], "");
$author = strip_tags($_POST[author], "");
$topic = strip_tags($_POST[topic], "");

$text = bbcode_parse($BBHandler,$text);
$title = bbcode_parse($BBHandler,$title);
$author = bbcode_parse($BBHandler,$author);
$topic = bbcode_parse($BBHandler,$topic);






if ($_SESSION['myusername'] != null)
{
$host="localhost"; // Host name 
$username="cryptum1_a"; // Mysql username 
$password=""; // Mysql password 
$db_name=""; // Database name 
$tbl_name="homepage"; // Table name
// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
date_default_timezone_set('Australia/Brisbane');
$time = date('d/m/Y h:i a', time());
mysql_query("INSERT INTO `homepage` (title,text,author,topic,time) VALUES ('$title','$text','$author','$topic','$time')");
echo "Thanks ".$_SESSION['myusername'].".<br>"."Your post has been submitted<br>"."Redirecting in 1...";
redirecta("http://cryptum.net/");
}
else {
require_once('/home/cryptum1/public_html/weblib/recaptchalib.php');
$privatekey = "";
  $resp = recaptcha_check_answer ($privatekey,
                                $_SERVER["REMOTE_ADDR"],
                                $_POST["recaptcha_challenge_field"],
                                $_POST["recaptcha_response_field"]);

  if (!$resp->is_valid) {

    // What happens when the CAPTCHA was entered incorrectly
echo "Captcha entered incorrectly. Redirecting";
redirecta("javascript:history.go(-1)");
  } else 
  {

$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_posts"; // Database name 
$tbl_name="homepage"; // Table name
// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");
date_default_timezone_set('Australia/Brisbane');
$time = date('d/m/Y h:i a', time());
mysql_query("INSERT INTO `homepage` (title,text,author,topic,time) VALUES ('$title','$text','Annonymous','$topic','$time')");


echo "Thanks Post Submitted. <br> Redirecting in 1 second...";
redirecta("http://cryptum.net/");

    // Your code here to handle a successful verification
  }
  }
}
else
{
echo "<h3>I dont want any posts right now</h3>";
redirecta("http://cryptum.net/");
}

?>