package com.keke.sanshui.base.admin.po.agent;

import lombok.Data;

@Data
public class CashQueryPo extends CashPo {
    private int offset = 0;
    private int limit = 20;
}
