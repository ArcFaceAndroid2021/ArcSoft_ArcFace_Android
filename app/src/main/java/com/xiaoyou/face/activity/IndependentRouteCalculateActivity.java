package com.xiaoyou.face.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNaviIndependentRouteListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapNaviPathGroup;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.NaviPoi;
import com.xiaoyou.face.R;

import java.util.ArrayList;
import java.util.List;


public class IndependentRouteCalculateActivity extends BasesActivity {

    private final String TAG = "IndependentRouteCalculate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basic_navi_independent);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        sList.clear();
        eList.clear();
        NaviLatLng mStartLatlng = new NaviLatLng(39.894914, 116.322062);
        NaviLatLng mEndLatlng = new NaviLatLng(39.903785, 116.423285);
        sList.add(mStartLatlng);
        eList.add(mEndLatlng);
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_DEFAULT);
    }


    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        super.onCalculateRouteSuccess(ints);
        mAMapNavi.startNavi(NaviType.EMULATOR);
    }
    AMapNaviPathGroup independentPathGroup = null;
    public void independentRouteCalculate(View view){
        NaviPoi start = new NaviPoi("起点", new LatLng(39.824722,116.455291), "");
        //途经点
        List<NaviPoi> poiList = new ArrayList();
        //终点
        NaviPoi end = new NaviPoi("终点", new LatLng(39.945834,116.462569), "");
        mAMapNavi.independentCalculateRoute(start, end, poiList, PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_DEFAULT, 1,new AMapNaviIndependentRouteListener(){

            @Override
            public void onIndependentCalculateSuccess(AMapNaviPathGroup aMapNaviPathGroup) {
                independentPathGroup = aMapNaviPathGroup;

                com.xiaoyou.face.activity.IndependentRouteCalculateActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(com.xiaoyou.face.activity.IndependentRouteCalculateActivity.this,"独立算路成功", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onIndependentCalculateFail(AMapCalcRouteResult aMapCalcRouteResult) {

            }
        });
    }
    public void seletctIndependentRoute(View view){
        if(independentPathGroup != null){
            independentPathGroup.selectRouteWithIndex(independentPathGroup.getPathCount()-1);
            mAMapNavi.stopNavi();
            mAMapNavi.startNaviWithPath(NaviType.EMULATOR,independentPathGroup);

            com.xiaoyou.face.activity.IndependentRouteCalculateActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(com.xiaoyou.face.activity.IndependentRouteCalculateActivity.this,"独立算路选路导航开始", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
