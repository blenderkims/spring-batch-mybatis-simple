package com.minseok.batch.configuration;

import com.minseok.batch.listener.WorkerTaskExecutorShutdownListener;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;

/**
 * package      : com.minseok.batch.configuration
 * class        : BatchConfiguration
 * author       : blenderkims
 * date         : 2023/04/11
 * description  :
 */
@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

    public static final int POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * Worker task executor thread pool task executor.
     *
     * @return the thread pool task executor
     */
    @Bean("workerTaskExecutor")
    public ThreadPoolTaskExecutor workerTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize((int) (POOL_SIZE * 1.5));
        taskExecutor.setMaxPoolSize(POOL_SIZE * 2);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.setThreadNamePrefix("worker-thread-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    /**
     * Worker task executor shutdown listener job execution listener.
     *
     * @param workerTaskExecutor the worker task executor
     * @return the job execution listener
     */
    @Bean("workerTaskExecutorShutdownListener")
    public JobExecutionListener workerTaskExecutorShutdownListener(
            @Qualifier("workerTaskExecutor") ThreadPoolTaskExecutor workerTaskExecutor) {
        return new WorkerTaskExecutorShutdownListener(workerTaskExecutor);
    }

    /**
     * Job launcher application runner job launcher application runner.
     *
     * @param jobLauncher   the job launcher
     * @param jobExplorer   the job explorer
     * @param jobRepository the job repository
     * @param properties    the properties
     * @return the job launcher application runner
     */
    @Bean
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        DefaultJobParametersConverter jobParametersConverter = new DefaultJobParametersConverter();
        jobParametersConverter.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        runner.setJobParametersConverter(jobParametersConverter);
        String jobNames = properties.getJob().getNames();
        if (StringUtils.hasText(jobNames)) {
            runner.setJobNames(jobNames);
        }
        return runner;
    }
}
