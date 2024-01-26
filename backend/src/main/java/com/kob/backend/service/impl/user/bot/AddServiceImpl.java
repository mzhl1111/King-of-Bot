package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.util.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AddServiceImpl implements AddService {

  @Autowired
  private BotMapper botMapper;

  @Override
  public Map<String, String> add(Map<String, String> data) {
    UsernamePasswordAuthenticationToken authenticationToken =
        (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
    User user = loginUser.getUser();

    String botName = data.get("botName");
    String description = data.get("description");
    String content = data.get("content");

    Map<String, String> map = new HashMap<>();
    botName = botName.trim();

    if (botName == null || botName.isEmpty()) {
      map.put("error_message", "Bot name cannot be empty");
      return map;
    }

    if (botName.length() > 100) {
      map.put("error_message", "Bot name cannot exceed length 100");
      return map;
    }

    if (description == null || description.isEmpty()) {
      description = "This user is lazy and hasn't provided any information";
    }

    if (description.length() > 1000) {
      map.put("error_message", "Description length cannot exceed 1000");
    }

    if (content == null || content.isEmpty()) {
      map.put("error_message", "Script content cannot be empty");
      return map;
    }

    if (content.length() > 10000) {
      map.put("error_message", "Script length cannot exceed 10k");
    }

    Date now = new Date();
    Bot bot = new Bot(null, user.getId(), botName, description, content, now, now);

    botMapper.insert(bot);
    map.put("error_message", "success");

    return map;
  }
}
