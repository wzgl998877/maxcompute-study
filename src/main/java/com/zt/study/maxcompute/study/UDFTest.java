package com.zt.study.maxcompute.study;

import com.aliyun.odps.data.Struct;
import com.aliyun.odps.udf.UDF;
import com.aliyun.odps.udf.annotation.Resolve;

import java.text.SimpleDateFormat;
import java.util.*;

@Resolve("struct<input_name:string, input_timestamp:bigint>->map<string,string>")
public class UDFTest extends UDF {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 将时间戳列表转换为时间字符串列表
     *
     * @param timestamps 时间戳列表
     * @return 时间字符串列表
     */
    public List<String> evaluate(List<Long> timestamps) {
        if (timestamps == null) {
            return null;
        }
        List<String> result = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat(PATTERN);
        for (Long timestamp : timestamps) {
            Date date = new Date(timestamp < 9999999999L ? timestamp * 1000 : timestamp);
            String dateString = formatter.format(date);
            result.add(dateString);
        }
        return result;
    }

    /**
     * 将时间戳map转换为时间字符串map
     *
     * @param timestamps 时间戳map，其中value为时间戳
     * @return 时间字符串map
     */
    public Map<String, String> evaluate(Map<String, Long> timestamps) {
        if (timestamps == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>(timestamps.size());
        SimpleDateFormat formatter = new SimpleDateFormat(PATTERN);
        for (String key : timestamps.keySet()) {
            Long timestamp = timestamps.get(key);
            Date date = new Date(timestamp < 9999999999L ? timestamp * 1000 : timestamp);
            String dateString = formatter.format(date);
            result.put(key, dateString);
        }
        return result;
    }

    /**
     * 将时间戳转换为时间字符串
     *
     * @param input 时间戳Struct
     * @return 时间字符串Struct
     */
    public Map<String, String> evaluate(Struct input) {
        if (input == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(PATTERN);
        String nameValue = (String) input.getFieldValue("input_name");
        Long timestampValue = (Long) input.getFieldValue("input_timestamp");
        Date date = new Date(timestampValue < 9999999999L ? timestampValue * 1000 : timestampValue);
        String dateString = formatter.format(date);
        Map<String, String> result = new HashMap<>(8);
        result.put("output_name", nameValue);
        result.put("output_time", dateString);
        return result;
    }
}