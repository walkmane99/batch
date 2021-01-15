package com.tempest;

public class DummyService2 {
    DummyService service;
    public DummyService2(DummyService service) {
        this.service = service;
    }

    public String message() {
        return this.service.message();
    }
}
