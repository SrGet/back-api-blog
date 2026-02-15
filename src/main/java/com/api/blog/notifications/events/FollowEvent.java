package com.api.blog.notifications.events;

import com.api.blog.Model.User;

public record FollowEvent(User follower, User followed) {
}
