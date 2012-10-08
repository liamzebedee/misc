<?
ini_set('session.cookie_domain',
substr($_SERVER['SERVER_NAME'],strpos($_SERVER['SERVER_NAME'],"."),100));
session_start();
$isMobile = false;
$isBot = false;
$mobileEnabled = $_SESSION['mobileEnabled'];
if ($mobileEnabled = null)
{
$mobileEnabled = true;
}

$op = strtolower($_SERVER['HTTP_X_OPERAMINI_PHONE']);
$ua = strtolower($_SERVER['HTTP_USER_AGENT']);
$ac = strtolower($_SERVER['HTTP_ACCEPT']);
$ip = $_SERVER['REMOTE_ADDR'];

$isMobile = strpos($ac, 'application/vnd.wap.xhtml+xml') !== false
        || $op != ''
        || strpos($ua, 'sony') !== false 
        || strpos($ua, 'symbian') !== false 
        || strpos($ua, 'nokia') !== false 
        || strpos($ua, 'samsung') !== false 
        || strpos($ua, 'mobile') !== false
        || strpos($ua, 'windows ce') !== false
        || strpos($ua, 'epoc') !== false
        || strpos($ua, 'opera mini') !== false
        || strpos($ua, 'nitro') !== false
        || strpos($ua, 'j2me') !== false
        || strpos($ua, 'midp-') !== false
        || strpos($ua, 'cldc-') !== false
        || strpos($ua, 'netfront') !== false
        || strpos($ua, 'mot') !== false
        || strpos($ua, 'up.browser') !== false
        || strpos($ua, 'up.link') !== false
        || strpos($ua, 'audiovox') !== false
        || strpos($ua, 'blackberry') !== false
        || strpos($ua, 'ericsson,') !== false
        || strpos($ua, 'panasonic') !== false
        || strpos($ua, 'philips') !== false
        || strpos($ua, 'sanyo') !== false
        || strpos($ua, 'sharp') !== false
        || strpos($ua, 'sie-') !== false
        || strpos($ua, 'portalmmm') !== false
        || strpos($ua, 'blazer') !== false
        || strpos($ua, 'avantgo') !== false
        || strpos($ua, 'danger') !== false
        || strpos($ua, 'palm') !== false
        || strpos($ua, 'series60') !== false
        || strpos($ua, 'palmsource') !== false
        || strpos($ua, 'pocketpc') !== false
        || strpos($ua, 'smartphone') !== false
        || strpos($ua, 'rover') !== false
        || strpos($ua, 'ipaq') !== false
        || strpos($ua, 'au-mic,') !== false
        || strpos($ua, 'alcatel') !== false
        || strpos($ua, 'ericy') !== false
        || strpos($ua, 'up.link') !== false
        || strpos($ua, 'vodafone/') !== false
        || strpos($ua, 'wap1.') !== false
        || strpos($ua, 'wap2.') !== false;

        $isBot =  $ip == '66.249.65.39' 
        || strpos($ua, 'googlebot') !== false 
        || strpos($ua, 'mediapartners') !== false 
        || strpos($ua, 'yahooysmcm') !== false 
        || strpos($ua, 'baiduspider') !== false
        || strpos($ua, 'msnbot') !== false
        || strpos($ua, 'slurp') !== false
        || strpos($ua, 'ask') !== false
        || strpos($ua, 'teoma') !== false
        || strpos($ua, 'spider') !== false 
        || strpos($ua, 'heritrix') !== false 
        || strpos($ua, 'attentio') !== false 
        || strpos($ua, 'twiceler') !== false 
        || strpos($ua, 'irlbot') !== false 
        || strpos($ua, 'fast crawler') !== false                        
        || strpos($ua, 'fastmobilecrawl') !== false 
        || strpos($ua, 'jumpbot') !== false
        || strpos($ua, 'googlebot-mobile') !== false
        || strpos($ua, 'yahooseeker') !== false
        || strpos($ua, 'motionbot') !== false
        || strpos($ua, 'mediobot') !== false
        || strpos($ua, 'chtml generic') !== false
        || strpos($ua, 'nokia6230i/. fast crawler') !== false;

if($isMobile && $mobileEnabled){
   header('Location: http://m.cryptum.net/');
   exit();
}
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<script type="text/javascript" src="http://cryptum.net/weblib/TypingText.js"></script>

<meta name="google-site-verification" content="IVTpKFQzw8aJyulhZdOa7peg9_wi038hMd2LXjww9ok">


<link rel="shortcut icon" href="http://www.cryptum.net/favicon.ico">
<link rel="stylesheet" type="text/css" href="http://cryptum.net/weblib/default.css">
<meta name="google-site-verification" content="IVTpKFQzw8aJyulhZdOa7peg9_wi038hMd2LXjww9ok">
<title>Cryptum - <? print ($pagetitle); ?> </title> <!-- prints title -->
 <?
