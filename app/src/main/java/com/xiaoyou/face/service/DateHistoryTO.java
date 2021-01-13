package com.xiaoyou.face.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lenyuqin
 * @data 2020/12/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateHistoryTO {
    private int year;
    private int month;
    private int day;
    private int isSign;
    private int unSign;
    private int isLate;
    private int isAsked;


}
