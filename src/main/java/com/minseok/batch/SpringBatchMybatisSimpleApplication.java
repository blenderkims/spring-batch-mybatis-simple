package com.minseok.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * The type Spring batch mybatis simple application.
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class SpringBatchMybatisSimpleApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(SpringBatchMybatisSimpleApplication.class, args)));
    }

}
