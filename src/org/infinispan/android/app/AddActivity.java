package org.infinispan.android.app;

import java.io.Serializable;

import org.infinispan.android.app.model.ShopItem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class AddActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        
        Intent intent = getIntent();

        Serializable serializableItem = null;
        if (intent.getExtras() != null) {
        	serializableItem = intent.getExtras().getSerializable("item");
        }
        
        if (serializableItem != null) {
        	ShopItem shopItem = (ShopItem)serializableItem;
        	
        	TextView name = (TextView)findViewById(R.id.name);
            name.setText(shopItem.getName());
            
            TextView prize = (TextView)findViewById(R.id.prize);
            prize.setText(shopItem.getPrize() + "");
            
            TextView description = (TextView)findViewById(R.id.description);
            description.setText(shopItem.getDescription());
            
            TextView manufacturer = (TextView)findViewById(R.id.manufacturer);
            manufacturer.setText(shopItem.getManufacturer());
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
		
		TextView name = (TextView)findViewById(R.id.name);
		TextView prize = (TextView)findViewById(R.id.prize);
		TextView description = (TextView)findViewById(R.id.description);
		TextView manufacturer = (TextView)findViewById(R.id.manufacturer);
		
		ShopItem shopItem = new ShopItem(name.getText().toString(), 
				Float.parseFloat(prize.getText().toString()), 
				description.getText().toString(), 
				manufacturer.getText().toString());
		intent.putExtra("item", shopItem);
		setResult(RESULT_OK, intent);
		finish();
	}
}
