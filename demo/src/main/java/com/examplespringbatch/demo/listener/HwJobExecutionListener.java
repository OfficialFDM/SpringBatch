package com.examplespringbatch.demo.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class HwJobExecutionListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Before starting the job JobName: " + jobExecution.getJobInstance().getJobName());
        System.out.println("Before starting the job CreateTime: " + jobExecution.getCreateTime());
        System.out.println("Before starting the job ExecutionContext: " + jobExecution.getExecutionContext());
        jobExecution.getExecutionContext().put("my key", "my value");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Before starting the job EndTime: " + jobExecution.getEndTime());
        System.out.println(jobExecution.getExecutionContext());
    }
}
