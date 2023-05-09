package com.socialnetwork.api.models.additional;

import com.socialnetwork.api.models.additional.keys.RetweetPk;
import com.socialnetwork.api.models.base.Post;
import com.socialnetwork.api.models.base.User;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "retweets")
public class Retweet {
  @EmbeddedId
  private RetweetPk retweetPk;

  @ManyToOne
  @MapsId("postId")
  @JoinColumn(name = "post_id")
  private Post post;

  @ManyToOne
  @MapsId("retweetedPostId")
  @JoinColumn(name = "retweeted_post_id")
  private Post retweetedPost;
}
