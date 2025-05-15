package cn.odboy.core.framework.web.converters;

import cn.hutool.core.util.StrUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateStringToDateConverter implements Converter<String, Date> {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date convert(String source) {
        if (StrUtil.isBlank(source)) {
            return null;
        }
        try {
            return sdf.parse(source);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date string: " + source);
        }
    }
}
