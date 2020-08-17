package com.nacei.model;

public class ResponseDTO {

    public static String response_fail = "fail";
    public static String response_success = "success";

    private String cmd;
    private String code = response_success;
    private String source = "service";

    public ResponseDTO (){
    }

    public ResponseDTO (String cmd){
        this.cmd = cmd;
    }

    public ResponseDTO(String cmd, String element, String value) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
