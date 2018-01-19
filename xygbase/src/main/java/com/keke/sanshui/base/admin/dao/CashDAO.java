package com.keke.sanshui.base.admin.dao;

import com.keke.sanshui.base.admin.po.agent.CashPo;
import com.keke.sanshui.base.admin.po.agent.CashQueryPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CashDAO {

    CashPo findById(Integer id);

    int insertCash(CashPo cashPo);

    int updatePo(@Param("param") CashPo cashPo);

    List<CashPo> selectList(@Param("param")CashQueryPo cashQueryPo);

    Long selectCount(@Param("param")CashQueryPo cashQueryPo);
}
