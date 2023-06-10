package com.socialnetwork.api.service.authorized;

import com.socialnetwork.api.exception.custom.NoPostWithSuchIdException;
import com.socialnetwork.api.exception.custom.NoUserWithSuchCredentialsException;
import com.socialnetwork.api.models.additional.View;
import com.socialnetwork.api.models.base.Hashtag;
import com.socialnetwork.api.models.base.Post;
import com.socialnetwork.api.models.base.User;
import com.socialnetwork.api.repository.PostRepository;
import com.socialnetwork.api.repository.ViewRepository;
import com.socialnetwork.api.service.CloudinaryService;
import com.socialnetwork.api.service.HashtagService;
import com.socialnetwork.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final UserService userService;
  private final CloudinaryService cloudinaryService;
  private final HashtagService hashtagService;
  private final NotificationService notificationService;
  private final ModelMapper modelMapper;
  private final ViewRepository viewRepository;

  public Post getReferenceById(int id) throws NoPostWithSuchIdException {
    if (!postRepository.existsById(id)) {
      throw new NoPostWithSuchIdException();
    }
    return postRepository.getReferenceById(id);
  }

  public Post save(Post post, String image) throws NoPostWithSuchIdException {
    post.setCreatedDate(LocalDateTime.now());

    if (image != null) {
      post.setImage(cloudinaryService.uploadPostPic(image, String.valueOf(post.getId())));
    }

    findAllHashtags(post.getText()).forEach(h ->
            hashtagService.findByName(h).ifPresentOrElse(
              hashtag -> {
                hashtag.setQuantity(hashtag.getQuantity() + 1);
                hashtagService.save(hashtag);
              },
              () -> hashtagService.save(new Hashtag(h, 1))
            )
    );

    postRepository.save(post);
    notificationService.saveReplyRetweet(post);
    return post;
  }

  private Set<String> findAllHashtags(String text) {
    Set<String> hashtags = new HashSet<>();
    Pattern pattern = Pattern.compile("#\\w+");
    Matcher matcher = pattern.matcher(text);

    while (matcher.find()) {
      hashtags.add(matcher.group());
    }

    return hashtags;
  }

  public Post edit(Post editedPost, Post originalPost) {
    modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    modelMapper.map(editedPost, originalPost);
    postRepository.save(originalPost);
    return originalPost;
  }

  public void delete(Post post) {
    postRepository.delete(post);
  }

  public List<Post> getPosts(int page, int postsNumber) {
    return postRepository.findAll(PageRequest.of(page, postsNumber, Sort.by(Sort.Direction.DESC, "createdDate"))).toList();
  }

  public List<Post> getUnviewedPosts(int page, int postsNumber, String currentUserUsername)
          throws NoUserWithSuchCredentialsException {
    return postRepository.findAllPostsUnViewedByUser(userService.findByUsername(currentUserUsername).getId(),
            PageRequest.of(page, postsNumber, Sort.by(Sort.Direction.DESC, "createdDate"))
    );
  }

  public List<Post> getReplies(int postId, int page, int usersForPage)
          throws NoPostWithSuchIdException {
    Post post = getReferenceById(postId);
    return postRepository.findAllByOriginalPostAndTextIsNotNull(
            post,
            PageRequest.of(page, usersForPage, Sort.by(Sort.Direction.DESC, "createdDate")));
  }

  public List<User> getRetweets(int id, String currentUserUsername, int page, int usersForPage)
          throws NoUserWithSuchCredentialsException, NoPostWithSuchIdException {
    if (!postRepository.existsById(id)) {
      throw new NoPostWithSuchIdException();
    }
    User currentUser = userService.findByUsername(currentUserUsername);
    return postRepository.findUsersByRetweetedPost(id)
            .stream()
            .skip(page * usersForPage).limit(usersForPage)
            .peek(f -> f.setCurrUserFollower(userService.isFollowed(currentUser, f)))
            .toList();
  }

  public boolean existsById(Integer postId) {
    return postRepository.existsById(postId);
  }

  public int countPostRetweets(int id) {
    return postRepository.countAllByOriginalPostAndTextNullAndImageNull(new Post(id));
  }

  public int countPostReplies(int id) {
    return postRepository.countAllByOriginalPostAndTextNotNullAndImageNull(new Post(id));
  }

  public void deleteUserRetweet(int userId, int postId) {
    postRepository.deletePostByAuthorAndOriginalPostAndTextIsNullAndImageIsNull(new User(userId), new Post(postId));
  }

  public void saveView(User currentUser, int postId) {
    viewRepository.save(new View(currentUser, new Post(postId)));
  }

  public boolean isRetweetedByUser(int userId, int postId) {
    return postRepository.existsByAuthorAndOriginalPostAndTextIsNullAndImageIsNull(new User(userId), new Post(postId));
  }
}
