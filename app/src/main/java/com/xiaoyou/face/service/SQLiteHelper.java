package com.xiaoyou.face.service;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.os.Handler;
import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Hu JiAo
 */
public class SQLiteHelper extends SQLiteOpenHelper implements Service {
    private final static String DATABASE_NAME = "FaceCheck";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_ATTENDANCE = "attendance";
    private final static String TABLE_STUDENT = "student";

    private int staticAsked = 0;
    private int isLate = 0;
    private int is_Sign = 0;
    private String url="jdbc:mysql://39.107.245.110:3306/FaceCheck?useUnicode=true&characterEncoding=utf-8&useSSL=true";
    private String password = "53enjhhnhjRy7ewG";
    private String username="FaceCheck";

    //创建数据库，里面添加了3个参数，分别是：Msgone VARCHAR类型，30长度当然这了可以自定义
    //Msgtwo VARCHAR(20)   Msgthree VARCHAR(30))  NOT NULL不能为空
    //is_Sign 是否签到
    //新增is_Late 是否迟到、is_Asked 是否请假、gps_msg 位置信息
    String sql = "CREATE TABLE attendance (stu_id int(11) NOT NULL ," +
            "  name varchar(20) DEFAULT NULL ," +
            "  is_Sign bit(1) DEFAULT 0 ," +
            "  day int(5) DEFAULT NULL ," +
            "  date date  DEFAULT NULL ," +
            "  month int(5) DEFAULT NULL ," +
            "  year int(5) DEFAULT NULL ," +
            "  is_Asked bit(1) DEFAULT NULL , " +
            "  is_Late bit(1) DEFAULT NULL ," +
            "  gps_msg varchar(255) DEFAULT NULL)";

    String createStudent = "CREATE TABLE student (" +
            "  id int(11) NOT NULL," +
            "  stu_id varchar(20) NOT NULL," +
            "  name varchar(20) DEFAULT NULL," +
            "  PRIMARY KEY (id))";

