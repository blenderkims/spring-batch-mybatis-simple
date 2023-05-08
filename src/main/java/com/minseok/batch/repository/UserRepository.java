package com.minseok.batch.repository;

import com.minseok.batch.dto.User;
import com.minseok.batch.dto.UserBak;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * package      : com.minseok.batch.repository
 * class        : UserRepository
 * author       : blenderkims
 * date         : 2023/04/19
 * description  :
 */
@Mapper
public interface UserRepository {

    public int selectUserTotalCount();

    /**
     * Select user paging list list.
     *
     * @param startId  the start id
     * @param endId    the end id
     * @param skipRows the skip rows
     * @param pageSize the page size
     * @return the list
     */
    public List<User> selectUserPagingList(@Param("startId") String startId
            , @Param("endId") String endId
            , @Param("_skiprows") int skipRows
            , @Param("_pagesize") int pageSize);

    /**
     * Select user min id string.
     *
     * @param startId  the start id
     * @param endId    the end id
     * @param skipRows the skip rows
     * @param pageSize the page size
     * @return the string
     */
    public String selectUserMinId(@Param("startId") String startId
            , @Param("endId") String endId
            , @Param("_skiprows") int skipRows
            , @Param("_pagesize") int pageSize);

    /**
     * Select user max id string.
     *
     * @param startId  the start id
     * @param endId    the end id
     * @param skipRows the skip rows
     * @param pageSize the page size
     * @return the string
     */
    public String selectUserMaxId(@Param("startId") String startId
            , @Param("endId") String endId
            , @Param("_skiprows") int skipRows
            , @Param("_pagesize") int pageSize);

    /**
     * Merge user bak int.
     *
     * @param userBak the user bak
     * @return the int
     */
    public int mergeUserBak(UserBak userBak);

    /**
     * Delete user bak remain data int.
     *
     * @return the int
     */
    public int deleteUserBakRemainData();
}
