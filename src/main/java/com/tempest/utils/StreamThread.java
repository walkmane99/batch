package com.tempest.utils;

import java.io.IOException;
import java.io.InputStream;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class StreamThread extends Thread {
    private static final int BUF_SIZE = 4096;
    private InputStream in;
    private String state;

    public StreamThread(InputStream in, String state) throws IOException {
        this.in = in;
        this.state = state;
    }

    public void run() {
        byte[] buf = new byte[BUF_SIZE];
        int size = -1;
        try {
            while ((size = in.read(buf, 0, BUF_SIZE)) != -1) {
                String msg = new String(buf, 0, size);
                if (!msg.trim().isEmpty()) {
                    log.debug("[" + state + "] " + msg);
                }
            }
        } catch (IOException e) {
            log.debug(e);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
        }
    }
}
