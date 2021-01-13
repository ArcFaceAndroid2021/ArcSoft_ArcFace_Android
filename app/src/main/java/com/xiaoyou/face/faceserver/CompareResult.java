package com.xiaoyou.face.faceserver;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 比对结果

 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompareResult {
    /**
     * id
     */
    private String id;
    /**
     * 姓名
     */
    private String userName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

//    public void setUserName(String userName) {
//        this.userName = userName;
//    }

    public String getUserNo() {
        return userNo;
    }

//    public void setUserNo(String userNo) {
//        this.userNo = userNo;
//    }

    public float getSimilar() {
        return similar;
    }

//    public void setSimilar(float similar) {
//        this.similar = similar;
//    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    /**
     * 学号
     */
    private String userNo;
    /**
     * 相似度
     */
    private float similar;
    /**
     * 追踪id
     */
    private int trackId;

    public CompareResult(String id,String stuId,String name, float similar) {
        this.id = id;
        this.userNo =stuId;
        this.userName =name;
        this.similar = similar;
    }
}
