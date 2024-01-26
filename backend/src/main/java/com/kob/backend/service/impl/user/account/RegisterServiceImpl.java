package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.spi.RegisterableService;

@Service
public class RegisterServiceImpl implements RegisterService {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Map<String, String> register(String userName, String password, String confirmPwd) {
    Map<String, String> map = new HashMap<>();
    if (userName == null) {
      map.put("error_message", "Username cannot be empty");
      return map;
    }

    userName = userName.trim();
    if(userName.isEmpty()) {
      map.put("error_message", "Username cannot be empty");
      return map;
    }

    if(userName.length() >100) {
      map.put("error_message", "Username length cannot exceed 100");
      return map;
    }

    if (!password.equals(confirmPwd)) {
      map.put("error_message", "Password and confirmed password not match");
      return map;
    }

    if (password.isEmpty()) {
      map.put("error_message", "Password cannot be empty");
      return map;
    }

    if(password.length() > 100) {
      map.put("error_message", "Password length cannot exceed 100");
      return map;
    }

    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", userName);
    List<User> users = userMapper.selectList(queryWrapper);
    if (!users.isEmpty()){
      map.put("error_message", "Username already exists");
      return map;
    }

    String encodedPwd = passwordEncoder.encode(password);
    String photo = null;
    User newUser = new User(null, userName, encodedPwd, photo, 1500);
    userMapper.insert(newUser);
    map.put("error_message", "success");
    return map;
  }
}
