package com.shareyourproxy.api.domain.realm;

import io.realm.RealmObject;

/**
 * Created by Evan on 8/17/15.
 */
public class RealmString extends RealmObject {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
