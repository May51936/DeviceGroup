package com.facesec.devicegroup;

import com.facesec.devicegroup.Util.ConfigUtils;

public class GetItemBuilder implements URLBuilder{

    private String getUrl;

    public GetItemBuilder() {
        getUrl = ConfigUtils.WEB_SERVER_IP + "?";
    }

    @Override
    public URLBuilder addItem(String name, String value) {
        getUrl += (name + "=" + value);
        return this;
    }

    @Override
    public String getResult() {
        return getUrl;
    }
}
