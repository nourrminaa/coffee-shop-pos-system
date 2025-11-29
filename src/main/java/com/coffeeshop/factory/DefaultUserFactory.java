package com.coffeeshop.factory;

import com.coffeeshop.models.*;

public class DefaultUserFactory implements IUserFactory {

    @Override
    public User create(int id, String username, String passwordHash, String displayName, String role) {
        if (role.equalsIgnoreCase("ADMIN")) {
            return new Admin(id, username, passwordHash, displayName);
        } else {
            return new Cashier(id, username, passwordHash, displayName);
        }
    }
}
