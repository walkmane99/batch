package com.tempest;

import com.tempest.annotation.Param;
import com.tempest.annotation.Task;
import com.tempest.annotation.Worker;

/**
 * Job
 */
@Task(name = "仕事")
public class Job {

    @Worker
    public void exec(@Param(name = "famiry") String name) {

    }

}