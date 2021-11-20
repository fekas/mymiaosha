package com.zhongbin.miaoshademo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成用户工具类
 */
public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setNickname("dabin" + i);
            user.setSalt("1a2b3c4d");
            user.setPassword("b7797cce01b4b131b433b6acf4add449");
            users.add(user);
        }
        System.out.println("create user");
//        Connection conn = getConn();
//
//        String sql = "INSERT INTO t_user(id, nickname, password, salt) values(?, ?, 'b7797cce01b4b131b433b6acf4add449', '1a2b3c4d');";
//        PreparedStatement ps = conn.prepareStatement(sql);
//        for (User u : users) {
//            ps.setLong(1, u.getId());
//            ps.setString(2, u.getNickname());
//            ps.addBatch();
//        }
//        ps.executeBatch();
//        ps.close();
//        conn.close();
//        System.out.println("insert to db");
        //登陆获取令牌
        String loginUrl = "http://localhost:8080/login/doLogin";
        File file = new File("/Users/zhongbin/Documents/Code/Test/Jmeter/config.txt");
        if (file.exists()){
            file.delete();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.seek(0);
        for (User u : users
             ) {
            URL url = new URL(loginUrl);
            HttpURLConnection co = ((HttpURLConnection) url.openConnection());
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream outputStream = co.getOutputStream();
            String param = "mobile=" + u.getId() + "&password=d3b1294a61a07da9b49b6e22b2cbd7f9";
            outputStream.write(param.getBytes());
            outputStream.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len=inputStream.read(buffer)) >= 0 ){
                byteArrayOutputStream.write(buffer, 0, len);
            }
            inputStream.close();
            byteArrayOutputStream.close();
            String response = new String(byteArrayOutputStream.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = ((String) respBean.getObj());
            System.out.println("create userTicket : " + userTicket + ", user id : " + u.getId());
            String row = u.getId() + "," + userTicket;
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(row.getBytes());
            randomAccessFile.write("\n".getBytes());
            System.out.println("write to file : " + u.getId());
        }
        randomAccessFile.close();
        System.out.println("over");
    }

    private static Connection getConn() throws Exception{
        String url = "jdbc:mysql://120.27.149.243:3306/miaosha?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "Zl.mtcldys606910";
        String driver = "com.mysql.cj.jdbc.Driver";

        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);

    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
