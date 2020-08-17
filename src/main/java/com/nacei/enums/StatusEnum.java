package com.nacei.enums;

public enum StatusEnum {

    success("10000","成功"),
    Fail("00000","失败"),
    ;

    private String status;
    private String desc;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    StatusEnum(String status, String desc){
        this.status = status;
        this.desc = desc;
    }

}
