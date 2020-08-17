package com.nacei.enums;

public enum RequstSignEnum {

    service_bind_element("service_bind_element","前端绑定通知"),
    service_bind_element_success("service_bind_element_success","前端绑定成功返回通知"),
    service_execute_program_settings("service_execute_program_settings","执行程序设置"),
    ;

    private String cmdCode;
    private String cmdExplain;
    RequstSignEnum(String cmdCode,String cmdExplain){
        this.cmdCode = cmdCode;
        this.cmdExplain = cmdExplain;
    }

    public String getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(String cmdCode) {
        this.cmdCode = cmdCode;
    }

    public String getCmdExplain() {
        return cmdExplain;
    }

    public void setCmdExplain(String cmdExplain) {
        this.cmdExplain = cmdExplain;
    }
}
