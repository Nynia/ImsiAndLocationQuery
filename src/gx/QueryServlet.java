package gx;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;

/**
 * Created by Ridiculous on 2016/9/8.
 */
public class QueryServlet extends javax.servlet.http.HttpServlet {
    static Logger logger = Logger.getLogger(QueryServlet.class.getName());
    List<String> ip_list = new ArrayList<String>();

    public void init() {
        logger.info("init..");
        String prefix = this.getServletContext().getRealPath("/");
        String fileName = prefix + "WEB-INF/ip.txt";
        logger.info(fileName);
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                ip_list.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String phoneNum = request.getParameter("phoneNum");
        String imsiId = request.getParameter("imsi");
        String locationId = request.getParameter("locationId");
        String ip = request.getRemoteHost();

        JSONObject result = new JSONObject();
        String errorCode = null;
        logger.info(ip_list);

        logger.info(phoneNum);
        logger.info(imsiId);
        logger.info(locationId);
        logger.info(ip);

        String imsi_result = null;
        String location_result = null;
        int flag = 0;
        if (phoneNum != null && imsiId != null && locationId != null) {
            if (phoneNum.length() == 11) {
                if (phoneNum.startsWith("133") || phoneNum.startsWith("149") || phoneNum.startsWith("153")
                        || phoneNum.startsWith("170") || phoneNum.startsWith("173") || phoneNum.startsWith("177")
                        || phoneNum.startsWith("180") || phoneNum.startsWith("181") || phoneNum.startsWith("189")) {
                    if (imsiId.length() <= 15 && locationId.length() <= 3) {
                        for (String ip_item : ip_list) {
                            if (ip.equals(ip_item)) {
                                //String url = "http://132.224.218.132:9250/dcninterface/imsiAndArea";
                                String url = "http://192.168.127.53:9250/dcninterface/imsiAndArea";
                                String params = String.format("phoneNum=%s", phoneNum);
                                String re = HttpRequest.sendGet(url, params);
                                JSONObject tempresult = JSONObject.parseObject(re);

                                imsi_result = tempresult.getString("imsi");
                                location_result = tempresult.getString("location");

                                String location_consistant = location_result.equals(locationId)?"1":"0";
                                String imsi_consistant = imsi_result.equals(imsiId)?"1":"0";

                                result.put("code", "0");
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("imsi", imsi_consistant);
                                jsonObject.put("location", location_consistant);
                                result.put("result", jsonObject);
                                // ********************************************************************
                                // JDBC driver name and database URL
                                final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
                                final String DB_URL = "jdbc:mysql://localhost:3306/jsgx";
                                // ***********************************************************************
                                // Database credentials
                                final String USER = "root";
                                final String PASS = "admin";
                                Connection conn = null;
                                Statement stmt = null;
                                try {
                                    // Register JDBC driver
                                    Class.forName(JDBC_DRIVER);

                                    // connection to mySQL server and  Open a connection
                                    logger.info("Connecting to a selected database...");
                                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                                    logger.info("Connected database successfully...");

                                    // create a Statement object ()
                                    logger.info("Inserting records into the table...");
                                    stmt = conn.createStatement();

                                    //  Execute a query
                                    String sql = "INSERT INTO imsi_area_record"
                                            + "(id,ip,phone_num,imsi_id,location_id,imsi_result,location_result," +
                                            "imsi_consistant,location_consistant,create_time)" +
                                            "VALUES(0,'" + ip + "'," +
                                            "'" + phoneNum + "'," +
                                            "'" + imsiId + "'," +
                                            "'" + locationId + "'," +
                                            "'" + imsi_result + "'," +
                                            "'" + location_result + "'," +
                                            "'" + imsi_consistant + "'," +
                                            "'" + location_consistant + "'," +
                                            "'" + Tools.getTimestamp() + "'" +
                                            ")";
                                    stmt.executeUpdate(sql);
                                    logger.info("database insert successfull");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //
                                flag = 1;
                                break;
                            }
                        }
                        if (flag == 0) {
                            result.put("code", "1");
                            errorCode = "151";
                        }
                    } else {
                        result.put("code", "1");
                        errorCode = "163";
                    }
                } else {
                    result.put("code", "1");
                    errorCode = "164";
                }
            } else {
                result.put("code", "1");
                errorCode = "103";
            }
        }
        else {
            result.put("code", "1");
            errorCode = "150";
        }

        if (errorCode != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errorCode", errorCode);
            jsonObject.put("errorMsg", Constants.resultCodeMap.get(errorCode));
            result.put("error", jsonObject);
        }
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();

        logger.info(result.toString());
        pw.write(result.toString());
    }
}
