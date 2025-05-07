package cn.odboy.core.framework.mybatisplus.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.base.MyEntity;
import cn.odboy.core.framework.mybatisplus.constant.MpQuery;
import cn.odboy.exception.BadRequestException;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 查询条件创建工具
 */
@Slf4j
public class MybatisHelper {
    public static <R, Q> QueryWrapper<R> build(Q query) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();
        if (query == null) {
            return queryWrapper;
        }
        Class<?> clazz = query.getClass();
        try {
            List<Field> fields = getAllFields(clazz, new ArrayList<>());
            for (Field field : fields) {
                if (!field.canAccess(query)) {
                    field.setAccessible(true);
                    MpQuery q = field.getAnnotation(MpQuery.class);
                    if (q != null) {
                        String attributeName = getAttributeName(q, field);
                        Object fieldVal = field.get(query);
                        if (ObjectUtil.isNull(fieldVal) || "".equals(fieldVal)) {
                            continue;
                        }
                        if (StrUtil.isNotBlank(q.blurry())) {
                            handleBlurryQuery(queryWrapper, q.blurry(), fieldVal);
                            continue;
                        }
                        handleWrapper(q, queryWrapper, attributeName, fieldVal);
                    }
                    field.setAccessible(false);
                }
            }
        } catch (Exception e) {
            log.error("Failed to build query wrapper for class: {}", clazz.getName(), e);
        }
        return queryWrapper;
    }

    private static String getAttributeName(MpQuery q, Field field) {
        String attributeName = StrUtil.isBlank(q.propName()) ? field.getName() : q.propName();
        return StrUtil.toUnderlineCase(attributeName);
    }

    private static <R> void handleWrapper(MpQuery q, QueryWrapper<R> queryWrapper, String attributeName, Object fieldVal) {
        switch (q.type()) {
            case EQUAL:
                queryWrapper.eq(attributeName, fieldVal);
                break;
            case GREATER_THAN:
                queryWrapper.ge(attributeName, fieldVal);
                break;
            case LESS_THAN:
                queryWrapper.le(attributeName, fieldVal);
                break;
            case LESS_THAN_NQ:
                queryWrapper.lt(attributeName, fieldVal);
                break;
            case INNER_LIKE:
                queryWrapper.like(attributeName, fieldVal);
                break;
            case LEFT_LIKE:
                queryWrapper.likeLeft(attributeName, fieldVal);
                break;
            case RIGHT_LIKE:
                queryWrapper.likeRight(attributeName, fieldVal);
                break;
            case IN:
                handleInOrNotQuery(true, queryWrapper, attributeName, fieldVal);
                break;
            case NOT_IN:
                handleInOrNotQuery(false, queryWrapper, attributeName, fieldVal);
                break;
            case NOT_EQUAL:
                queryWrapper.ne(attributeName, fieldVal);
                break;
            case NOT_NULL:
                queryWrapper.isNotNull(attributeName);
                break;
            case IS_NULL:
                queryWrapper.isNull(attributeName);
                break;
            case BETWEEN:
                handleBetweenQuery(queryWrapper, fieldVal, attributeName);
                break;
            default:
                break;
        }
    }

    private static <R> void handleInOrNotQuery(boolean b, QueryWrapper<R> queryWrapper, String attributeName, Object fieldVal) {
        Collection<?> wrapNotInVal = (Collection<?>) fieldVal;
        if (CollectionUtil.isNotEmpty(wrapNotInVal)) {
            Optional<?> anyValOptional = wrapNotInVal.stream().findAny();
            if (anyValOptional.isPresent()) {
                Object o = anyValOptional.get();
                if (o instanceof Long) {
                    if (b) {
                        queryWrapper.in(attributeName, fieldVal);
                    } else {
                        queryWrapper.notIn(attributeName, fieldVal);
                    }
                } else if (o instanceof Integer) {
                    if (b) {
                        queryWrapper.in(attributeName, fieldVal);
                    } else {
                        queryWrapper.notIn(attributeName, fieldVal);
                    }
                } else {
                    throw new BadRequestException("InOrNotIn查询参数类型的对象属性值必须是Long/Integer类型集合");
                }
            }
        }
    }

    private static <R> void handleBetweenQuery(QueryWrapper<R> queryWrapper, Object fieldVal, String finalAttributeName) {
        if (fieldVal instanceof List) {
            List<Object> between = new ArrayList<>((List<?>) fieldVal);
            int minLength = 2;
            if (CollectionUtil.isNotEmpty(between) && between.size() >= minLength) {
                queryWrapper.between(finalAttributeName, between.get(0), between.get(1));
            } else {
                throw new BadRequestException("BETWEEN类型的对象列表长度必须 >= 2");
            }
        } else {
            throw new BadRequestException("BETWEEN类型的对象必须是一个List子集合");
        }
    }

    /**
     * 模糊多字段
     *
     * @param queryWrapper /
     * @param blurry       /
     * @param fieldVal     /
     * @param <R>          /
     */
    private static <R> void handleBlurryQuery(QueryWrapper<R> queryWrapper, String blurry, Object fieldVal) {
        List<String> blurryList = Arrays.stream(blurry.split(",")).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        queryWrapper.and(wrapper -> {
            for (String blurryItem : blurryList) {
                String column = StrUtil.toUnderlineCase(blurryItem);
                wrapper.or();
                wrapper.like(column, fieldVal.toString());
            }
        });
    }

    public static List<Field> getAllFields(Class<?> clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }

    @TableName("test_domain")
    private static class TestDomain {
        @NotNull(groups = MyEntity.Update.class)
        @TableId(value = "user_id", type = IdType.AUTO)
        private Long id;
        @TableField(value = "dept_id")
        private Long deptId;
        @NotBlank
        private String username;
        @NotBlank
        private String nickName;
    }

    public static void main(String[] args) {
        QueryWrapper<TestDomain> query = new QueryWrapper<TestDomain>();
        query.or(wrapper -> wrapper.eq("username", 1).or().eq("nickname", 2));
        query.eq("id", 1);
        query.orderByDesc("id");
        System.err.println("getSqlSelect=================================");
        System.err.println(query.getSqlSelect());
        System.err.println("getSqlSegment=================================");
        System.err.println(query.getSqlSegment());
        System.err.println("getSqlComment=================================");
        System.err.println(query.getSqlComment());
        System.err.println("getSqlSet=================================");
        System.err.println(query.getSqlSet());
        System.err.println("getTargetSql=================================");
        System.err.println(query.getTargetSql());
    }
}
