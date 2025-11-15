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

        return existingUser;
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

}
