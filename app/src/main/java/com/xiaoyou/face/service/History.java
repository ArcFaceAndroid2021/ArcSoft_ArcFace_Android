package com.xiaoyou.face.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
    private LocalDate date;
    private int isSignUp;
    private int notSigUp;
    private int isLate;
    private int isAsk;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getIsSignUp() {
        return isSignUp;
    }

    public void setIsSignUp(int isSignUp) {
        this.isSignUp = isSignUp;
    }

    public int getNotSigUp() {
        return notSigUp;
    }

    public void setNotSigUp(int notSigUp) {
        this.notSigUp = notSigUp;
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
