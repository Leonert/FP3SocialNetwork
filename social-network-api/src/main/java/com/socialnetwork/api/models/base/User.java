package com.socialnetwork.api.models.base;

import com.socialnetwork.api.models.additional.Bookmark;
import com.socialnetwork.api.models.additional.Follow;
import com.socialnetwork.api.models.additional.Like;
import com.socialnetwork.api.models.additional.View;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "username")
  private String username;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "password")
  private String password;

  @Column(name = "email_address")
  private String emailAddress;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "profile_background_image_url")
  private String profileBackgroundImageUrl;

  @Column(name = "profile_image_url")
  private String profileImageUrl;
  //relations
  @OneToMany(mappedBy = "followerUser")
  private List<Follow> followers;

  @OneToMany(mappedBy = "followedUser")
  private List<Follow> followed;

  @OneToMany(mappedBy = "seenPost")
  private List<View> seenPosts;

  @OneToMany(mappedBy = "likedBy")
  private List<Like> likedPosts;

  @OneToMany(mappedBy = "bookmarkedBy")
  private List<Bookmark> bookmarkedPosts;

  @OneToMany(mappedBy = "sender")
  private List<Message> sentMessages;

  @OneToMany(mappedBy = "recipient")
  private List<Message> receivedMessages;

  private boolean isEnabled;
}