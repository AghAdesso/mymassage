package url;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class CallUrl {

	public static void main(String[] args) {
		if (args != null && args.length == 1) {
			try {
				URL myURL = new URL(args[0]);
				URLConnection myURLConnection = myURL.openConnection();
				myURLConnection.setDoOutput(true);
				OutputStreamWriter osw = new OutputStreamWriter(myURLConnection.getOutputStream());
				myURLConnection.getInputStream();
				osw.flush();
				osw.close();
				// myURL.openStream();
				System.out.println("L'appel à l'URL : " + args[0] + " est OK.");
			} catch (Exception e) {
				System.out.println(e);
			}
		} else {
			System.out.println("Erreur : il n'y a pas d'arguments dans l'appel de 'CallUrl'.");
		}
	}

}
