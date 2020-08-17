package com.nacei.model;

public class ClickResponseDTO extends ResponseDTO {

    private String element;
    private String delayed;

    public String getDelayed() {
        return delayed;
    }

    public void setDelayed(String delayed) {
        this.delayed = delayed;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }
}
