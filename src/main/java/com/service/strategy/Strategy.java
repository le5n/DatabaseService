package com.service.strategy;


import com.chat.util.entity.User;

@FunctionalInterface
public interface Strategy {
    User execute(User user);
}
