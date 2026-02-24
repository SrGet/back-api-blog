package com.api.blog.notifications.events;

import com.api.blog.Model.User;

public record LikePostEvent(User likedBy, User recipient) {
}
