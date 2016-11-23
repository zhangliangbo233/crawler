package com.maiya.dal.dao.credit;

import com.maiya.dal.model.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户信息dao
 * Created by zhanglb on 16/9/14.
 */
public interface UserInfoDao {

    /**
     * 查询用户列表
     *
     * @param limitMonth
     */
    List<UserInfo> listUser(@Param("offsetDate") String offsetDate, @Param("limitMonth") int limitMonth,
                            @Param("offsetMonth") int offsetMonth);
}
