package com.xiaoyou.face.activity;

import android.os.Bundle;

import com.amap.api.navi.AMapNaviView;
import com.xiaoyou.face.R;


public class BasicNaviActivity extends BasesActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basic_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
    }


}
