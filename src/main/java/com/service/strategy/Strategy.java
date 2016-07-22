package com.service.strategy;

import com.service.util.entity.User;

@FunctionalInterface
public interface Strategy {
    User execute(User user);
}