    //构造函数，创建数据库
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    //建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
        db.execSQL(createStudent);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_ATTENDANCE;
        String sql2 = "DROP TABLE IF EXISTS " + TABLE_STUDENT;
        db.execSQL(sql);
        db.execSQL(sql2);
        onCreate(db);
    }


    //查询本月的数据，查询一个字段，返回日子的int集合 根据月去查询去除相同日子，返回日子
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Integer> queryByMouth() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[1];
        selectionArgs[1] = String.valueOf(LocalDate.now().getMonthValue());
        Cursor cursor = db.rawQuery("SELECT DISTINCT day FROM " + TABLE_ATTENDANCE + " WHERE month = ?", selectionArgs);
        ArrayList<Integer> result = new ArrayList<>();
        while (cursor.isLast()) {
            String columnName = cursor.getColumnName(1);
            result.add(Integer.valueOf(columnName));
            cursor.moveToNext();
        }
        return result;
    }


    /**
     * 学生注册
     *
     * @param registerInfo 学生注册信息
     * @return 插入条数
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public long studentRegister(RegisterInfo registerInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", registerInfo.getId());
        cv.put("stu_id", registerInfo.getStuId());
        cv.put("name", registerInfo.getName());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection cn = DriverManager.getConnection(url,username,password);
                    System.out.println("学生注册信息连接数据库成功");
                    Statement st = cn.createStatement();
                    String sql ="insert into student values( '"+registerInfo.getId()+"','"+registerInfo.getStuId()+"','"+registerInfo.getName()+"')";
                    System.out.println(sql);
                    st.execute(sql);
                    System.out.println("插入成功");
                } catch (ClassNotFoundException | SQLException e) {
                    System.out.println("插入失败");
                    e.printStackTrace();
                }
            }
        }).start();




        return db.insert(TABLE_STUDENT, null, cv);
    }

    /**
     * 查询学生信息
     *
     * @param id 注册id
     * @return 学生id
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public RegisterInfo getStudentInfo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENT, null, "id=?", new String[]{"" + id}, null, null, null);
        RegisterInfo registerInfo = new RegisterInfo();
        if (cursor.getCount() != 0) {
            if (cursor.moveToNext()) {
                registerInfo.setId(cursor.getInt(0));
                registerInfo.setStuId(cursor.getString(1));
                registerInfo.setName(cursor.getString(2));
            }
        }
        return registerInfo;
    }


    /**
     * 首页签到部分,返回具体时间
     * @return 返回已签到的日子
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<DateHistoryTO> getCalendar() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[1];
        selectionArgs[0] = String.valueOf(LocalDate.now().getYear());
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT distinct day,month,year FROM " + TABLE_ATTENDANCE + " WHERE year=?", selectionArgs);
        List<DateHistoryTO> historyArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            DateHistoryTO history = new DateHistoryTO();
            history.setYear(cursor.getInt(2));
            history.setDay(cursor.getInt(0));
            history.setMonth(cursor.getInt(1));
            historyArrayList.add(history);
        }
        return historyArrayList;
    }


    /**
     * 签到统计部分
     *
     * @return 学号，姓名，签到时间
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<StudentInfoTO> getCountToday() throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] selectionArgs = new String[3];
        selectionArgs[0] = String.valueOf(LocalDateTime.now().getDayOfMonth());
        selectionArgs[1] = String.valueOf(LocalDate.now().getMonthValue());
        selectionArgs[2] = String.valueOf(LocalDate.now().getYear());
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_ATTENDANCE + " WHERE  day= ? and month = ? and year = ?", selectionArgs);
        ArrayList<StudentInfoTO> studentInfoTO = new ArrayList<>();
        while (cursor.moveToNext()) {
            StudentInfoTO studentInfoTO1 = new StudentInfoTO();
            studentInfoTO1.setDateTime(sdf.parse(cursor.getString(4)));
            studentInfoTO1.setStuId(cursor.getString(0));
            studentInfoTO1.setName(cursor.getString(1));
            if( cursor.getString(2).equals("1") && cursor.getString(7).equals("0") && cursor.getString(8).equals("0") ){
                studentInfoTO1.setSituation("签到成功");
            } else if( cursor.getString(2).equals("0") && cursor.getString(7).equals("1") && cursor.getString(8).equals("0") ){
                studentInfoTO1.setSituation("请假");
            } else if( cursor.getString(2).equals("0") && cursor.getString(7).equals("0") && cursor.getString(8).equals("1") ){
                studentInfoTO1.setSituation("迟到");
            } else{
                studentInfoTO1.setSituation("出错!");
                System.out.println("出现错误!");
            }
            studentInfoTO1.setGPS_msg(cursor.getString(9));
            studentInfoTO.add(studentInfoTO1);
        }
        return studentInfoTO;
    }


    /**
     * 考勤情况统计
     *
     * @return 当天的签到信息
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public History getTodayHistory() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[4];
        selectionArgs[0] = String.valueOf(LocalDateTime.now().getDayOfMonth());
        selectionArgs[1] = String.valueOf(LocalDate.now().getMonthValue());
        selectionArgs[2] = String.valueOf(LocalDate.now().getYear());
        selectionArgs[3] = String.valueOf(1);
        //请假 is_Asked
        String[] selectionArgsAsk = new String[4];
        selectionArgsAsk[0] = String.valueOf(LocalDateTime.now().getDayOfMonth());
        selectionArgsAsk[1] = String.valueOf(LocalDate.now().getMonthValue());
        selectionArgsAsk[2] = String.valueOf(LocalDate.now().getYear());
        selectionArgsAsk[3] = String.valueOf(1);
        //迟到 is_Late
        String[] selectionArgsLate = new String[4];
        selectionArgsLate[0] = String.valueOf(LocalDateTime.now().getDayOfMonth());
        selectionArgsLate[1] = String.valueOf(LocalDate.now().getMonthValue());
        selectionArgsLate[2] = String.valueOf(LocalDate.now().getYear());
        selectionArgsLate[3] = String.valueOf(1);
        //当天总人数
        @SuppressLint("Recycle") Cursor cursor1 = db.rawQuery("SELECT  * FROM " + TABLE_ATTENDANCE + " WHERE  day= ? and month = ? and year = ? and is_Sign = ? ", selectionArgs);
        //请假
        @SuppressLint("Recycle") Cursor cursor2 = db.rawQuery("SELECT  * FROM " + TABLE_ATTENDANCE + " WHERE  day= ? and month = ? and year = ? and is_Asked = ? ", selectionArgsAsk);
        //迟到
        @SuppressLint("Recycle") Cursor cursor3 = db.rawQuery("SELECT  * FROM " + TABLE_ATTENDANCE + " WHERE  day= ? and month = ? and year = ? and is_Late = ? ", selectionArgsLate);
        //全部人数
        @SuppressLint("Recycle") Cursor curso2r = db.rawQuery("SELECT  * FROM " + TABLE_STUDENT, null);
        int count = curso2r.getCount();
        History history = new History();
        history.setDate(LocalDate.now());
        history.setIsSignUp(cursor1.getCount());
        history.setIsAsk(cursor2.getCount());
        history.setIsLate(cursor3.getCount());
        history.setNotSigUp(count - cursor1.getCount() - cursor2.getCount() - cursor3.getCount());
        return history;
    }

    /**
     * 考勤历史(默认返回今年的),用于日历
     *
     * @return 返回一个历史数据list
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<DateHistoryTO> getHistory() {
        // 先获取当前月份
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[2];
        //获取月
        selectionArgs[0] = String.valueOf(LocalDate.now().getMonthValue());
        //获取年
        selectionArgs[1] = String.valueOf(LocalDate.now().getYear());

//        //IsAsk
//        String[] selectionArgs2 = new String[3];
//        //获取月
//        selectionArgs2[0] = String.valueOf(LocalDate.now().getMonthValue());
//        //获取年
//        selectionArgs2[1] = String.valueOf(LocalDate.now().getYear());
//        //请假
//        selectionArgs2[2] = String.valueOf(1);
//
//        //IsLate
//        String[] selectionArgs3 = new String[3];
//        //获取月
//        selectionArgs3[0] = String.valueOf(LocalDate.now().getMonthValue());
//        //获取年
//        selectionArgs3[1] = String.valueOf(LocalDate.now().getYear());
//        //请假
//        selectionArgs3[2] = String.valueOf(0);
        //all
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT SUM(is_Sign) as is_sign,month,day,SUM(is_Asked) as is_asked,SUM(is_Late) as is_late FROM " + TABLE_ATTENDANCE + " where month=? and year = ? group by day;", selectionArgs);
        //IsAsk
//        @SuppressLint("Recycle") Cursor cursor2 = db.rawQuery("SELECT count(stu_id) as is_Ask,month,day FROM " + TABLE_ATTENDANCE + " where month=? and year = ? group by day;", selectionArgs);
//        //IsLate
//        @SuppressLint("Recycle") Cursor cursor3 = db.rawQuery("SELECT count(stu_id) as is_Late,month,day FROM " + TABLE_ATTENDANCE + " where month=? and year = ? group by day;", selectionArgs);
        //查找所有的人
        @SuppressLint("Recycle") Cursor cursor1 = db.rawQuery("SELECT count(*) FROM student;", null);
        cursor1.moveToNext();
        System.out.println("任务开始:begin!");
        List<DateHistoryTO> historyArrayList = new ArrayList<>();
        int total = cursor1.getInt(0);
        while (cursor.moveToNext()) {
            DateHistoryTO history = new DateHistoryTO();
            history.setIsAsk(cursor.getInt(3));
            System.out.println("IsAsk" + cursor.getInt(3));
            history.setIsLate(cursor.getInt(4));
            System.out.println("IsLate" + cursor.getInt(4));
            history.setUnSign(total - cursor.getInt(0) - cursor.getInt(3) - cursor.getInt(4));
            history.setIsSign(cursor.getInt(0));
            System.out.println("IsSign" + cursor.getInt(0));
            history.setDay(cursor.getInt(2));
            history.setMonth(cursor.getInt(1));
            historyArrayList.add(history);
        }
        return historyArrayList;
    }


    /**
     * 查询
     *
     * @param stuId 学号
     * @param name  用户名
     * @return 学生信息的list
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<StudentInfoTO> queryStudentInfo(String stuId, String name) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[2];
        selectionArgs[0] = String.valueOf(stuId);
        selectionArgs[1] = String.valueOf(name);
        String currentSqlSel;
        if ("".equals(name)){
            currentSqlSel = "SELECT * FROM " + TABLE_ATTENDANCE + " where stu_id like '%" + stuId + "%'";
        } else {
            currentSqlSel = "SELECT * FROM " + TABLE_ATTENDANCE + " where name Like '%" + name + "%'";
        }
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(currentSqlSel, null);
        ArrayList<StudentInfoTO> studentInfoTOS = new ArrayList<>();
        while (cursor.moveToNext()) {
            StudentInfoTO studentInfo = new StudentInfoTO();
            String datetime = cursor.getString(4);
            studentInfo.setName(cursor.getString(1));
            studentInfo.setStuId(cursor.getString(0));
            if( cursor.getString(2).equals("1") && cursor.getString(7).equals("0") && cursor.getString(8).equals("0") ){
                studentInfo.setSituation("签到成功");
            } else if( cursor.getString(2).equals("0") && cursor.getString(7).equals("1") && cursor.getString(8).equals("0") ){
                studentInfo.setSituation("请假");
            } else if( cursor.getString(2).equals("0") && cursor.getString(7).equals("0") && cursor.getString(8).equals("1") ){
                studentInfo.setSituation("迟到");
            } else{
                studentInfo.setSituation("出错!");
                System.out.println("出现错误!");
            }
            studentInfo.setGPS_msg(cursor.getString(9));
            studentInfo.setDateTime(sdf.parse(datetime));
            studentInfoTOS.add(studentInfo);
        }
        return studentInfoTOS;
    }

    /**
     * 签到
     *
     * @param stuId 学号
     * @param name  用户名
     * @param data  当前时间
     * @return 是否插入成功
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean signUp(String stuId, String name, LocalDateTime data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stu_id", stuId);
        cv.put("name", name);
        cv.put("is_Sign", Is_Sign.TURE.getCode());
        cv.put("day", data.getDayOfMonth());
        cv.put("month", data.getMonthValue());
        cv.put("year", data.getYear());
        cv.put("date", DateFormatUtils.getTodayDate());
        cv.put("is_Asked", 0);
        cv.put("is_Late", 0);//默认没迟到没请假
        cv.put("gps_msg", "西北工业大学");//默认值
        boolean end=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection cn =DriverManager.getConnection(url,"FaceCheck",password);
                    System.out.println("签到Connection连接数据库成功");
                    Statement st = cn.createStatement();
                    String sql ="insert into attendance values( '"+name+"',"+Integer.parseInt(stuId)+","+Is_Sign.TURE.getCode()+", "+data.getDayOfMonth()+" , "+data.getMonthValue()+" , "+data.getYear()+" , '"+DateFormatUtils.getTodayDate()+"' )";
                    System.out.println(sql);
                    st.execute(sql);
                    System.out.println("签到成功");
                } catch (ClassNotFoundException | SQLException e) {
                    System.out.println("签到失败");
                    e.printStackTrace();
                }
            }
        }).start();
        return db.insert(TABLE_ATTENDANCE, null, cv) == 1;
    }




    /**
     *
     * 重载函数，加入gps信息
     * @param stuId
     * @param name
     * @param data
     * @param gps_msg
     * @return
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean signUp(String stuId, String name, LocalDateTime data,String gps_msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //add the time decide
        Calendar cal = Calendar.getInstance();// 当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        final int startTime = 9 * 60 + 25;// 起始时间 9:25的分钟数
        final int endTime = 16 * 60 + 45;// 结束时间 16:45的分钟数
        if (minuteOfDay >= startTime && minuteOfDay <= endTime) {
            System.out.println("时间：在范围内");
            isLate = 0;
            is_Sign = 1;
            staticAsked = 0;
        } else {
            System.out.println("时间：在范围外");
            isLate = 1;
            is_Sign = 0;
            staticAsked = 0;
        }
        cv.put("stu_id", stuId);
        cv.put("name", name);
        cv.put("is_Sign", is_Sign);
        cv.put("day", data.getDayOfMonth());
        cv.put("month", data.getMonthValue());
        cv.put("year", data.getYear());
        cv.put("date", DateFormatUtils.getTodayDate());
        cv.put("is_Asked", staticAsked);//默认没请假
        cv.put("is_Late", isLate);
        cv.put("gps_msg", gps_msg);//默认值
        boolean end=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection cn =DriverManager.getConnection(url,"FaceCheck",password);
                    System.out.println("签到Connection连接数据库成功");
                    Statement st = cn.createStatement();
                    String sql ="insert into attendance values( '"+name+"',"+Integer.parseInt(stuId)+","+
                            is_Sign+", "+data.getDayOfMonth()+" , "+
                            data.getMonthValue()+" , "+data.getYear()+" , '"+
                            DateFormatUtils.getTodayDate()+"'" +", " +
                    staticAsked + ", " + isLate + ", " +
                            "'" + gps_msg + "'" +
                            " )";
                    System.out.println(sql);
                    st.execute(sql);
                    System.out.println("签到成功");
                } catch (ClassNotFoundException | SQLException e) {
                    System.out.println("签到失败");
                    e.printStackTrace();
                }
            }
        }).start();
        return db.insert(TABLE_ATTENDANCE, null, cv) == 1;
    }

    /**
     * 查询是否签到
     *
     * @param stuId 学生学号
     * @param data  当前时间
     * @return 是否签到
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean isSignUp(String stuId, LocalDateTime data) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[4];
        selectionArgs[0] = String.valueOf(stuId);
        selectionArgs[1] = String.valueOf(LocalDateTime.now().getDayOfMonth());
        selectionArgs[2] = String.valueOf(LocalDate.now().getMonthValue());
        selectionArgs[3] = String.valueOf(LocalDate.now().getYear());
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE + " WHERE stu_id = ? and day= ? and month = ? and year = ? ", selectionArgs);
        return cursor.getCount() == 1;
    }

    /**
     *  将指定学号的学生当天考勤记录设为迟到
     *  这里用的是本机设备当天日期
     * @param stuId 学生学号
     * @param data 指定日期
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean setIsLate(String stuId, LocalDateTime data){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[4];
        selectionArgs[0] = String.valueOf(stuId);
        selectionArgs[1] = String.valueOf(LocalDateTime.now().getDayOfMonth());
        selectionArgs[2] = String.valueOf(LocalDate.now().getMonthValue());
        selectionArgs[3] = String.valueOf(LocalDate.now().getYear());
        ContentValues v1 = new ContentValues();
        v1.put("is_Late",1);
        return db.update( TABLE_ATTENDANCE ,v1,"stu_id = ? and day= ? and month = ? and year = ?  ",selectionArgs)==1;
    }

    /**
     * 添加学生请假的考勤记录
     * @param stuId
     * @param name

     * @return
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean addLeave(String stuId,String name) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[4];
        selectionArgs[0] = String.valueOf(stuId);
        selectionArgs[1] = String.valueOf(LocalDateTime.now().getDayOfMonth());
        selectionArgs[2] = String.valueOf(LocalDate.now().getMonthValue());
        selectionArgs[3] = String.valueOf(LocalDate.now().getYear());

//        String[] selectionArgsData = new String[3];
//        selectionArgsData[0] = String.valueOf(LocalDateTime.now().getDayOfMonth());
//        selectionArgsData[1] = String.valueOf(LocalDate.now().getMonthValue());
//        selectionArgsData[2] = String.valueOf(LocalDate.now().getYear());
//        String currentSqlSel;
//        if ("".equals(name)){
//            currentSqlSel = "SELECT * FROM " + TABLE_ATTENDANCE + " where stu_id like '%" + stuId + "%'" + "and day= ? and month = ? and year = ?";
//        } else {
//            currentSqlSel = "SELECT * FROM " + TABLE_ATTENDANCE + " where name Like '%" + name + "%'" + "and day= ? and month = ? and year = ?";
//        }
//        Cursor cursor = db.rawQuery(currentSqlSel, selectionArgsData);
        ContentValues cv = new ContentValues();
//        cv.put("stu_id", cursor.getString(0));
//        cv.put("name", cursor.getString(1));
        cv.put("is_Sign", 0);//设置为 没签到
//        cv.put("day", cursor.getString(3));
//        cv.put("month", cursor.getString(4));
//        cv.put("year", cursor.getString(5));
//        cv.put("date", cursor.getString(6));
        cv.put("is_Asked", 1);//设置为 请假
        cv.put("is_Late", 0);//设置为 没迟到
//        cv.put("gps_msg", cursor.getString(9));//默认值

//        ArrayList<StudentInfoTO> studentInfoTOS = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            StudentInfoTO studentInfo = new StudentInfoTO();
//            String datetime = cursor.getString(4);
//            studentInfo.setName(cursor.getString(1));
//            studentInfo.setStuId(cursor.getString(0));
//            studentInfo.setDateTime(sdf.parse(datetime));
//            studentInfoTOS.add(studentInfo);
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection cn =DriverManager.getConnection(url,"FaceCheck",password);
                    System.out.println("签到Connection连接数据库成功");
                    Statement st = cn.createStatement();
                    String sql ="UPDATE  attendance SET is_Sign = 0,  is_Asked = 1， is_Late = 0 WHERE stu_id = " + selectionArgs[0] + " AND day= " + selectionArgs[1] + " AND month = " + selectionArgs[2] +" AND year = " + selectionArgs[3];
                    System.out.println(sql);
                    st.execute(sql);
                    System.out.println("更新成功");
                } catch (ClassNotFoundException | SQLException e) {
                    System.out.println("更新失败");
                    e.printStackTrace();
                }
            }
        }).start();



        return db.update( TABLE_ATTENDANCE ,cv,"stu_id = ? and day= ? and month = ? and year = ?  ",selectionArgs) == 1;
    }
}