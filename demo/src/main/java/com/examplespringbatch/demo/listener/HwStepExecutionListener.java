package com.examplespringbatch.demo.listener;

import org.springframework.batch.core.*;
import org.springframework.stereotype.Component;

@Component
public class HwStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Before Step Method stepExecution.getExecutionContext : "+stepExecution.getExecutionContext());
        System.out.println("Before Step Method stepExecution.getJobExecution().getExecutionContext : "+stepExecution.getJobExecution().getExecutionContext());
        System.out.println("Before Step Method stepExecution.getJobExecution().getJobParameters() : "+stepExecution.getJobExecution().getJobParameters());
        System.out.println("Before Step Method stepExecution.getJobParameters(): "+stepExecution.getJobParameters());

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
