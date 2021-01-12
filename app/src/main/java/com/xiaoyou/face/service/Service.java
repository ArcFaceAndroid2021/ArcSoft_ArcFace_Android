package com.xiaoyou.face.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface Service {
    /**
     * 学生注册
     *
     * @param registerInfo 学生注册信息
     * @return 插入条数
     */
    long studentRegister(RegisterInfo registerInfo);


    /**
     * 查询学生信息
     *
     * @param id 注册id
     * @return 学生id
     */
    RegisterInfo getStudentInfo(int id);


    /**
     * 首页签到部分,返回具体时间
     *
     * @return 返回已签到的日子
     */
    List<DateHistoryTO> getCalendar();


    /**
     * 签到统计部分
     *
     * @return 学号，姓名，签到时间
     */
    List<StudentInfoTO> getCountToday() throws ParseException;


    /**
     * 考勤情况统计
     *
     * @return 当天的签到信息
     */
    History getTodayHistory();


    /**
     * 考勤历史(默认返回今年的) 用于日历
     *
     * @return 返回一个历史数据list
     */
    List<DateHistoryTO> getHistory();


    /**
     * 查询
     *
     * @param stuId 学号
     * @param name  用户名
     * @return 学生信息的list
     */
    List<StudentInfoTO> queryStudentInfo(String stuId, String name) throws ParseException;



    /**
     * 签到
     *
     * @param stuId 学号
     * @param name  用户名
     * @param data  当前时间
     * @return 是否插入成功
     */
    Boolean signUp(String stuId, String name, LocalDateTime data);


    //to do
    /**
     *
     * @param stuId 学号
     * @param name  用户名
     * @param data  当前时间
     * @param gps_msg GPS信息
     * @return 是否插入成功
     */
    Boolean signUp(String stuId, String name, LocalDateTime data,String gps_msg);



    /**
     * 查询是否签到
     *
     * @param stuId 学生学号
     * @param data  当前时间
     * @return 是否签到
     */
    Boolean isSignUp(String stuId, LocalDateTime data);


    /**
     * 设置指定学生考勤记录为迟到
     *
     * @param stuId 学生学号
     *
     */

    /**
     * 将指定学生的考勤记录设置为迟到
     * @param stuId
     * @param data
     * @return 设置是否成功
     */
    boolean setIsLate(String stuId, LocalDateTime data);

    /**
     * 添加请假学生的考勤记录
     * @param stuId
     * @param name
     * @param data
     * @return
     */
    boolean addLeave(String stuId,String name,LocalDate data);

}
