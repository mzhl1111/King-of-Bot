package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.util.UserDetailsImpl;
import com.kob.backend.service.user.bot.GetService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetServiceImpl implements GetService {

  @Autowired
  private BotMapper botMapper;

  @Override
  public List<Bot> get() {
    UsernamePasswordAuthenticationToken authenticationToken =
        (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authenticationToken.getPrincipal();
    User user = userDetails.getUser();

    QueryWrapper queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", user.getId());

    return botMapper.selectList(queryWrapper);
  }
}
