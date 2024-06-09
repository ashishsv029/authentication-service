package com.meghasandesham.authenticationservice.controller;

import com.meghasandesham.authenticationservice.dto.*;
import com.meghasandesham.authenticationservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private AuthService authService;

    @PostMapping("/register") //deals with form data content type as users image is also sent
    public ResponseEntity<RegisterResponseDTO> register(@RequestParam("name") String name,
                                                        @RequestParam("email") String email,
                                                        @RequestParam("password") String password,
                                                        @RequestParam("profile_pic") MultipartFile profilePic) throws Exception {
        RegisterRequestDTO registerPayload = new RegisterRequestDTO(null, name, email, password, profilePic, null, null);
        RegisterResponseDTO response = authService.registerUser(registerPayload);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginPayload, HttpServletResponse response) throws Exception {
        LoginResponseDTO authResponse = authService.loginUser(loginPayload);
        // We can add a cookie in response in 2 ways. 1. using response.addCookie 2. using response.setHeader("Set-Cookie", <value>);
        // The javax.servlet.http.Cookie class does not have a built-in method setSameSite because the SameSite attribute was introduced after the standard Java Cookie class was designed. To set the SameSite attribute in a cookie, you have to manually add it to the Set-Cookie header in the HTTP response.
        Cookie cookie = new Cookie("authToken", authResponse.getJwt());
        cookie.setHttpOnly(true); // So that this cookie cant be accessed by client side javascript code and http cookies cant be set by client
        //cookie.setSecure(true); // Use true if your site is HTTPS
        cookie.setPath("/");
        //cookie.setDomain("localhost"); // Set domain to your main application, the cookie is sent to only this domain and its subdomains
        //the above field to be set when the chat application is in other domain and not on the domain in which auth service is there..
        cookie.setMaxAge(24 * 60 * 60); // 1 day expiry -> not a session cookie, i.e will be sustained by browser for 1 day
        // Browsers typically store persistent cookies in a designated location on the user's device, often in a text file called a "cookie jar" or "cookie store." The exact location varies depending on the browser and operating system.
        // The default behavior for cookies without a SameSite attribute is equivalent to SameSite=Lax. To specify another value we need to do that only via the header like below
        //response.setHeader("Set-Cookie", String.format("authToken=%s; HttpOnly; SameSite=None;", authResponse.getJwt())); //without this header browser wont set the cookie
        response.addCookie(cookie);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponseDTO> login(@RequestBody ValidationRequestDTO validationPayload, HttpServletRequest request) throws Exception {
        System.out.println("VALIDATE API CALL...");
        if(Objects.equals(validationPayload.getJwt(), "")) { // when jwt is not sent in payload, trying to extract from request cookies
            Cookie[] cookies = request.getCookies();
            String jwtFromAuthCookie = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("authToken".equals(cookie.getName())) { // Replace "authToken" with the name of your cookie
                        jwtFromAuthCookie = cookie.getValue();
                        break;
                    }
                }
            }

            if(jwtFromAuthCookie == null) {
                throw new Exception("JWT Not Found..");
            }
            validationPayload.setJwt(jwtFromAuthCookie);
        }
        ValidationResponseDTO response = authService.validateUser(validationPayload);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

/*
Some info about SameSite property of cookie

The SameSite attribute in cookies is used to control how cookies are sent with cross-site requests. It can take three different values:

Strict: When set to Strict, the cookie is only sent in a first-party context. This means that the cookie will only be sent along with same-site requests, and it will not be sent with cross-site requests. This is the most secure option.

Lax: When set to Lax, the cookie is sent along with same-site requests and with cross-site navigation (e.g., when clicking on a link). However, the cookie is not sent with cross-site requests that are triggered by submitting forms, loading resources (images, scripts, etc.), or making AJAX requests. This provides a balance between security and usability.

None: When set to None, the cookie is sent with both same-site and cross-site requests. This is used when the cookie needs to be sent with cross-site requests, such as in the case of Single Sign-On (SSO) systems or when implementing OAuth flows. When setting SameSite=None, it's important to also set the Secure attribute to true to ensure that the cookie is only sent over HTTPS connections.

 Clicking a Link vs Making a HTTP/Ajax call

When a user clicks on a link, the browser navigates to the URL specified in the link's href attribute.
This navigation is considered a cross-site navigation if the target URL is different from the current page's origin.
Cookies with SameSite=Lax or SameSite=None are sent along with cross-site navigation by default.

When JavaScript code initiates a fetch or AJAX call, the browser sends an HTTP request to the specified URL.
This request is considered a cross-site request if the target URL is different from the current page's origin
By default, cookies with SameSite=Lax or SameSite=None are not sent with cross-site requests made via fetch or XMLHttpRequest (XHR).
To include cookies with such requests, the request must explicitly set the credentials option to include.

 When a cookie has its SameSite attribute set to Strict, it will not be sent with cross-site requests, even if credentials: 'include' is specified in the request options.
 */
