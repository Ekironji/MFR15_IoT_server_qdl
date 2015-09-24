package com.giovanniburresi.mfr15iotserver.data;

/**
 * Created by Mario on 21/09/2015.
 */
public class IoTService {

    String name;
    String desctiprion;

    public IoTService(String name, String desctiprion) {
        this.name = name;
        this.desctiprion = desctiprion;
    }

    public String getDesctiprion() {
        return desctiprion;
    }

    public void setDesctiprion(String desctiprion) {
        this.desctiprion = desctiprion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
