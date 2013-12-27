package com.pangff.demoview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class MyViewActivity extends Activity{
  private MyView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.myview);
      text = (MyView) findViewById(R.id.text);
      text.setBackgroundColor(Color.RED);
      String str = "";
      for(int i=0;i<100;i++){
          str+=(""+i);
      }
      text.setText(str);
    }
  
}
