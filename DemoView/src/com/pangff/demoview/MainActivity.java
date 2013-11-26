package com.pangff.demoview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.VelocityTracker;

public class MainActivity extends Activity {

	private MyView text;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		text = (MyView) findViewById(R.id.text);
		text.setBackgroundColor(Color.RED);
		String str = "";
		for(int i=0;i<100;i++){
			str+=(""+i);
		}
		text.setText(str);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
