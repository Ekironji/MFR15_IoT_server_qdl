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
            response.put("response", "true");
            response.put("server_ip", localip);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response.toString();
    }


    public String getMessageError(String error){
        String response = "";

        JSONObject json = new JSONObject();
        try {
            json.put("response", "false");
            json.put("error", error);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

}
