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
  } 
  else 
  {

$comment = $_POST['com'];

$host="localhost"; // Host name 
$username="cryptum1_a"; // Mysql username 
$password=""; // Mysql password 
$db_name=""; // Database name 
$tbl_name="homepage"; // Table name
// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");

$arrayBBCode=array(
    ''=>         array('type'=>BBCODE_TYPE_ROOT,  'childs'=>'i'),
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
$comment = strip_tags($comment, "");
$comment = bbcode_parse($BBHandler,$comment);



$xxx = "INSERT INTO `homepage` (text,postid) VALUES ('$comment','$_POST[id]')";

$result = mysql_query($xxx);
if (!$result) 
{
    die('Invalid query');
}
echo "Comment Submitted. Redirecting in 1...";
echo "<meta http-equiv=\"refresh\" content=\"1;url=http://cryptum.net/post_view.php?id=".$_POST[id].'"'.">";
}

?>