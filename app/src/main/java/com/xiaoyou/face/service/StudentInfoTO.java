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
}
