package com.maiya.dal.dao.credit;

import com.maiya.dal.model.MemberIntenetInfo;

import java.util.List;

public interface MemberIntenetInfoDao {


    MemberIntenetInfo queryMemberIntenetInfoByUserId(String userId);

    void updateJDAuthorFlagByUserId(String userId, int flag);

    void updateTaoBaoAuthorFlagByUserId(String userId, int flag);

    void insert(MemberIntenetInfo obj);

    List<MemberIntenetInfo> queryMemberIntenetInfoByJDAuthorFlag(int jdAuthorFlag);

    List<MemberIntenetInfo> queryMemberIntenetInfoByTBAuthorFlag(int sTaoBaoAuthorFlag);

}
