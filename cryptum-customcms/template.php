<? 


/*
//setting the iPhone / iPod browser variables
$ipod = stripos($_SERVER['HTTP_USER_AGENT'],"iPod");
$iphone = stripos($_SERVER['HTTP_USER_AGENT'],"iPhone");
//detecting device browser
if ($ipod == true || $iphone == true){
header( 'Location: http://m.cryptum.net' ) ;
}
*/
ini_set('session.cookie_domain',
substr($_SERVER['SERVER_NAME'],strpos($_SERVER['SERVER_NAME'],"."),100));
session_start();


?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">


<head>
<script type="text/javascript" src="weblib/TypingText.js">
</script>
<link rel="alternate" type="application/rss+xml" href="http://cryptum.net/rss.xml" title="BLANK RSS FEED">
<meta name="google-site-verification" content="IVTpKFQzw8aJyulhZdOa7peg9_wi038hMd2LXjww9ok">
<?
echo $headHTML;
?>

<link rel="shortcut icon" href="http://www.cryptum.net/favicon.ico">
<link rel="stylesheet" type="text/css" href="http://cryptum.net/css/default.css">
<meta name="google-site-verification" content="IVTpKFQzw8aJyulhZdOa7peg9_wi038hMd2LXjww9ok">
<title>Cryptum - <? print ($pagetitle); ?> </title> <!-- prints title -->
<META NAME="Title" CONTENT="<? echo "Cryptum -" . $pagetitle; ?>">
<META NAME="description" CONTENT="Cryptum.net is liamzebedee's personal website for developement.">
<META NAME="keywords" CONTENT="cryptum cryptum-technologies cryptum.net liamzebedee Liam E-P CSL cryptum-scripting-language">
<META NAME="robot" CONTENT="index,follow">
<META NAME="copyright" CONTENT="Copyright � 2010 Cryptum Technologies. All Rights Reserved.">
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




<!-- Kontera ContentLink(TM);-->
<script type='text/javascript'>
var dc_AdLinkColor = 'blue' ; 
var dc_PublisherID = 167159 ; 
</script>
<script type='text/javascript' src='http://kona.kontera.com/javascript/lib/KonaLibInline.js'>
</script>

<div id="logo">

<img src='http://cryptum.net/crylogo4.png' width='1240' height='117' alt='Cryptum Logo'>
 <!-- prints logo -->




</div>
<div style="float:right;"></div>
<!-- Dropdown Menu Data -->
<ul id="sddm">

    <li><a href="#" 
        onmouseover="mopen('m4')" 
        onmouseout="mclosetime()">Misc</a>
        <div id="m4" 
            onmouseover="mcancelclosetime()" 
            onmouseout="mclosetime()">
        
        <a href="http://csl.cryptum.net/">The Cryptum Scripting Language</a>
        <a href="http://cryptum.net/us.php">Cryptum- What we are</a>
		
		<a href="http://mcraft.cryptum.net/">Minecraft at Cryptum</a>
		<a href="http://servers.minecraftforum.net/">Minecraft Alpha Server List</a>
 <a href="http://cryptum.net/checkmcv.php">Script to check newest minecraft version</a>
<a href="http://mcraft.cryptum.net/downloads">Minecraft Downloads</a>
<a href="http://mcraft.cryptum.net/me.php">Minecraft- Me</a>
</div></li>
<li><a onmouseover="mopen('m5')" onmouseout="mclosetime()" href="http://dev.cryptum.net/">Dev</a>
        <div id="m5" 
            onmouseover="mcancelclosetime()" 
            onmouseout="mclosetime()">
<a href="http://servers.minecraftforum.net/">PHP Minecraft Update Checker(page not made yet)</a>
<a href="http://servers.minecraftforum.net/">C# Remote Botnet(page not made yet)</a>
<a href="http://servers.minecraftforum.net/">Perl Translator for custom formats(page not made yet)</a>
<a href="http://dev.cryptum.net/crytech.php">Crytech Content Management System(page not made yet)</a>

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
	<li><a href="#"
onmouseover="mopen('m3')" 
        onmouseout="mclosetime()">User</a>
        <div id="m3" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
<a href="http://cryptum.net/proxy">Proxy</a> 
        <a href="http://login.cryptum.net">Login</a> 
<a href="http://cryptum.net:2096/">Webmail</a></div>
    </li>
<li>
<a onmouseover="mopen('m7')" onmouseout="mclosetime()" href="#">Affiliates</a>
        <div id="m7" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
<a href="http://brezerd.net">Brezerd</a>
<a href="http://tozzard.net">Tozzard</a>

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

session_start(); 
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
<div id="Content">
