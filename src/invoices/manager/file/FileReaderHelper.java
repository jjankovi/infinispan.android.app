package invoices.manager.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

/**
 * FileReaderHelper helps with Android file reading operation 
 * 
 * @author jjankovi
 *
 */
public class FileReaderHelper {

	/**
	 * From given context and resource id, a raw text is read.
	 * 
	 * @param 		context
	 * @param 		id
	 * @return		text of resource
	 */
	public static String readRawTextFile(Context context, int id) {
		InputStream inputStream = context.getResources().openRawResource(id);
		InputStreamReader in = new InputStreamReader(inputStream);
		BufferedReader buf = new BufferedReader(in);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while ((line = buf.readLine()) != null)
				text.append(line);
		} catch (IOException e) {
			return null;
		}
		return text.toString();
	}
	
}
