package com.epam.strategy;

import com.epam.entity.User;

@FunctionalInterface
public interface Strategy {
    User execute(User user);
}
