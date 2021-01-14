package com.xiaoyou.face.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.xiaoyou.face.R;
import com.xiaoyou.face.activity.IndexActivity;
import com.xiaoyou.face.activity.RegisterAndRecognizeActivity;
import com.xiaoyou.face.activity.SignDetailActivity;
import com.xiaoyou.face.adapter.FunctionAdapter;
import com.xiaoyou.face.databinding.FragmentIndexBinding;
import com.xiaoyou.face.model.Channel;
import com.xiaoyou.face.service.DateHistoryTO;
import com.xiaoyou.face.service.SQLiteHelper;
import com.xiaoyou.face.service.Service;
import com.xiaoyou.face.utils.ToastUtils;
import com.xiaoyou.face.utils.Tools;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打卡的fragment类
 */
public class IndexFragment extends Fragment implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener{

    /**
     * 日历组件显示的时间map值
     */
    Map<String, Calendar> timeMap;

    private FragmentIndexBinding binding;
    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    TextView mTextCurrentDay;
    CalendarView mCalendarView;
    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;

    private final static int REQUEST_CODE = 45;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 这里我们使用bindview 来进行视图绑定
        binding = FragmentIndexBinding.inflate(inflater, container, false);
        // 我们这里给控件赋值
        // 月份显示
        mTextMonthDay = binding.tvMonthDay;
        // 年份显示
        mTextYear = binding.tvYear;
        // 时间显示
        mTextLunar = binding.tvLunar;
        // 现在时间
        mTextCurrentDay = binding.tvCurrentDay;
        // 日历视图组件
        mCalendarView = binding.calendarView;
        // 时间显示控件
        mRelativeTool = binding.rlTool;
        // 日历布局
        mCalendarLayout = binding.calendarLayout;
        // 数据初始化
        initView();
        initData();
        // 宫格布局初始化
        // 9宫格布局初始化
        ArrayList<Channel> channelList = new ArrayList<>();
        channelList.add(new Channel(R.mipmap.face,"人脸录入"));
        channelList.add(new Channel(R.mipmap.login,"开始签到"));
        channelList.add(new Channel(R.mipmap.statistics,"签到详情"));
        channelList.add(new Channel(R.mipmap.maps,"教室导航"));
        channelList.add(new Channel(R.mipmap.leave_request,"请假"));
        channelList.add(new Channel(R.mipmap.remind,"提醒"));
        binding.toolList.setAdapter(new FunctionAdapter(channelList,getContext()));
        // grad 布局点击事件监听
        binding.toolList.setOnItemClickListener((parent, view, position, id) -> {
            switch (position){
                case 0:
                    startActivity(new Intent(getContext(), RegisterAndRecognizeActivity.class));
                    break;
                case 1:
                    // 签到不需要显示录入按钮
                    Intent intent = new Intent(getContext(), RegisterAndRecognizeActivity.class);
                    intent.putExtra("login",false);
                    startActivity(intent);
                    break;
                case 2:
                    startActivity(new Intent(getContext(), SignDetailActivity.class));
                    break;
                case 3:
                    //to do
                    //教室导航
                    startActivity(new Intent(getContext(), IndexActivity.class));
                    break;
                case 4:
                    //to do
                    //请假
                    //新增一个对话框
                    new MaterialDialog.Builder(getContext())
                            .customView(R.layout.dialog_input, true)
                            .title("请假信息录入")
                            .positiveText("确认请假")
                            .onPositive((dialog, which) -> {
                                // 点击录入的时的点击事件
                                MaterialEditText no = dialog.findViewById(R.id.input_no);//学号信息
                                MaterialEditText name = dialog.findViewById(R.id.input_name);//姓名


                                Service sqLiteHelper = new SQLiteHelper(getContext());
                                //绑定SQLite对应的请假函数
                                try{
                                    if(sqLiteHelper.addLeave(no.getEditValue(),name.getEditValue())){
                                        ToastUtils.info("请假成功!请前往签到详情查看");
                                    } else {
                                        ToastUtils.info("请假失败!请检查是否已请假或者信息错误");
                                    }
                                    System.out.println("Student id :"+no.getEditValue()+"    Student name is :"+name.getEditValue());
                                } catch (ParseException e){
                                    System.out.println("error!");
                                }

                            }).show();
                    break;
                case 5:
                    //to do
                    //提醒
                    ToastUtils.info("没有权限!请前往服务器进行操作");
                    //打开另一个APP
//                    try{
//                        //startActivity(new Intent(getContext(), JPushMainActivity.class));
//                        String packageName = "com.xuexiang.jpushsample";
//                        String activity = "com.xuexiang.jpushsample.MainFragment";
//                        ComponentName component = new ComponentName(packageName,activity);
//                        Intent pushIntent = new Intent();
//                        if(isInstallByread("com.xuexiang.jpushsample")){
//                            pushIntent.setComponent(component);
//                            startActivity(pushIntent);
//                        }  else{
//                            System.out.println("error!");
//                        }
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
                    break;
                default:
                    break;
            }
        });
        // 返回视图view
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
    /**
     *  日历视图点击事件
      * @param calendar 日历控件
     * @param isClick 是否点击
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        // 显示时间控件
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);

        // 我们这里修改导航栏的时间显示
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
    }

    @Override
    public void onYearChange(int year) {
        // 年份视图点击的时候修改顶部的时间
        mTextMonthDay.setText(String.valueOf(year));
    }

    /**
     * 设置标记
     * @param year 年份
     * @param month 月份
     * @param day 日
     * @param color 颜色
     * @return calendar对象
     */
    private Calendar getSchemeCalendar(int year, int month, int day, int color) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        // 如果单独标记颜色、则会使用这个颜色
        calendar.setSchemeColor(color);
        return calendar;
    }


    /**
     * 视图初始化
      */
    @SuppressLint("SetTextI18n")
    protected void initView() {
        // 点击月份显示月份切换
        mTextMonthDay.setOnClickListener(v -> {
            if (!mCalendarLayout.isExpand()) {
                mCalendarLayout.expand();
                return;
            }
            // 显示月份
            mCalendarView.showYearSelectLayout(mYear);
            // 隐藏隐藏年份和时间
            mTextLunar.setVisibility(View.GONE);
            mTextYear.setVisibility(View.GONE);
            // 月份哪里显示时间
            mTextMonthDay.setText(String.valueOf(mYear));
        });
        // 点击今天就直接跳转到今天
        binding.flCurrent.setOnClickListener(v -> mCalendarView.scrollToCurrent());

        // 日历点击事件(包括年份点击)
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);

        // 这里我们显示当前时间到顶部导航栏
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        // 显示今天日期
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
    }


    /**
     * 日历初始化
     */
    private void initData(){
//        int year = mCalendarView.getCurYear();
//        int month = mCalendarView.getCurMonth();
        Service service = new SQLiteHelper(getContext());
        List<DateHistoryTO> calendar = service.getCalendar();
        timeMap = new HashMap<>();
        // 添加时间标记
        for (DateHistoryTO history : calendar) {
            addMark(history.getYear(),history.getMonth(),history.getDay());
        }
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(timeMap);
    }

    /**
     * 添加时间标记
     */
    private void addMark(int year, int month, int day){
       timeMap.put(getSchemeCalendar(year, month, day, Tools.getRandomColor()).toString(),
                getSchemeCalendar(year, month, day, Tools.getRandomColor()));
    }
}