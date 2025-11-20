package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.User;
import com.example.demo.Repo.AdminRepo;
import com.example.demo.Repo.UserRepo;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AdminRepo adminRepo;

    public User registerUser(User user) {
        User existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists!");
        }
        user.setRole("USER");
        return userRepo.save(user);
    }

    public User loginUser(User user) {
        User existingUser = userRepo.findByEmail(user.getEmail());

        if (existingUser == null) {
            throw new RuntimeException("User with email " + user.getEmail() + " not found!");
        }

        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }
        existingUser.setAuthToken(generateAuthToken());

        return userRepo.save(existingUser);
    }

    // Google login: create or fetch user by email (no password check)
    public User googleLoginOrRegister(String email, String fullName, String pictureUrl) {
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Google login failed: email is empty");
        }

        User existingUser = userRepo.findByEmail(email);
        if (existingUser != null) {
            existingUser.setAuthToken(generateAuthToken());
            return userRepo.save(existingUser);
        }

        User newUser = new User();

        // Split full name into first and last name (simple logic)
        String firstName = fullName;
        String lastName = "";
        if (fullName != null && !fullName.isEmpty()) {
            String[] parts = fullName.trim().split(" ", 2);
            firstName = parts[0];
            if (parts.length > 1) {
                lastName = parts[1];
            }
        }

        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(null); // no password for Google-only accounts
        newUser.setRole("USER");
        newUser.setPhone(null);
        newUser.setProfile(pictureUrl);

        newUser.setAuthToken(generateAuthToken());

        return userRepo.save(newUser);
    }

    public User findUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        return userRepo.findByEmail(email);
    }

    public Admin createAdmin(Admin admin) {

        Admin existingAdmin = this.adminRepo.findByEmail(admin.getEmail());
        if (existingAdmin != null) {
            throw new RuntimeException("Admin With Email " + admin.getEmail() + " already exists");
        }
        admin.setRole("ADMIN");
        return adminRepo.save(admin);
    }

    public Admin adminLogin(Admin admin) {
        Admin existingAdmin = adminRepo.findByEmail(admin.getEmail());

        if (existingAdmin == null) {
            throw new RuntimeException("User with email " + admin.getEmail() + " not found!");
        }

        if (!existingAdmin.getPassword().equals(admin.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        return existingAdmin;
    }

    private static final String AUTH_TOKEN_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int AUTH_TOKEN_LENGTH = 32;

    private String generateAuthToken() {
        StringBuilder tokenBuilder = new StringBuilder(AUTH_TOKEN_LENGTH);
        for (int i = 0; i < AUTH_TOKEN_LENGTH; i++) {
            int index = (int) (Math.random() * AUTH_TOKEN_CHARACTERS.length());
            tokenBuilder.append(AUTH_TOKEN_CHARACTERS.charAt(index));
        }
        return tokenBuilder.toString();
    }

}
