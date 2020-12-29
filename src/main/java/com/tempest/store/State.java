package com.tempest.store;

import com.tempest.utils.FaildCreateObjectException;

public interface State {
    public <T> T getProperties() throws FaildCreateObjectException;
}
