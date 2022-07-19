package com.facesec.devicegroup.deviceGroupLib;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

class PostItemBuilder implements URLBuilder{

    private List<NameValuePair> params;

    public PostItemBuilder() {
        params = new ArrayList<NameValuePair>();
    }

    @Override
    public PostItemBuilder addItem(String name, Object value) {
        params.add(new BasicNameValuePair(name, (String)value));
        return this;
    }

    @Override
    public List<NameValuePair> getResult() {
        return params;
    }
}
