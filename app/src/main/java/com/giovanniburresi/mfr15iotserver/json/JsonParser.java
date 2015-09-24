package com.giovanniburresi.mfr15iotserver.json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mario on 18/09/2015.
 */
public class JsonParser {

    public JsonParser(){}



    public String getLookupAddressResponse(String localip){

        JSONObject response = new JSONObject();
        try {
            response = new JSONObject();
            response.put("RESPONSE", "true");
            response.put("SERVER_IP", localip);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response.toString();
    }


    public static String getMessageError(String error){
        String response = "";

        JSONObject json = new JSONObject();
        try {
            json.put("RESPONSE", "false");
            json.put("ERROR", error);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public static String getMessageSuccess(String error){
        String response = "";

        JSONObject json = new JSONObject();
        try {
            json.put("RESPONSE", "true");
            json.put("MESSAGE", error);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

}
