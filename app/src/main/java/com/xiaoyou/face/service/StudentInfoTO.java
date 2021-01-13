package com.xiaoyou.face.service;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentInfoTO {
    //学生学号
    private String stuId;
    //学生名字
    private String name;
    //打卡时间
    private Date dateTime;
    //签到情况
    private String situation;
    //位置信息
    private  String GPS_msg;


    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getGPS_msg() {
        return GPS_msg;
    }

    public void setGPS_msg(String GPS_msg) {
        this.GPS_msg = GPS_msg;
    }




    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
