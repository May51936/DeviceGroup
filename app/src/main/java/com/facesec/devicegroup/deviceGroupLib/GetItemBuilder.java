package com.facesec.devicegroup.deviceGroupLib;

import com.facesec.devicegroup.deviceGroupLib.util.ConfigUtils;

class GetItemBuilder implements URLBuilder{

    private String getUrl;

    public GetItemBuilder() {
        getUrl = ConfigUtils.WEB_SERVER_IP + "?";
    }

    @Override
    public URLBuilder addItem(String name, Object value) {
        getUrl += (name + "=" + value);
        return this;
    }

    @Override
    public String getResult() {
        return getUrl;
    }
}
