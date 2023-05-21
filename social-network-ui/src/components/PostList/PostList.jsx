import { Typography } from '@mui/material';
import React from 'react';
import { useDispatch, useSelector } from 'react-redux';

import axiosIns from '../../axiosInstance';
import { getPostId, openReplayModal } from '../../features/slices/homeSlice';
import Post from '../Post/Post';
import { usePostStyle } from '../Post/PostStyle';
import ReplyHeader from '../Post/ReplyHeader';

export default function PostList() {
  const posts = useSelector((state) => state.home.post);

  const classes = usePostStyle();
  const dispatch = useDispatch();

  const handleRetweet = (id) => {
    axiosIns.post(
      '/api/posts',
      { originalPost: id },
      {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      }
    );
  };

  const handleReplay = (props) => {
    dispatch(openReplayModal(props));
  };

  const handelLike = (props) => {
    dispatch(openReplayModal(props));
    alert(`past ${props}  add to like`);
  };

  return (
    <div>
      {posts &&
        posts.map((post) => (
          <Post
            replay={
              post.originalPost ? (
                post.text === null && post.image === null ? (
                  <ReplyHeader repeat={post.author.name} />
                ) : (
                  <Typography className={classes.reply}>Reply</Typography>
                )
              ) : null
            }
            classes={classes.Page}
            key={post.id}
            username={post.author.username}
            avatar={post.author.profileImage}
            name={post.author.name}
            retweet={post.retweetsNumber}
            like={post.likesNumber}
            view={post.view}
            reply={post.repliesNumber}
            content={post.text}
            data={post.createdDate}
            image={post.image}
            originalPost={post.originalPost}
            handleClick={() => dispatch(getPostId(`${post.id}`))}
            handleClickLike={() => handelLike(`${post.id}`)}
            handleClickReplay={() => handleReplay(`${post.id}`)}
            handleClickRetweet={() => handleRetweet(`${post.id}`)}
          >
            {post.originalPost && (
              <Post
                classes={classes.PageSmall}
                key={post.id}
                username={post.originalPost.author.username}
                avatar={post.originalPost.author.profileImage}
                name={post.originalPost.author.name}
                retweet={post.originalPost.retweetsNumber}
                like={post.originalPost.likesNumber}
                view={post.originalPost.view}
                reply={post.originalPost.repliesNumber}
                content={post.originalPost.text}
                data={post.originalPost.createdDate}
                image={post.originalPost.image}
                handleClick={() => dispatch(getPostId(`${post.originalPost.id}`))}
                handleClickLike={() => handelLike(`${post.originalPost.id}`)}
                handleClickReplay={() => handleReplay(`${post.originalPost.id}`)}
                handleClickRetweet={() => handleRetweet(`${post.originalPost.id}`)}
              />
            )}
          </Post>
        ))}
    </div>
  );
}
