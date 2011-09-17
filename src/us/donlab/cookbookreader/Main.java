package us.donlab.cookbookreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setContentView(R.layout.main);
        Intent start = new Intent(this, Reader.class);
        startActivity(start);
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onPause(){
    	super.onPause();     
    }
    
    
}