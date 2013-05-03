package invoices.manager.dialog;

import invoices.manager.activity.R;
import invoices.manager.file.FileReaderHelper;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

/**
 * AboutDialog provides an information about application in
 * user friendly style
 * 
 * @author jjankovi
 *
 */
public class AboutDialog extends Dialog {

	private static Context context;

	/**
	 * Constructs a predefined dialog with given context 
	 * 
	 * @param 		context
	 */
	public AboutDialog(Context context) {
		super(context);
		AboutDialog.context = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		
		TextView tv = (TextView)findViewById(R.id.info_text);
		tv.setText(Html.fromHtml(FileReaderHelper.readRawTextFile(context, R.raw.info)));
		
		tv = (TextView)findViewById(R.id.description_text);
		tv.setText(FileReaderHelper.readRawTextFile(context, R.raw.description));
		
	}

}
