package com.socialnetwork.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.socialnetwork.api.models.additional.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

import static com.socialnetwork.api.util.Const.Response.TIME_FORMAT;

@Data
public class NotificationDto {
  UserDto.Response.Author initiator;
  PostDto.Response.Id post;
  NotificationType notificationType;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
  LocalDateTime timestamp;
}
