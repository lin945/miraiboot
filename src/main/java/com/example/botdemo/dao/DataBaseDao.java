package com.example.botdemo.dao;

import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author lin945
 * @date 2020/8/30 18:42
 */
@Repository
public interface DataBaseDao {
    public long getUser(String user);

    public Map<String, Long> getUserMap(String user);

    public int addUser(String token, long qq);
}
