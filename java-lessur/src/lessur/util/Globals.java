package lessur.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

public class Globals {
	
	public static String loginServerURL = "http://dev.cryptum.net/lessur/login.php";
	
	public static PublicKey getServerPublicKey(){
		try {
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(
					new BigInteger("8993054185765395520395570106438032857218863553573002872380178576442553654748766877047637620447425593551817260004526791382571089718226176923333389245906989"),
					new BigInteger("3001963698289200929503162479839827235890782056611145894824225407294897290916575952073823592749580178336682721672226528223497937079081949945220115511478849")
					);
		    KeyFactory fact = null;
			fact = KeyFactory.getInstance("RSA");
			return fact.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getHTML(String urlToRead) {
	      URL url;
	      HttpURLConnection conn;
	      BufferedReader rd;
	      String line;
	      String result = "";
	      try {
	         url = new URL(urlToRead);
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         while ((line = rd.readLine()) != null) {
	            result += line;
	         }
	         rd.close();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return result;
	   }
}
