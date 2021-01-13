package com.xiaoyou.face.model;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *  用户签到信息
 */
@Data
@AllArgsConstructor
@SmartTable()
public class Login {
    @SmartColumn(id =1,name = "学号")
    String no;
    @SmartColumn(id =2,name = "姓名")
    String name;
    @SmartColumn(id =3,name = "签到情况")
    String situation;
    @SmartColumn(id =4,name = "签到时间")
    String time;
    //位置信息
    @SmartColumn(id =5,name = "位置")
    String GPS_msg;
}
