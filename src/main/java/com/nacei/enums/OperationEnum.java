package com.nacei.enums;

public enum OperationEnum {


    click("点击元素"),
    get_text("获取文字"),
    get_pic("获取图片"),
    ;

    private String desc;
    OperationEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String optionDesc(String name){
        try {
            return OperationEnum.valueOf(name).getDesc();
        }catch (Exception e){
            return "";
        }
    }

    public static String optionName(String desc){
        try {
            for (OperationEnum value : OperationEnum.values()) {
                if(value.getDesc().equals(desc)){
                    return value.name();
                }
            }
        }catch (Exception e){
            return "";
        }
        return "";
    }
}
