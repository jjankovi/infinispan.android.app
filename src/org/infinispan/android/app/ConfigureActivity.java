package org.infinispan.android.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ConfigureActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_configure, menu);
        return true;
    }
}
