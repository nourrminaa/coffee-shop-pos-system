package com.coffeeshop.factory;

import com.coffeeshop.models.User;

public interface IUserFactory {
    User create(int id, String username, String passwordHash, String displayName, String role);
}
