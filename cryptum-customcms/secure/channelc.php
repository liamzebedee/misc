<p>
Channel C is a new channel I will be introducing. It will be a file sharing channel.
This is how it will work
</p>
<br><br><br>

<ol>
Server<br>
<li>File Uploaded By User
<li>File is transferred from a temporary directory to a directory outside the web root
<li>File is renamed to random number
<li>Filename is transferred to a database. Original filename and new filename are both stored
<br>
<br>
Client<br>
<li>List of original filenames is outputted from database
<li>User clicks on file link, sends a request with parameters to a php file
<li>PHP file performs a lookup in the database to check if a file with that (original) filename exists
<li>If it does exist, PHP looks up the (original) filename in the database and finds the real filename and requests the file for download by the user
</ol>
<br>
<br>
<p>
Things I'm not sure about are the way I'm going to store the data. An alternate way to store the 
images is actually store their binary data in the database as the datatype VARBINARY. Its really
just the to BLOB or not to BLOB question. Its really the matter of how many files it will store
in the database. 
</p>
