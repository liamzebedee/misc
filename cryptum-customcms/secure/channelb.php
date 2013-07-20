<!DOCTYPE HTML>
<html>
<head><title>hi</title><script type="text/javascript" src="http://cryptum.net/weblib/jq.js"></script></head>
<?
$a = $_POST['chname'];
if($_POST['text'] != null) 
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
//$user = encrypt($_POST["user"],$key);
$text = encrypt($_POST["text"],$key);
$da = $_POST["chname"];
//session_start();
//$username = $_SESSION['chatusername'];
$username=$_SERVER['REMOTE_ADDR'];

$q = "INSERT INTO  `$a` (`text`) VALUES ('$text')";
//$q = "INSERT INTO  `$a` (`text` , `name`) VALUES ('$text','$username')";
//echo $q;
//$q = "INSERT INTO  ".$da." (`text` , `user`) VALUES (".$text.",".$user.")";
mysql_query($q);
//echo $q;
//echo $da;
//echo $text;
//echo $user;
//echo "<META HTTP-EQUIV=\"Refresh\" CONTENT=\"1; URL=".$_SERVER['HTTP_REFERER']."\">";
}

?>
<BODY onLoad="init()">
<center><b><a href="http://secure.cryptum.net/readthis.html">README</a></b></center><br>
<form action="channelb.php" method="POST" id="forma"> 
<input type="hidden" id="chname" name="chname" value="b">
Data:<br><textarea name="text" id="text" style="margin-top: 2px; margin-bottom: 2px; height: 231px; margin-left: 2px; margin-right: 2px; width: 997px; "></textarea><br></form><br><br><hr />
<script type="text/javascript">
$(window).keypress(function(e) {
    if(e.keyCode == 13) {
        document.forms["forma"].submit();
    }
});
/*
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
   document.getElementById("result").innerHTML=mygetrequest.responseText
  }
  else{
   
  }
 }
}
var chn=encodeURIComponent(document.getElementById("chname").value)
mygetrequest.open("GET", "channel.php?name="+chn, true)
mygetrequest.send(null)

}
function init()
{
getpost();
setTimeout("init()", 2000);
}
*/

    function getpost() {
        var xmlhttp;
        if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp = new XMLHttpRequest();
        }
        else {// code for IE6, IE5
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.onreadystatechange = function () {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                document.getElementById("result").innerHTML = xmlhttp.responseText;
            }
        }
        var chn = encodeURIComponent(document.getElementById("chname").value)
        xmlhttp.open("GET", "channel.php?name=" + chn, true)
        xmlhttp.send(null)
    }
function init()
{
getpost();
setTimeout("init()", 2000);
}

</script>

<div id="result">
</div>
</body>
</html>