echo $headHTML;
?>
<META NAME="Title" CONTENT="<? echo "Cryptum -" . $pagetitle; ?>">
<META NAME="description" CONTENT="Cryptum.net is the development site of Cryptum Technologies and is owned by liamzebedee.">
<META NAME="keywords" CONTENT="cryptum cryptum-technologies cryptum.net liamzebedee Liam E-P CSL cryptum-scripting-language">
<META NAME="robot" CONTENT="index,follow">
<META NAME="copyright" CONTENT="Copyright � 2010-2011 Cryptum Technologies. All Rights Reserved.">
<META NAME="author" CONTENT="liamzebedee">
<META NAME="generator" CONTENT="www.onlinemetatag.com">
<META NAME="language" CONTENT="English">
<META NAME="revisit-after" CONTENT="1">
<!-- meta tags -->

<!-- dropdown menu -->

<script type="text/javascript" src="http://cryptum.net/weblib/menu.js">

</script>

<!-- end of scripts, time for the plain data -->



</head>

<body>



<div id="globalDiv">






<div id="logo">

<img src='http://cryptum.net/crylogo5.png' alt='Cryptum Logo'>
 <!-- prints logo -->
<br><br>




</div>
<div style="float:right;"></div>
<!-- Dropdown Menu Data -->
<ul id="sddm">

    <li><a href="#" 
        onmouseover="mopen('m4')" 
        onmouseout="mclosetime()">Uncatagorised</a>
        <div id="m4" 
            onmouseover="mcancelclosetime()" 
            onmouseout="mclosetime()">
        

<a href="http://cryptum.net/us.php">Cryptum- Us</a>
<a href="http://mcraft.cryptum.net/">Minecraft at Cryptum</a>
<a href="http://servers.minecraftforum.net/">Minecraft Alpha Server List</a>
<a href="http://mcraft.cryptum.net/me.php">Minecraft- Me</a>
</div></li>
<li><a onmouseover="mopen('m5')" onmouseout="mclosetime()" href="http://dev.cryptum.net/">Dev</a>
        <div id="m5" 
            onmouseover="mcancelclosetime()" 
            onmouseout="mclosetime()">
<a href="http://dev.cryptum.net/pwnarena/">Java Game Prototype in Slick2d/LWJGL</a>



        </div>
</li>
    
<li><a href="http://cryptum.net/submitnews.php">Submit News</a> 
    
      <div id="m8" 
            onmouseover="mcancelclosetime()" 
            onmouseout="mclosetime()">  
            </div>
            </li>
<li><a href="http://cryptum.net" 
        onmouseover="mopen('m1')" 
        onmouseout="mclosetime()">Home</a>
        <div id="m1" 
            onmouseover="mcancelclosetime()" 
            onmouseout="mclosetime()">
        </div>
    </li>
	<li><a href="http://portal.cryptum.net/"
onmouseover="mopen('m3')" 
        onmouseout="mclosetime()">User Portal</a>
        <div id="m3" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
<a href="http://portal.cryptum.net/proxy/">Proxy</a> 
        <a href="http://portal.cryptum.net/login/">Login</a> 
<a href="http://cryptum.net:2096/">Webmail</a></div>
    </li>
<li>
<a onmouseover="mopen('m7')" onmouseout="mclosetime()" href="#">Affiliates</a>
        <div id="m7" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
<a href="http://brezerd.net">Brezerd</a>

        </div>
    </li>
    <li><a href="http://cryptum.net/downloads/" 
        onmouseover="mopen('m2')" 
        onmouseout="mclosetime()">Downloads</a>
        <div id="m2" 
            onmouseover="mcancelclosetime()" 
            onmouseout="mclosetime()">
        
       <a href="http://cryptum.net/downloads/school">Schoolwork</a>
<a href="http://cryptum.net/downloads/logoarchives/">Logo Archives</a>
<a href="http://cryptum.net/downloads/hackedarchive/">Hacked Archives</a>
        </div>
    </li>
</ul>
<div style="clear:both"></div>
<?


if ($_SESSION['myusername'] != null) 
{
echo "You are logged in as <b>".$_SESSION['myusername']."</b>";
}

?>
<!-- END OF PAGE -->



<div id="leftbar">
<?
include("/home/cryptum1/public_html/sidebar.php")
?>
</div>





<!--
<script type="text/javascript">
//Define first typing example:
new TypingText(document.getElementById("example1"));

//Define second typing example (use "slashing" cursor at the end):
//new TypingText(document.getElementById("example2"), 100, function(i){ var ar = new Array("\\", "|", "/", "-"); return " " + ar[i.length % ar.length]; });

//Type out examples:
TypingText.runAll();
</script> -->
<br>
<div id="content">