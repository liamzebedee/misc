<?php

// maximum execution time in seconds
set_time_limit (24 * 60 * 60);
function find_version_longa()
{
ob_start();
include_once("http://www.minecraft.net/game/getversion.jsp?user=liamzebedee&password=liam&version=999");
$html = ob_get_contents();
ob_end_clean();
$array = explode(":",$html);
$versionfull = $array[0];
return $versionfull;
}
/* creates a compressed zip file */
function create_zip($files = array(),$destination = '',$overwrite = false) {
	//if the zip file already exists and overwrite is false, return false
	if(file_exists($destination) && !$overwrite) { return false; }
	//vars
	$valid_files = array();
	//if files were passed in...
	if(is_array($files)) {
		//cycle through each file
		foreach($files as $file) {
			//make sure the file exists
			if(file_exists($file)) {
				$valid_files[] = $file;
			}
		}
	}
	//if we have good files...
	if(count($valid_files)) {
		//create the archive
		$zip = new ZipArchive();
		if($zip->open($destination,$overwrite ? ZIPARCHIVE::OVERWRITE : ZIPARCHIVE::CREATE) !== true) {
			return false;
		}
		//add the files
		foreach($valid_files as $file) {
			$zip->addFile($file,$file);
		}
		//debug
		//echo 'The zip archive contains ',$zip->numFiles,' files with a status of ',$zip->status;
		
		//close the zip — done!
		$zip->close();
		
		//check to make sure the file exists
		return file_exists($destination);
	}
	else
	{
		return false;
	}
}

function downloadfile($url,$pathtosave)
{
// folder to save downloaded files to. must end with slash
$newfname = $pathtosave . basename($url);

$file = fopen ($url, "rb");
if ($file) {
  $newf = fopen ($newfname, "wb");

  if ($newf)
  while(!feof($file)) {
    fwrite($newf, fread($file, 1024 * 8 ), 1024 * 8 );
    
  }
}

if ($file) {
  fclose($file);
}

if ($newf) {
  fclose($newf);
}
}

/*
$os = "windows";
$files_to_zip = array(
	$os."/.minecraft/bin/minecraft.jar",
	$os.'/.minecraft/bin/jinput.jar',
	$os.'/.minecraft/bin/lwjgl.jar',
	$os.'/.minecraft/bin/lwjgl_util.jar',
	$os.'/.minecraft/bin/natives/jinput-dx8.dll',
	$os.'/.minecraft/bin/natives/jinput-dx8_64.dll',
	$os.'/.minecraft/bin/natives/jinput-raw.dll',
	$os.'/.minecraft/bin/natives/jinput-raw_64.dll',
	$os.'/.minecraft/bin/natives/lwjgl.dll',
	$os.'/.minecraft/bin/natives/lwjgl64.dll',
	$os.'/.minecraft/bin/natives/OpenAL32.dll',
	$os.'/.minecraft/bin/natives/OpenAL64.dll',
);
//if true, good; if false, zip creation failed
$result = create_zip($files_to_zip,'dotminecraft.zip');
*/
$long = find_version_longa();

mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long, 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/linux/", 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/linux/" . ".minecraft/", 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/linux/" . ".minecraft/" . "bin/", 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/linux/" . ".minecraft/" . "bin/" . "natives", 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/mac/", 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/mac/" . ".minecraft/", 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/mac/" . ".minecraft/" . "bin/", 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/mac/" . ".minecraft/" . "bin/" . "natives", 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/windows/", 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/windows/" . ".minecraft/", 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/windows/" . ".minecraft/" . "bin/" , 0755);
mkdir("/home/cryptum1/public_html/mcraft/downloads/".$long . "/windows/" . ".minecraft/" . "bin/". "natives", 0755);

$dir = "/home/cryptum1/public_html/mcraft/downloads/".$long;
$launcherlinux = "http://www.minecraft.net/download/Minecraft.jar";
$launchermac = "http://www.minecraft.net/download/Minecraft.zip";
$launcherwindows = "http://www.minecraft.net/download/Minecraft.exe";
$locals = array
(
"http://s3.amazonaws.com/MinecraftDownload/lwjgl.jar",
"http://s3.amazonaws.com/MinecraftDownload/jinput.jar",
"http://s3.amazonaws.com/MinecraftDownload/lwjgl_util.jar",
"http://s3.amazonaws.com/MinecraftDownload/minecraft.jar",
);

$os = array("/windows","/mac","/linux");

downloadfile($launcherlinux,$dir."/linux/");
downloadfile($launchermac,$dir."/mac/");
downloadfile($launcherwindows,$dir."/windows/");
foreach($os as $o)
{

foreach($locals as $link)
{
downloadfile($link,$dir . $o . "/.mincraft/bin/");
}

}
?>