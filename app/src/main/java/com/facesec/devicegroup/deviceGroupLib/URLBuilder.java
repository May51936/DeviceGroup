package com.facesec.devicegroup.deviceGroupLib;

/***
 * Created by Wang Tianyu
 * URL builder interface for builder pattern
 */

public interface URLBuilder {

    public URLBuilder addItem(String name, Object value);

    public Object getResult();

}
