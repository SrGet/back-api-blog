package com.api.blog.Utils;

public final class RedisKeys {

    private RedisKeys() {
    }

    public static String postsAmount(Long userId){
        return "posts:amount:" + userId;
    }

    public static String postLikesAmount(Long userId){
        return "post:likes:amount:" + userId;
    }

    public static String followingAmount(Long userId){
        return "following:amount:" + userId;
    }

    public static String followersAmount(Long userId){
        return "followers:amount:" + userId;
    }

    public static String commentLikesAmount(Long commentId){
        return "comment:likes:amount:" + commentId;
    }

    public static String notificationsUnread(Long userId){
        return "notifications:unread:" + userId;
    }

}
