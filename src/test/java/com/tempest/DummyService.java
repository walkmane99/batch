package com.tempest;

import com.tempest.annotation.Service;

@Service
public class DummyService {
    private String string;

    public DummyService() {
        this.string = "hello world";
    }

    public String message() {
        return this.string;
    }

    public void setMe(String str, int i) {
        return;
    }

}
