<? 
ini_set('session.cookie_domain',
substr($_SERVER['SERVER_NAME'],strpos($_SERVER['SERVER_NAME'],"."),100));
session_start();
session_destroy();
Header( "HTTP/1.1 301 Moved Permanently" ); 
Header( "Location: http://cryptum.net" ); 

echo "You have been logged out";
echo "<br>";
echo "Returning you to home";
sleep(3);

?> 
<HTML>
<META http-equiv="refresh"; url=http://cryptum.net/>
<HTML>
