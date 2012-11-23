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
        
        Intent intent = getIntent();
        String value1 = intent.getStringExtra("value1");
        if (value1!=null) {
        	EditText textValue1 = (EditText)findViewById(R.id.value1);
        	textValue1.setText(value1);
        }
        String value2 = intent.getStringExtra("value2");
        if (value2!=null) {
        	EditText textValue2 = (EditText)findViewById(R.id.value2);
        	textValue2.setText(value2);
        }
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
	public void save(View view) {
		Intent intent = getIntent();
		intent.putExtra("value1", ((EditText)
				findViewById(R.id.value1)).getText().toString());
		intent.putExtra("value2", ((EditText)
				findViewById(R.id.value2)).getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}
}
