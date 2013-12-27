package com.pangff.demoview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/**
 * @author pangff
 * MainActivity
 */
public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }

  /**
   * 按钮点击事件
   * @param view
   */
  public void onViewClick(View view) {
    String tag = (String) view.getTag();
    if (tag.equals("view")) {
      getNextPage(MyViewActivity.class);
    }else if (tag.equals("viewgroup")) {
      getNextPage(MyViewGroupActivity.class);
    }
  }
  
  /**
   * 页面跳转
   * @param classes
   */
  public void getNextPage(Class<?> classes){
    Intent intent = new Intent();
    intent.setClass(this, classes);
    startActivity(intent);
  }

}
