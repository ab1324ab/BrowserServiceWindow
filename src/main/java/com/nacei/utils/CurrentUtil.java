package com.nacei.utils;

import java.util.UUID;

public class CurrentUtil {

    /**
     * 获取16位uuid
     * @return
     */
    public static String serial_no(){
        String serial = UUID.randomUUID().toString().replace("-","");
        serial = serial.substring(8,23);
        return serial;
    }
}
