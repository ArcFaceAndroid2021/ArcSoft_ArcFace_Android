package com.xiaoyou.face.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
    private String dbUrl="jdbc:mysql://39.107.245.110:3306/db_student?useUnicode=true&characterEncoding=utf8&useSSL=true"; // 数据库连接地址
    private String dbUserName="tangxushuo"; // 用户tangxueshuo
    private String dbPassword="SKWFHbKi7RkTSEE8"; // 密码
    private String jdbcName="com.mysql.jdbc.Driver"; // 驱动名称
    /**
     * 获取数据库连接
     * @return
     * @throws Exception
     */
    public Connection getCon(){
        try {
            Class.forName(jdbcName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Connection con = null;
        try {
            con = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return con;
    }
    /**
     * 关闭数据库连接
     * @param con
     * @throws Exception
     */
    public void closeCon(Connection con)throws Exception{
        if(con!=null){
            con.close();
        }
    }

    public static void main(String[] args) {
        DbUtil dbUtil=new DbUtil();
        try {
            dbUtil.getCon();
            System.out.println("数据库连接成功！");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("数据库连接失败?");
        }
    }
}
