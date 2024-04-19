package com.honkai.blog.db.mapper;

import org.apache.ibatis.annotations.Select;

import io.lettuce.core.dynamic.annotation.Param;

public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    @Select("${nativeSql}")
    Object nativeSql(@Param("nativeSql") String nativeSql);
}