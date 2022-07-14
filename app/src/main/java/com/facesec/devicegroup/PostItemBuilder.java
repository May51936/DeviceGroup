package com.facesec.devicegroup;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class PostItemBuilder implements URLBuilder{

    private List<NameValuePair> params;

    public PostItemBuilder() {
        params = new ArrayList<NameValuePair>();
    }

    @Override
    public PostItemBuilder addItem(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
        return this;
    }

    @Override
    public List<NameValuePair> getResult() {
        return params;
    }
}
