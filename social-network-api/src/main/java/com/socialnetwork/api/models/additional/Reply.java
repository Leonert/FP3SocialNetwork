package com.socialnetwork.api.models.additional;

import com.socialnetwork.api.models.additional.keys.ReplyPK;
import com.socialnetwork.api.models.base.Post;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "replies")
public class Reply {
  @EmbeddedId
  private ReplyPK replyPK;

  @ManyToOne
  @JoinColumn(name = "reply_id")
  @MapsId("replyId")
  private Post reply;
  @OneToOne
  @JoinColumn(name = "replied_id")
  @MapsId("repliedId")
  private Post replied;
}