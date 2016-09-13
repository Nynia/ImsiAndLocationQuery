package gx;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ridiculous on 2016/5/26.
 */
public class Tools {
    public static String getTimestamp() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String atTime = sdf.format(new Date(System.currentTimeMillis()));
        return atTime;
    }
}
