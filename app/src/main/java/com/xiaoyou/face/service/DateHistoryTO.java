package com.xiaoyou.face.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateHistoryTO {
    private int year;
    private int day;
    private int month;
    private int isSign;
    private int unSign;
    private int isLate;
    private int isAsk;


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getIsSign() {
        return isSign;
    }

    public void setIsSign(int isSign) {
        this.isSign = isSign;
    }

    public int getUnSign() {
        return unSign;
    }

    public void setUnSign(int unSign) {
        this.unSign = unSign;
    }

    public int getIsLate() {
        return isLate;
    }

    public void setIsLate(int isLate) {
        this.isLate = isLate;
    }

    public int getIsAsk() {
        return isAsk;
    }

    public void setIsAsk(int isAsk) {
        this.isAsk = isAsk;
    }
}
