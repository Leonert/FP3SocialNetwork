package com.socialnetwork.api.service;

import com.socialnetwork.api.dto.BookmarkDto;
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

  public void save(User user, Post post) {
    Bookmark bookmark = new Bookmark();
    BookmarkPk bookmarkPk = new BookmarkPk();

    bookmarkPk.setUserId(user.getId());
    bookmarkPk.setPostId(post.getId());

    bookmark.setBookmarkPk(bookmarkPk);
    bookmark.setBookmarkedBy(user);
    bookmark.setBookmarkedPost(post);

    bookmarkRepository.save(bookmark);
  }

  public void delete(Bookmark bookmark) {
    bookmarkRepository.delete(bookmark);
  }

  public boolean existsByUserAndPost(User user, Post post) {
    return bookmarkRepository.existsByBookmarkedByAndBookmarkedPost(user, post);
  }

  public Optional<Bookmark> findByUserAndPost(User user, Post post) {
    return bookmarkRepository.findByBookmarkedByAndBookmarkedPost(user, post);
  }

  // returns a list of ids of users who bookmarked that post
  public List<Integer> getUsersBookmarksIdsForPost(Post post) {
    return bookmarkRepository.findAllByBookmarkedPost(post)
            .stream()
            .map(bookmark -> bookmark.getBookmarkedBy().getId())
            .toList();
  }

  public BookmarkDto.Response.BookmarksList getUsersIdsDtoForPost(Post post) {
    BookmarkDto.Response.BookmarksList bookmarksList = new BookmarkDto.Response.BookmarksList();
    List<Integer> usersBookmarksIds = getUsersBookmarksIdsForPost(post);
    bookmarksList.setBookmarks(usersBookmarksIds);
    return bookmarksList;
  }
}
