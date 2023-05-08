package com.minseok.batch.job;

import com.minseok.batch.configuration.BatchConfiguration;
import com.minseok.batch.dto.User;
import com.minseok.batch.dto.UserBak;
import com.minseok.batch.partitioner.UserPartitioner;
import com.minseok.batch.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.retry.backoff.FixedBackOffPolicy;

import java.util.Date;
import java.util.Map;

/**
 * package      : com.minseok.batch.job
 * class        : UserBackupBatchJob
 * author       : blenderkims
 * date         : 2023/04/19
 * description  :
 */
@Slf4j
@Configuration
public class UserBackupBatch extends AbstractBatch {

    private static final String JOB_NAME = "userBackupBatchJob";
    private static final int CHUNK_SIZE = 1000;
    private static final int PAGE_SIZE = 500;
    private static final int RETRY_LIMIT = 3;
    private final SqlSessionFactory sqlSessionFactory;
    private final UserRepository userRepository;
    private final TaskExecutor workerTaskExecutor;

    /**
     * Instantiates a new User backup batch.
     *
     * @param jobBuilderFactory  the job builder factory
     * @param stepBuilderFactory the step builder factory
     * @param sqlSessionFactory  the sql session factory
     * @param userRepository     the user repository
     * @param workerTaskExecutor the worker task executor
     */
    public UserBackupBatch(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, SqlSessionFactory sqlSessionFactory, UserRepository userRepository, TaskExecutor workerTaskExecutor) {
        super(jobBuilderFactory, stepBuilderFactory);
        this.sqlSessionFactory = sqlSessionFactory;
        this.userRepository = userRepository;
        this.workerTaskExecutor = workerTaskExecutor;
    }

    @Override
    public String jobName() {
        return JOB_NAME;
    }

    @Bean(JOB_NAME)
    public Job batchJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(startStep(null))
                .next(cleanupStep(null))
                .preventRestart()
                .build();
    }

    @Bean(JOB_NAME + "StartStep")
    @JobScope
    public Step startStep(@Value("#{jobParameters[executedAt]}") Date executedAt) {
        return stepBuilderFactory.get(JOB_NAME + "StartStep")
                .partitioner("partitionStep", partitioner())
                .step(partitionStep())
                .partitionHandler(partitionHandler())
                .build();
    }

    /**
     * Cleanup step step.
     *
     * @param executedAt the executed at
     * @return the step
     */
    @Bean(JOB_NAME + "CleanupStep")
    @JobScope
    public Step cleanupStep(@Value("#{jobParameters[executedAt]}") Date executedAt) {
        return stepBuilderFactory.get(JOB_NAME + "CleanupStep")
                .tasklet((contribution, chunkContext) -> {
                    userRepository.deleteUserBakRemainData();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    /**
     * Partition step step.
     *
     * @return the step
     */
    @Bean(JOB_NAME + "PartitionStep")
    public Step partitionStep() {
        return stepBuilderFactory.get(JOB_NAME + "PartitionStep")
                .<User, UserBak>chunk(CHUNK_SIZE)
                .reader(itemReader(null, null))
                .processor(itemProcessor())
                .writer(itemWriter(null, null))
                .faultTolerant()
                .retry(DeadlockLoserDataAccessException.class)
                .retryLimit(RETRY_LIMIT)
                .backOffPolicy(new FixedBackOffPolicy())
                .build();
    }

    /**
     * Partitioner partitioner.
     *
     * @return the partitioner
     */
    @Bean(JOB_NAME + "Partitioner")
    @StepScope
    public Partitioner partitioner() {
        return new UserPartitioner(userRepository);
    }

    /**
     * Partition handler task executor partition handler.
     *
     * @return the task executor partition handler
     */
    @Bean(JOB_NAME + "PartitionHandler")
    @StepScope
    public TaskExecutorPartitionHandler partitionHandler() {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setTaskExecutor(workerTaskExecutor);
        partitionHandler.setGridSize(BatchConfiguration.POOL_SIZE);
        partitionHandler.setStep(partitionStep());
        return partitionHandler;
    }

    /**
     * Item reader my batis paging item reader.
     *
     * @param startId the start id
     * @param endId   the end id
     * @return the my batis paging item reader
     */
    @Bean(JOB_NAME + "ItemReader")
    @StepScope
    public MyBatisPagingItemReader<User> itemReader(
            @Value("#{stepExecutionContext[startId]}") String startId
            , @Value("#{stepExecutionContext[endId]}") String endId) {
        log.debug("[reader] start id: {}, end id: {}", startId, endId);
        return new MyBatisPagingItemReaderBuilder<User>()
                .sqlSessionFactory(sqlSessionFactory)
                .pageSize(PAGE_SIZE)
                .queryId("com.minseok.batch.repository.UserRepository.selectUserPagingList")
                .parameterValues(Map.of("startId", startId, "endId", StringUtils.defaultString(endId, StringUtils.EMPTY)))
                .saveState(false)
                .build();
    }

    /**
     * Item processor item processor.
     *
     * @return the item processor
     */
    @Bean(JOB_NAME + "ItemProcessor")
    @StepScope
    public ItemProcessor<User, UserBak> itemProcessor() {
        return (user) -> UserBak.of(user);
    }

    /**
     * Item writer my batis batch item writer.
     *
     * @param startId the start id
     * @param endId   the end id
     * @return the my batis batch item writer
     */
    @Bean(JOB_NAME + "ItemWriter")
    @StepScope
    public MyBatisBatchItemWriter<UserBak> itemWriter(
            @Value("#{stepExecutionContext[startId]}") String startId
            , @Value("#{stepExecutionContext[endId]}") String endId) {
        return new MyBatisBatchItemWriterBuilder<UserBak>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.minseok.batch.repository.UserRepository.mergeUserBak")
                .build();
    }
}
