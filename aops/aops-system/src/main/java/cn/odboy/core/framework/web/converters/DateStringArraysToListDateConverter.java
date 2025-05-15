package cn.odboy.core.framework.web.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DateStringArraysToListDateConverter implements Converter<String[], List<Date>> {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Date> convert(String[] source) {
        if (source.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(source)
                .map(s -> {
                    try {
                        return sdf.parse(s);
                    } catch (ParseException e) {
                        throw new IllegalArgumentException("Invalid date string: " + s);
                    }
                })
                .collect(Collectors.toList());
    }
}
