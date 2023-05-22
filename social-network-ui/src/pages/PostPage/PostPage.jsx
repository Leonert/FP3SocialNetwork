import { CircularProgress, Container, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';

import axiosIns from '../../axiosInstance';
import NotFound from '../../components/NotFound/NotFound';
import Post from '../../components/Post/Post';
import { usePostStyle } from '../../components/Post/PostStyle';
import ReplyHeader from '../../components/Post/ReplyHeader';
import { bookmarksPost, getPostId, likesPost, openReplayModal } from '../../features/slices/homeSlice';
import usePostPageStyles from './PostPageStyles';

const PostPage = () => {
  const postClasses = usePostStyle();
  const postPageClasses = usePostPageStyles();
  const posts = useSelector((state) => state.home.post);
  const dispatch = useDispatch();
  const { id } = useParams();
  const [isLoading, setIsLoading] = useState(true);
  const [post, setPost] = useState(null);

  useEffect(() => {
    const fetchPost = async () => {
      const response = await axiosIns.get(`/api/posts/${id}`);
      setIsLoading(false);
      setPost(response.data);
      dispatch(openReplayModal(id));

      return response;
    };

    fetchPost().catch(() => setIsLoading(false));
  }, [posts, id]);

  const handleRetweet = (id) => {
    axiosIns.post('/api/posts', { originalPost: id });
  };

  const handleReplay = (props) => {
    dispatch(openReplayModal(props));
  };

  const handleLike = async (props) => {
    await axiosIns.post(`/api/posts/${props}/likes`, {}).then((response) => {
      const LikeNumber = response.data;
      dispatch(
        likesPost({
          postId: props,
          likesNumber: LikeNumber,
        })
      );
    });
  };

  const handleBookmark = (props) => {
    axiosIns.post(`/api/posts/${props}/bookmarks`, {}).then((response) => {
      const bookmarksNum = response.data;
      dispatch(
        bookmarksPost({
          postId: props,
          bookmarksNumber: bookmarksNum,
        })
      );
    });
  };

  return (
    <Container maxWidth="sm" className={postPageClasses.container}>
      {isLoading && <CircularProgress />}
      {!isLoading && post && (
        <Post
          replay={
            post.originalPost ? (
              post.text === null && post.image === null ? (
                <ReplyHeader repeat={post.author.name} />
              ) : (
                <Typography className={postClasses.reply}>Reply</Typography>
              )
            ) : null
          }
          postPage={true}
          classes={postClasses.Page}
          key={post.id}
          username={post.author.username}
          avatar={post.author.profileImage}
          name={post.author.name}
          retweet={post.retweetsNumber}
          like={post.likesNumber}
          view={post.view}
          reply={post.repliesNumber}
          content={post.text}
          date={post.createdDate}
          image={post.image}
          bookmark={post.bookmarksNumber}
          liked={post.liked}
          bookmarked={post.bookmarked}
          retweeted={post.retweeted}
          originalPost={post.originalPost}
          handleClick={() => dispatch(getPostId(post.id))}
          handleClickLike={() => handleLike(post.id)}
          handleClickReplay={() => handleReplay(post.id)}
          handleClickRetweet={() => handleRetweet(post.id)}
          handleClickBookmark={() => handleBookmark(post.id)}
        >
          {post.originalPost && (
            <Post
              id={post.originalPost.id}
              classes={postClasses.PageSmall}
              key={post.id}
              username={post.originalPost.author.username}
              avatar={post.originalPost.author.profileImage}
              name={post.originalPost.author.name}
              retweet={post.originalPost.retweetsNumber}
              like={post.originalPost.likesNumber}
              view={post.originalPost.view}
              reply={post.originalPost.repliesNumber}
              content={post.originalPost.text}
              liked={post.originalPost.liked}
              bookmarked={post.originalPost.bookmarked}
              retweeted={post.originalPost.retweeted}
              date={post.originalPost.createdDate}
              image={post.originalPost.image}
              bookmark={post.originalPost.bookmarksNumber}
              handleClick={() => dispatch(getPostId(post.originalPost.id))}
              handleClickLike={() => handleLike(post.originalPost.id)}
              handleClickReplay={() => handleReplay(post.originalPost.id)}
              handleClickRetweet={() => handleRetweet(post.originalPost.id)}
              handleClickBookmark={() => handleBookmark(post.originalPost.id)}
            />
          )}
        </Post>
      )}
      {!isLoading && !post && <NotFound />}
    </Container>
  );
};

export default PostPage;