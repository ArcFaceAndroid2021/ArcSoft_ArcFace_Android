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
}
