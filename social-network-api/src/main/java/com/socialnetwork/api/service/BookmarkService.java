package com.socialnetwork.api.service;

import com.socialnetwork.api.models.additional.Bookmark;
import com.socialnetwork.api.models.additional.keys.BookmarkPk;
import com.socialnetwork.api.models.base.Post;
import com.socialnetwork.api.models.base.User;
import com.socialnetwork.api.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {
  private final BookmarkRepository bookmarkRepository;

  public void save(int userId, int postId) {
    bookmarkRepository.save(new Bookmark(new User(userId), new Post(postId)));
  }

  public void delete(int userId, int postId) {
    bookmarkRepository.deleteByBookmarkedByAndBookmarkedPost(new User(userId), new Post(postId));
  }

  public boolean existsByIds(int userId, int postId) {
    return bookmarkRepository.existsByBookmarkPk(new BookmarkPk(userId, postId));
  }

  public Optional<Bookmark> findByUserAndPost(User user, Post post) {
    return bookmarkRepository.findByBookmarkedByAndBookmarkedPost(user, post);
  }

  public List<Integer> getUsersBookmarksIds(Post post) {
    return bookmarkRepository.findAllByBookmarkedPost(post)
            .stream()
            .map(bookmark -> bookmark.getBookmarkedBy().getId())
            .toList();
  }

  public int countPostBookmarks(Post post) {
    return bookmarkRepository.countAllByBookmarkedPost(post);
  }
}
