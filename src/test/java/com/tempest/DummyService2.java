package com.tempest;

import com.tempest.annotation.Service;

@Service
public class DummyService2 {
    DummyService service;

    private String string;

    public DummyService2(DummyService service) {
        this.service = service;
    }

    public void setString(String str) {
        this.string = str;
    }

    public String message() {
        // return "dummy";
        return this.service.message();
    }
}
