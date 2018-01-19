package com.keke.sanshui.admin.util;

import com.keke.sanshui.base.util.MD5Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignUtil {

    public final  static  String createSign(Integer gUid,String rechargeDiamond,String key){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Guid=").append(gUid).append("&");
        stringBuilder.append("RechargeDiamond=").append(rechargeDiamond);
        stringBuilder.append(key);
        String data =  MD5Util.md5(stringBuilder.toString()).toLowerCase();
        return data;
    }

}

