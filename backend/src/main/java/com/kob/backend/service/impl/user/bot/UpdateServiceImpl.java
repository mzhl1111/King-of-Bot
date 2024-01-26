package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.util.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UpdateServiceImpl implements UpdateService {
  @Autowired
  private BotMapper botMapper;

  @Override
  public Map<String, String> update(Map<String, String> data) {
    UsernamePasswordAuthenticationToken authenticationToken =
        (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
            .getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authenticationToken.getPrincipal();
    User user = userDetails.getUser();

    Integer botId = Integer.parseInt(data.get("botId"));
    String botName = data.get("botName");
    String description = data.get("description");
    String content = data.get("content");
    Date modifyDate = new Date();
    Map<String, String> map = new HashMap<>();

    if ((botName == null || botName.isEmpty()) && (description == null || description.isEmpty()) &&
        (content == null || content.isEmpty())) {
      map.put("error_message", "No information to update");
      return map;
    }

    QueryWrapper queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("Id", botId);
    Bot bot = botMapper.selectOne(queryWrapper);

    if (bot == null) {
      map.put("error_message", "Bot does not exit or already deleted");
      return map;
    }

    if (!bot.getUserId().equals(user.getId())) {
      map.put("error_message", "Cannot modify a bot not owned by you");
      return map;
    }

    botName = botName.trim();
    if (botName == null || botName.isEmpty()) {
      botName = bot.getBotName();
    } else if (botName.length() > 100) {
      map.put("error_message", "Bot name cannot exceed length of 100");
      return map;
    }

    if (description == null || description.isEmpty()) {
      description = bot.getDescription();
    } else if (description.length() > 1000) {
      map.put("error_message", "Bot description cannot exceed length of 1000");
    }

    if (content == null || content.isEmpty()) {
      content = bot.getContent();
    } else if (content.length() > 10000) {
      map.put("error_message", "Bot code content cannot exceed lenght of 10000");
      return map;
    }

    Bot newBot = new Bot(bot.getId(), user.getId(), botName, description, content,
        bot.getCreateTime(), modifyDate);

    botMapper.updateById(newBot);

    map.put("error_message", "success");
    return map;
  }
}
