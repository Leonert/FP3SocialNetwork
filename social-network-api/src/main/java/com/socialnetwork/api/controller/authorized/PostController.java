package com.socialnetwork.api.controller.authorized;

import com.socialnetwork.api.dto.authorized.PostDto;
import com.socialnetwork.api.dto.authorized.UserDto;
import com.socialnetwork.api.exception.custom.AccessDeniedException;
import com.socialnetwork.api.exception.custom.NoPostWithSuchIdException;
import com.socialnetwork.api.exception.custom.NoUserWithSuchCredentialsException;
import com.socialnetwork.api.mapper.authorized.PostMapper;
import com.socialnetwork.api.mapper.authorized.UserMapper;
import com.socialnetwork.api.mapper.noneauthorized.NonAuthPostMapper;
import com.socialnetwork.api.models.base.Post;
import com.socialnetwork.api.models.base.User;
import com.socialnetwork.api.security.JwtTokenUtil;
import com.socialnetwork.api.service.BookmarkService;
import com.socialnetwork.api.service.authorized.LikeService;
import com.socialnetwork.api.service.authorized.PostService;
import com.socialnetwork.api.service.authorized.UserService;
import com.socialnetwork.api.service.noneauthorized.NonAuthPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.socialnetwork.api.util.Constants.Auth.AUTHORIZATION_HEADER;
import static com.socialnetwork.api.util.Constants.Auth.ID_QUERY;
import static com.socialnetwork.api.util.Constants.Auth.PAGE_NUMBER_QUERY;
import static com.socialnetwork.api.util.Constants.Auth.RESULTS_PER_PAGE_QUERY;
import static com.socialnetwork.api.util.Constants.Response.PAGE_NUMBER_DEFAULT;
import static com.socialnetwork.api.util.Constants.Response.POSTS_PER_PAGE_DEFAULT;
import static com.socialnetwork.api.util.Constants.Response.RESULTS_PER_PAGE_DEFAULT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
  private final PostService postService;
  private final LikeService likeService;
  private final BookmarkService bookmarkService;
  private final UserService userService;
  private final PostMapper postMapper;
  private final UserMapper userMapper;
  private final JwtTokenUtil jwtTokenUtil;

  @GetMapping("/{id}")
  public PostDto.Response.WithAuthor getPostById(@PathVariable(ID_QUERY) Integer id,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response)
      throws NoPostWithSuchIdException, NoUserWithSuchCredentialsException, IOException {
    if (!jwtTokenUtil.isAuthTokenExists(request)) {
      response.sendRedirect("unauth/" + id);
      return null;
    }
    return postMapper.convertToPostDtoDefault(postService.getReferenceById(id),
            jwtTokenUtil.getUsernameFromToken(request.getHeader(AUTHORIZATION_HEADER)));
  }

  @GetMapping("")
  public List<PostDto.Response.WithAuthor>
    getFeed(@RequestParam(PAGE_NUMBER_QUERY) Optional<Integer> page,
            @RequestParam(RESULTS_PER_PAGE_QUERY) Optional<Integer> postsPerPage,
            @RequestParam("viewed") Optional<Boolean> showViewedPosts,
            HttpServletRequest request, HttpServletResponse response)
      throws NoUserWithSuchCredentialsException, NoPostWithSuchIdException, IOException {
    int pageD = page.orElse(PAGE_NUMBER_DEFAULT);
    int resultsD = postsPerPage.orElse(POSTS_PER_PAGE_DEFAULT);
    if (!jwtTokenUtil.isAuthTokenExists(request)) {
      response.sendRedirect("posts/unauth?" + PAGE_NUMBER_QUERY + "=" + pageD
          + "&" + RESULTS_PER_PAGE_QUERY + "=" + resultsD);
      return null;
    }
    String username = jwtTokenUtil.getUsernameFromToken(request.getHeader(AUTHORIZATION_HEADER));
    List<PostDto.Response.WithAuthor> outcome = new ArrayList<>();
    List<Post> posts = showViewedPosts.orElse(false)
        ? postService.getPosts(pageD, resultsD) :
        postService.getUnviewedPosts(pageD, resultsD, username);
    for (Post post: posts) {
      outcome.add(postMapper.convertToPostDtoDefault(post, username));
    }
    return outcome;
  }

  @GetMapping("/{id}/replies")
  public List<PostDto.Response.WithAuthor> getReplies(
          @PathVariable("id") int id, @RequestParam(PAGE_NUMBER_QUERY) Optional<Integer> page,
          @RequestParam(RESULTS_PER_PAGE_QUERY) Optional<Integer> usersForPage,
          HttpServletRequest request, HttpServletResponse response) throws NoPostWithSuchIdException, IOException {
    int pageD = page.orElse(PAGE_NUMBER_DEFAULT);
    int resultsD = usersForPage.orElse(RESULTS_PER_PAGE_DEFAULT);
    if (!jwtTokenUtil.isAuthTokenExists(request)) {
      response.sendRedirect("/api/posts/unauth/" + id + "/replies" + "?"
          + PAGE_NUMBER_QUERY + "=" + pageD + "&" + RESULTS_PER_PAGE_QUERY + "=" + resultsD);
      return null;
    }
    return postMapper.mapForListing(postService.getReplies(id, pageD, resultsD),
            jwtTokenUtil.getUsernameFromToken(request.getHeader(AUTHORIZATION_HEADER)));
  }

  @GetMapping("/{id}/retweets")
  public List<UserDto.Response.Listing> getRetweets(
          @PathVariable("id") int id,
          @RequestParam(PAGE_NUMBER_QUERY) Optional<Integer> page,
          @RequestParam(RESULTS_PER_PAGE_QUERY) Optional<Integer> usersForPage,
          HttpServletRequest request, HttpServletResponse response)
      throws NoUserWithSuchCredentialsException, IOException {
    int pageD = page.orElse(PAGE_NUMBER_DEFAULT);
    int resultsD = usersForPage.orElse(RESULTS_PER_PAGE_DEFAULT);
    if (!jwtTokenUtil.isAuthTokenExists(request)) {
      response.sendRedirect("/api/posts/unauth/" + id + "/retweets" + "?"
          + PAGE_NUMBER_QUERY + "=" + pageD + "&" + RESULTS_PER_PAGE_QUERY + "=" + resultsD);
      return null;
    }
    String currentUserUsername = jwtTokenUtil.getUsernameFromToken(request.getHeader(AUTHORIZATION_HEADER));
    return userMapper.mapForListing(postService.getRetweets(id,
            currentUserUsername, pageD, resultsD), currentUserUsername);
  }

  @GetMapping("/{id}/likes")
  public List<UserDto.Response.Listing> getLikes(
          @PathVariable("id") int id,
          @RequestParam(PAGE_NUMBER_QUERY) Optional<Integer> page,
          @RequestParam(RESULTS_PER_PAGE_QUERY) Optional<Integer> usersForPage,
          HttpServletRequest request, HttpServletResponse response)
      throws NoUserWithSuchCredentialsException, IOException {
    int pageD = page.orElse(PAGE_NUMBER_DEFAULT);
    int resultsD = usersForPage.orElse(RESULTS_PER_PAGE_DEFAULT);
    if (!jwtTokenUtil.isAuthTokenExists(request)) {
      response.sendRedirect("/api/posts/unauth/" + id + "/likes" + "?"
          + PAGE_NUMBER_QUERY + "=" + pageD + "&" + RESULTS_PER_PAGE_QUERY + "=" + resultsD);
      return null;
    }
    String currentUserUsername = jwtTokenUtil.getUsernameFromToken(request.getHeader(AUTHORIZATION_HEADER));
    return userMapper.mapForListing(likeService.getLikes(id,
            currentUserUsername, pageD, resultsD), currentUserUsername);
  }

  @PostMapping()
  public ResponseEntity<Integer> addPost(@RequestBody PostDto.Request.Created postDto, HttpServletRequest request)
          throws NoPostWithSuchIdException, AccessDeniedException {
    User user = userService.getReferenceById(postDto.getUser().getId());
    checkAuthentication(user, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(postService.save(postMapper.convertToPost(postDto, user)));
  }

  @PatchMapping()
  public ResponseEntity<?> editPost(@RequestBody PostDto.Request.Editable postDto, HttpServletRequest request)
          throws NoPostWithSuchIdException, AccessDeniedException {
    Post post = postService.getReferenceById(postDto.getId());
    checkAuthentication(post, request);
    postService.edit(postMapper.convertToPost(postDto), post);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("{id}")
  public ResponseEntity<?> deletePostById(@PathVariable(ID_QUERY) int id, HttpServletRequest request)
          throws NoPostWithSuchIdException, AccessDeniedException {
    Post post = postService.getReferenceById(id);
    checkAuthentication(post, request);
    postService.delete(post);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/{id}/bookmarks")
  public ResponseEntity<Integer> bookmarkUnbookmark(@PathVariable(ID_QUERY) int postId,
                                                    HttpServletRequest request) throws NoUserWithSuchCredentialsException {
    return ResponseEntity
            .status(bookmarkService.bookmarkUnBookmark(postId, jwtTokenUtil.getUsernameFromToken(
                    request.getHeader(AUTHORIZATION_HEADER))) ? HttpStatus.OK : HttpStatus.CREATED)
            .body(bookmarkService.countPostBookmarks(new Post(postId)));
  }

  @PostMapping("/{id}/likes")
  public ResponseEntity<Integer> saveLike(@PathVariable(ID_QUERY) int postId, HttpServletRequest request)
          throws NoUserWithSuchCredentialsException {
    return ResponseEntity
            .status(likeService.likeUnlike(userService.findByUsername(jwtTokenUtil.getUsernameFromToken(
                    request.getHeader(AUTHORIZATION_HEADER))).getId(),
                    postId) ? HttpStatus.CREATED : HttpStatus.OK)
            .body(likeService.countPostLikes(new Post(postId)));
  }

  @PostMapping("/{id}/views")
  public void saveView(@PathVariable(ID_QUERY) int postId, HttpServletRequest request)
          throws NoUserWithSuchCredentialsException {
    postService.saveView(userService.findByUsername(
        jwtTokenUtil.getUsernameFromToken(request.getHeader(AUTHORIZATION_HEADER))), postId);
  }

  private void checkAuthentication(Post post, HttpServletRequest request) throws AccessDeniedException {
    if (!jwtTokenUtil.getUsernameFromToken(
        request.getHeader(AUTHORIZATION_HEADER)).equals(post.getAuthor().getUsername())) {
      throw new AccessDeniedException();
    }
  }

  private void checkAuthentication(User user, HttpServletRequest request) throws AccessDeniedException {
    if (!jwtTokenUtil.getUsernameFromToken(
        request.getHeader(AUTHORIZATION_HEADER)).equals(user.getUsername())) {
      throw new AccessDeniedException();
    }
  }
}
