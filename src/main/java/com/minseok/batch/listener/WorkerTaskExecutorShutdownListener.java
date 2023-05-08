package com.minseok.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * package      : com.minseok.batch.listener
 * class        : WorkerTaskExecutorShutdownListener
 * author       : blenderkims
 * date         : 2023/04/12
 * description  :
 */
public class WorkerTaskExecutorShutdownListener implements JobExecutionListener {
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * Instantiates a new Worker task executor shutdown listener.
     *
     * @param threadPoolTaskExecutor the thread pool task executor
     */
    public WorkerTaskExecutorShutdownListener(@Qualifier("workerTaskExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {}

    @Override
    public void afterJob(JobExecution jobExecution) {
        threadPoolTaskExecutor.shutdown();
    }
}
