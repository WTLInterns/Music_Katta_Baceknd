package com.example.demo.Controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.User;
import com.example.demo.Service.AuthService;

// @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/auth/google")
    public RedirectView redirectToGoogle() {
        String scope = URLEncoder.encode("openid email profile", StandardCharsets.UTF_8);
        String redirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        String url = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirect +
                "&response_type=code" +
                "&scope=" + scope;

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }

    @GetMapping("/auth/google/callback")
    public RedirectView handleGoogleCallback(@RequestParam("code") String code, Model model) {
        // 1) Exchange authorization code for tokens using RestTemplate
        String tokenRequestBody = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> tokenRequest = new HttpEntity<>(tokenRequestBody, headers);

        ResponseEntity<Map> tokenEntity = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                tokenRequest,
                Map.class);

        Map<String, Object> tokenResponse = tokenEntity.getBody();

        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            RedirectView errorView = new RedirectView();
            errorView.setUrl("https://ddhavalmulay.com/login/callback?error=google_token_failed");
            return errorView;
        }

        String accessToken = (String) tokenResponse.get("access_token");

        // 2) Fetch user info from Google using access token
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfoEntity = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                userInfoRequest,
                Map.class);

        Map<String, Object> userInfo = userInfoEntity.getBody();

        String email = userInfo != null && userInfo.get("email") != null ? userInfo.get("email").toString() : "";
        String name = userInfo != null && userInfo.get("name") != null ? userInfo.get("name").toString() : "";
        String picture = userInfo != null && userInfo.get("picture") != null ? userInfo.get("picture").toString()
                : null;

        // 3) Create or fetch user in DB using AuthService
        User appUser = authService.googleLoginOrRegister(email, name, picture);

        // 4) Redirect back to frontend with user info in query params (NO JWT)
        StringBuilder frontendUrlBuilder = new StringBuilder("https://ddhavalmulay.com/login/callback");
        frontendUrlBuilder.append("?email=").append(URLEncoder.encode(appUser.getEmail(), StandardCharsets.UTF_8));

        String fullName = (appUser.getFirstName() != null ? appUser.getFirstName() : "") +
                (appUser.getLastName() != null && !appUser.getLastName().isEmpty() ? (" " + appUser.getLastName())
                        : "");
        frontendUrlBuilder.append("&name=").append(URLEncoder.encode(fullName, StandardCharsets.UTF_8));

        // Always add profile parameter, even if null
        String profileParam = appUser.getProfile() != null ? appUser.getProfile() : "";
        frontendUrlBuilder.append("&profile=").append(URLEncoder.encode(profileParam, StandardCharsets.UTF_8));

        // Add authToken so frontend can store it
        String authTokenParam = appUser.getAuthToken() != null ? appUser.getAuthToken() : "";
        frontendUrlBuilder.append("&authToken=")
                .append(URLEncoder.encode(authTokenParam, StandardCharsets.UTF_8));

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(frontendUrlBuilder.toString());
        return redirectView;
    }

    // Simple API to fetch user details by email for the frontend profile page
    @GetMapping("/api/users/by-email")
    @ResponseBody
    public ResponseEntity<User> getUserByEmail(@RequestParam("email") String email) {
        User user = authService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // ---------------manually login--------------------------

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return this.authService.registerUser(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        return this.authService.loginUser(user);
    }

    @PostMapping("/create-admin")
    public Admin createAdmin(@RequestBody Admin admin) {
        return this.authService.createAdmin(admin);
    }

    @PostMapping("/login-admin")
    public Admin adminLogin(@RequestBody Admin admin) {
        return this.authService.adminLogin(admin);
    }

}
