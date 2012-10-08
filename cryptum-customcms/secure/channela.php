<?
session_start();
?>
<!DOCTYPE HTML>
<html>
<head>
<script type="text/javascript" src="http://cryptum.net/weblib/bbeditor/bbeditor/ed.js"></script>
<script type=text/javascript> 
<!--
function show_alert() { 
var msg = 'i can haz pwnage. oh and awesome chat system made by cryptum technologies';
alert(msg); 
}
-->
</script>


<title>Channel A</title>
<script type="text/javascript" src="http://cryptum.net/weblib/jq.js"></script>
<LINK REL=StyleSheet HREF="default.css" TYPE="text/css" MEDIA=screen>
<script type="text/javascript">
<!--
$(window).keypress(function(e) {
    if(e.keyCode == 13) {
        //document.forms["forma"].submit();
sendMessage();
    }
});

    function ajaxRequest(){
if (window.XMLHttpRequest)
  {// code for IE7+, Firefox, Chrome, Opera, Safari
  return new XMLHttpRequest();
  }
else
  {// code for IE6, IE5
  return new ActiveXObject("Microsoft.XMLHTTP");
  }
}

function getpost()
{
var mygetrequest=new ajaxRequest()
mygetrequest.onreadystatechange=function(){
 if (mygetrequest.readyState==4){
  if (mygetrequest.status==200 || window.location.href.indexOf("http")==-1){
   document.getElementById("messageData").innerHTML=mygetrequest.responseText
  }
  else{
   
  }
 }
}
var chn=encodeURIComponent(document.getElementById("chname").value)
mygetrequest.open("GET", "channel.php?channelId="+chn, true)
mygetrequest.send(null)

}

function getNumberOfPosts()
{
var mygetrequest=new ajaxRequest()
mygetrequest.onreadystatechange=function(){
 if (mygetrequest.readyState==4){
  if (mygetrequest.status==200 || window.location.href.indexOf("http")==-1){
   
  }
  else{
   
  }
 }
}
var chn=encodeURIComponent(document.getElementById("chname").value)
mygetrequest.open("GET", "numMsg.php?channelId="+chn, true)
mygetrequest.send(null)

}

function init()
{
//document.write(getNumberOfPosts());
//if (getNumberOfPosts() > document.getElementById("numMsg").innerHTML)
//{
getpost();
setTimeout("init()", 750);
//}
}

function sendMessage()
{
var textd = document.forms.forma.text.value;
$.post("channela.php", { text: textd } );
document.forms.forma.text.value = '';
}
-->
</script>



</head>
<body BGCOLOR="black" TEXT="white" onLoad="init()">
<h3>Chat has been updated. See version.html</h3>
<table>
<tr style="background-color:#eeeeee;">
<td>
<a href="http://secure.cryptum.net/set.php">Preferences</a></td><td>
<b><a href="http://secure.cryptum.net/readthis.html">README</a></b><br></td><td>
<b><a href="http://secure.cryptum.net/version.html">Secure Chat Version 0.2</a></b><br></td>
<td><input type=button value="I can haz...?" OnClick="show_alert()"><br>
</td>
</tr></table><br>
<?
//$a = $_POST[''];
if($_POST['text'] != null && ($_POST['text']) != '\n') 
{
//http://www.phpbuilder.com/board/showthread.php?t=10326721
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

$host="localhost"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name="cryptum1_chat"; // Database name .

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");

$da = $_POST["chname"];
$arrayBBCode=array(
    ''=>         array('type'=>BBCODE_TYPE_ROOT, ),
    'i'=>        array('type'=>BBCODE_TYPE_NOARG, 'open_tag'=>'<i>',
                    'close_tag'=>'</i>', 'childs'=>'b'),
'u'=>        array('type'=>BBCODE_TYPE_NOARG, 'open_tag'=>'<u>',
                    'close_tag'=>'</u>', 'childs'=>'b'),
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
$text = bbcode_parse($BBHandler,$text);

$user = encrypt($_SESSION["chatusername"],$key);
$text = encrypt($text,$key);

$q = "INSERT INTO  `a` (`text`,`user`) VALUES ('$text','$user')";
//echo $q;
mysql_query($q);
//echo mysql_error();

}

?>

<form action="channela.php" method="POST" id="forma"> 
<input type="hidden" id="chname" name="chname" value="a">
Data:<br>

<script type="text/javascript">edToolbar('text'); </script>
<textarea name="text" id="text" style="margin-top: 2px; margin-bottom: 2px; height: 231px; margin-left: 2px; margin-right: 2px; width: 997px;"></textarea><br></form><br>

<br>


<div id="messageData">
</div>

</body>
</html>