package com.honkai.blog.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.honkai.blog.db.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE phone = '${phone}' AND password = '${password}'")
    List<User> getUserSql(String phone, String password);
}
