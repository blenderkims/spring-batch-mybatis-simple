package com.minseok.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;

import java.util.Date;

/**
 * package      : com.minseok.batch.job
 * class        : AbstractBatch
 * author       : blenderkims
 * date         : 2023/04/19
 * description  :
 */
@RequiredArgsConstructor
public abstract class AbstractBatch {
    /**
     * The Job builder factory.
     */
    protected final JobBuilderFactory jobBuilderFactory;

    /**
     * The Step builder factory.
     */
    protected final StepBuilderFactory stepBuilderFactory;

    /**
     * Job name string.
     *
     * @return the string
     */
    public abstract String jobName();

    /**
     * Batch job job.
     *
     * @return the job
     */
    public abstract Job batchJob();

    /**
     * Start step step.
     *
     * @return the step
     */
    public abstract Step startStep(Date executedAt);

}
