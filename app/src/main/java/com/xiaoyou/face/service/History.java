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
}
