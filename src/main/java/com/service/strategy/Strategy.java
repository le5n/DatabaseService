package com.service.strategy;

import com.service.entity.User;

@FunctionalInterface
public interface Strategy {
    User execute(User user);
}
