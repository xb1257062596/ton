package com.example.a123.tongxunlu;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import CallFragmentPac.CallFragment;
import CallFragmentPac.InComingService;
import LianFragmentPac.LianFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,LianFragment.ChuanshuListener{
    private TextView callText;
    private TextView lianText;
    private TextView huangText;
    private Fragment callFragment,lianFragment,huangFragment;
    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private FragmentPagerAdapter mAdapter;
    private String str=null;
    private int str1=-1;
    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState=savedInstanceState;
        super.onCreate(savedInstanceState);
       if(getSupportActionBar()!=null){
           getSupportActionBar().hide();
       }
        setContentView(R.layout.activity_main);
        Intent intent= new Intent(MainActivity.this, InComingService.class);
        startService(intent);

        initView();

        eventView();
    }

    /**
     * @param intent 当从其他Activity返回到这个Activity时调用
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);         //更新Intent
        Intent intent1=getIntent();
        try{
           str=intent1.getStringExtra("姓名");
            str1=intent1.getIntExtra("位置",-1);

        }catch (Exception e){

        }
        try{
            if(str1==-1){
                str=intent1.getStringExtra("姓名");
            }
        }catch (Exception e){
            Log.d("test", e.getMessage());
        }finally {
            lianFragment.onActivityCreated(savedInstanceState);
        }

    }

    /**
     * 添加事件
     */
    private void eventView() {
        callText.setOnClickListener(this);

        lianText.setOnClickListener(this);

        huangText.setOnClickListener(this);

        mViewPager.setAdapter(mAdapter);

      mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

          }

          @Override
          public void onPageSelected(int position) {
              resert();
              switch (position){
                  case 0:
                      callText.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.black));
                      break;
                  case 1:
                      lianText.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.black));
                      break;
                  case 2:
                      huangText.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.black));
                      break;
              }

          }

          @Override
          public void onPageScrollStateChanged(int state) {

          }
      });
    }

    /**
     * 初始化数据
     */
    private void initView() {
        mFragments= new ArrayList<>();

        callText=(TextView)this.findViewById(R.id.callText);

        lianText=(TextView)this.findViewById(R.id.lianText);

        huangText=(TextView)this.findViewById(R.id.huangText);

        mViewPager=(ViewPager)this.findViewById(R.id.mViewPager);

        callFragment = new CallFragment();

        lianFragment=new LianFragment();

        huangFragment = new HuangFragment();

        mFragments.add(callFragment);

        mFragments.add(lianFragment);

        mFragments.add(huangFragment);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };

        callText.setTextColor(ContextCompat.getColor(this,R.color.black));
        mViewPager.setCurrentItem(0);
    }


    /**
     * @param view
     * 改变按钮颜色 在改变Fragment
     */
    @Override
    public void onClick(View view) {
        resert();
        switch(view.getId()){
            case R.id.callText:
                callText.setTextColor(ContextCompat.getColor(this,R.color.black));
                mViewPager.setCurrentItem(0);
                break;
            case R.id.lianText:
                lianText.setTextColor(ContextCompat.getColor(this,R.color.black));
                mViewPager.setCurrentItem(1);
                break;
            case R.id.huangText:
                huangText.setTextColor(ContextCompat.getColor(this,R.color.black));
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    /**
     * 先把所有颜色全变成黑色
     */
    private void resert() {
        callText.setTextColor(ContextCompat.getColor(this,R.color.white));
        lianText.setTextColor(ContextCompat.getColor(this,R.color.white));
        huangText.setTextColor(ContextCompat.getColor(this,R.color.white));
    }

    /**
     * @return  用来与LianFragment通信
     */
    @Override
    public String chuanshu() {

        return str;
    }

    @Override
    public int flag() {
        return str1;
    }
}
