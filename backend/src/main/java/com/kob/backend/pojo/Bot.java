package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bot {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer userId;
  private String botName;
  private String description;
  private String content;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Canada/Pacific")
  private Date createTime;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Canada/Pacific")
  private Date modifyTime;
}