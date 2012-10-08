<?
ini_set('session.cookie_domain',
substr($_SERVER['SERVER_NAME'],strpos($_SERVER['SERVER_NAME'],"."),100));
session_start();
if ($_SESSION['mobileEnabled'] = null)
{
$_SESSION['mobileEnabled'] = true;
}
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<script type="text/javascript" src="http://cryptum.net/weblib/TypingText.js"></script>

<meta name="google-site-verification" content="IVTpKFQzw8aJyulhZdOa7peg9_wi038hMd2LXjww9ok">


<link rel="shortcut icon" href="http://www.cryptum.net/favicon.ico">
<link rel="stylesheet" type="text/css" href="http://cryptum.net/weblib/mobile.css">
<meta name="google-site-verification" content="IVTpKFQzw8aJyulhZdOa7peg9_wi038hMd2LXjww9ok">
<title>Cryptum - <? print ($pagetitle); ?> </title> <!-- prints title -->

<META NAME="Title" CONTENT="<? echo "Cryptum -" . $pagetitle; ?>">
<META NAME="description" CONTENT="Cryptum.net is the development site of Cryptum Technologies and is owned by liamzebedee.">
<META NAME="keywords" CONTENT="cryptum cryptum-technologies cryptum.net liamzebedee Liam E-P CSL cryptum-scripting-language">
<META NAME="robot" CONTENT="index,follow">
<META NAME="copyright" CONTENT="Copyright 2010-2011 Cryptum Technologies. All Rights Reserved.">
<META NAME="author" CONTENT="liamzebedee">
<META NAME="generator" CONTENT="www.onlinemetatag.com">
<META NAME="language" CONTENT="English">
<META NAME="revisit-after" CONTENT="1">
 <meta name="viewport" content="width=device-width, height=device-height" />  

<!-- meta tags -->

</head>

<body>

<div id="logo">

<img src='http://cryptum.net/weblib/mobilelogo1.png' alt='Cryptum Logo' style="float:right">
 <!-- prints logo -->
<br><br>




</div>

<div id="globalDiv">
<?
if ($_SESSION['myusername'] != null) 
{
echo "You are logged in as <b>".$_SESSION['myusername']."</b>";
}
?>
<div id="content">