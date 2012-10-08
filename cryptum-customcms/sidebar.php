<?
function linka($name,$link)
{
return "<a href=".$link.">".$name."</a><br>";
}

$sidebaritems = "<br>".linka("Post List","post_list.php")."<font color=green>The website is being edited currently.</font><br>"."Cryptum was created by Liam Edwards-Playne, a 13yr old in Australia-";

echo "<div id=sidebar>";
echo "<b>Sidebar (in developement)</b><br><br>";
echo "<b>".$pagetitle."</b>";
echo $sidebaritems;

?>
<br />Sites we follow<br />
<a href="http://slashdot.org/">Slashdot</a> <br/>
<a href="http://notch.tumblr.com/">Notch's Minecraft Blog</a><br/>
<a href="http://www.webtrickz.com">Webtrickz</a><br/>
<a href="http://blog.iphone-dev.org/">Iphone DevTeam</a><br/>
<a href="http://giveawayoftheday.com/">giveawayoftheday</a><br/>
<a href="http://viruswriting.blogspot.com">viruswritingblog</a><br/>
<a href="http://trixoo.com">Trixoo Tech Blog</a><br/>
<a href="http://www.ghacks.net">Ghacks tech blog</a><br/>
<a href="http://gohacking.com">Ethical Hacking tutorials</a><br/>
<a href="http://i-hacked.com/">Hacking Blog</a><br/>
<a href="http://hackaday.com">Hardware hacking fanatics</a><br/>
<a href="http://lifehacker.com">Lifehacker: General tech + hacking news</a><br/>
<a href="http://www.80vul.com/">Newest security vunrabilities</a><br />
<br><br><br>

</div><br>
