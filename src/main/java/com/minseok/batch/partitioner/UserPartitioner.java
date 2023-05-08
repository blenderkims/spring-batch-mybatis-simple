package com.minseok.batch.partitioner;

import com.minseok.batch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * package      : com.minseok.batch.partitioner
 * class        : UserPartitioner
 * author       : blenderkims
 * date         : 2023/04/19
 * description  :
 */
@Slf4j
@RequiredArgsConstructor
public class UserPartitioner implements Partitioner {

    private final UserRepository userRepository;
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        final Map<String, ExecutionContext> result = new HashMap<>();
        final int totalCount = userRepository.selectUserTotalCount();
        final int unit = totalCount / gridSize;
        final String minId = userRepository.selectUserMinId(null, null, 0, totalCount);
        final String maxId = userRepository.selectUserMaxId(null, null, 0, totalCount);
        int number = 0;
        int page = unit;
        String startId = minId;
        String endId = Optional.ofNullable(userRepository.selectUserMaxId(null, null, page - 1, 1)).orElse(maxId);
        while (startId.compareTo(maxId) < 0) {
            ExecutionContext executionContext = new ExecutionContext();
            result.put("partition" + number, executionContext);
            executionContext.putString("startId", startId);
            if (!maxId.equals(endId)) {
                executionContext.putString("endId", endId);
            }
            log.debug("[partition: {}] start id: {}, end id: {}", number, executionContext.get("startId"), executionContext.get("endId"));
            page = page + unit;
            startId = endId;
            endId = Optional.ofNullable(userRepository.selectUserMaxId(null, null, page - 1, 1)).orElse(maxId);
            number++;
        }
        return result;
    }
}
