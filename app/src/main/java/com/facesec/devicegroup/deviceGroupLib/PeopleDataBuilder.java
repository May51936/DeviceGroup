package com.facesec.devicegroup.deviceGroupLib;

import org.json.JSONException;
import org.json.JSONObject;

public class PeopleDataBuilder implements URLBuilder {
    private JSONObject data = new JSONObject();
    @Override
    public PeopleDataBuilder addItem(String name, Object value) {
        try {
            data.put(name,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public JSONObject getResult() {
        return data;
    }
}
