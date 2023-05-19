package com.socialnetwork.api.models.additional;

import com.socialnetwork.api.models.additional.keys.ViewPk;
import com.socialnetwork.api.models.base.Post;
import com.socialnetwork.api.models.base.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;


@Entity
@Data
@Table(name = "views")
@NoArgsConstructor
public class View {
  @ManyToOne
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  User viewer;
  @ManyToOne
  @MapsId("postId")
  @JoinColumn(name = "post_id")
  Post seenPost;
  @EmbeddedId
  private ViewPk viewPk;

  public View(User viewer, Post seenPost) {
    this.viewer = viewer;
    this.seenPost = seenPost;
    viewPk = new ViewPk(viewer.getId(), seenPost.getId());
  }
}
