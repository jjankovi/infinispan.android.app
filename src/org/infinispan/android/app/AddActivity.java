package org.infinispan.android.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class AddActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add, menu);
        return true;
    }
	
	/** Called when the cancel button is pressed -> go back to main activity **/
	public void cancel(View view) {
		finish();
	}
	
	/** Called when the add button is pressed **/
	public void add(View view) {
		Intent intent = getIntent();
		intent.putExtra("value1", ((EditText)
				findViewById(R.id.value1)).getText().toString());
		intent.putExtra("value2", ((EditText)
				findViewById(R.id.value2)).getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}
}
