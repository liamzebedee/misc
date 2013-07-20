<?
$headHTML = "<script type=\"text/javascript\" src=\"http://cryptum.net/weblib/bbeditor/bbeditor/ed.js\"></script>";
include ("/home/cryptum1/public_html/weblib/template.php");
?>




<form action="post_create.php" method="POST">
Title: <input type="text" name="title" /><br />
Author: <select name=author>
  
<?
if ($_SESSION['myusername'] != null)
{
echo "<option>".$_SESSION['myusername']."</option>";
}
?>
<option>Annonymous</option>
</select><br>
Topic: <select name=topic>
  <option>Empty</option>

<option>Website Update</option>

</select>
<br>
Text:
<br>


<br>
<script>edToolbar('text'); </script>
<p>
You can also use the [br] tag for a linebreak<br>
<textarea name="text" cols="100" rows="20" id="text" class="ed">
</textarea><p><br>





<?
if ($_SESSION['myusername'] == null) {
require_once('/home/cryptum1/public_html/weblib/recaptchalib.php');
  $publickey = ""; // you got this from the signup page
  echo recaptcha_get_html($publickey);
}
?>

<P>
<input type="submit" value="Submit" />
</form>
This website uses HTML tag blocking techniques and a BBCode-like syntax

<? include("/home/cryptum1/public_html/weblib/footer.php"); ?>