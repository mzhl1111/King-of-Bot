package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.util.UserDetailsImpl;
import com.kob.backend.service.user.bot.RemoveService;
import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RemoveServiceImpl implements RemoveService {

  @Autowired
  private BotMapper botMapper;

  @Override
  public Map<String, String> remove(Map<String, String> data) {
    UsernamePasswordAuthenticationToken authenticationToken =
        (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
    User user = loginUser.getUser();

    int botId = Integer.parseInt(data.get("botId"));
    QueryWrapper queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", botId);
    queryWrapper.eq("user_id", user.getId());
    Bot bot = botMapper.selectOne(queryWrapper);



    Map<String, String> map = new HashMap<>();
    if (bot == null) {
      map.put("error_message", "Bot not exist or not your bot or already deleted");
      return map;
    }

    botMapper.deleteById(bot.getId());
    map.put("error_message", "success");
    return map;
  }
}
