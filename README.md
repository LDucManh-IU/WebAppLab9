# Web-Application-Development-Lab-09

## LƯU ĐỨC MẠNH - ITITIU23016 

## LAB 9: SPRING SECURITY &amp; JWT AUTHENTICATION

TESTING AUTHENTICATION: 

- 7.1 Test Registration

```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
}

Expected: 201 Created
{
    "id": 4,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "role": "USER",
    "isActive": true,
    "createdAt": "2024-11-03T10:00:00"
}
```
![image](./Inclass/TestRegistration.png)

- 7.2 Test Login

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "password123"
}

Expected: 200 OK
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
}
```
![image](./Inclass/TestLogin.png)

- 7.3 Test Protected Endpoint (Without Token)

```
GET http://localhost:8080/api/customers

Expected: 401 Unauthorized
{
    "timestamp": "2024-11-03T10:05:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Authentication required. Please provide valid JWT token.",
    "path": "/api/customers"
}
```
![image](./Inclass/TestProtectedEndpoint(WithoutToken).png)

- 7.4 Test Protected Endpoint (With Token)

```
GET http://localhost:8080/api/customers
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Expected: 200 OK
[
    {
        "id": 1,
        "customerCode": "C001",
        "fullName": "John Doe",
        ...
    }
]
```
![image](./Inclass/TestProtectedEndpoint(WithToken).png)

- 7.5 Test Authorization (USER trying to DELETE)
```
DELETE http://localhost:8080/api/customers/1
Authorization: Bearer <USER_TOKEN>

Expected: 403 Forbidden
{
    "timestamp": "2024-11-03T10:10:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access denied. Insufficient permissions.",
    "path": "/api/customers/1"
}
```
![image](./Inclass/TestAuthorization(USERtrying%20toDELETE).png)

- 7.6 Test Authorization (ADMIN can DELETE)
```
DELETE http://localhost:8080/api/customers/1
Authorization: Bearer <ADMIN_TOKEN>

Expected: 200 OK
{
    "message": "Customer deleted successfully"
}
```
![image](./Inclass/TestAuthorization(ADMINcanDELETE).png)

# Homework

#### Ex 6: PASSWORD MANAGEMENT
- Task 6.1: Change Password Endpoint 

![Test 6.1](./Homework/6.1.png)

`ChangePasswordDTO.java` - DTO for password change requests: It contains 3 fields: current password, new password, and confirm password.
```java
public class ChangePasswordDTO {
    @NotBlank
    private String currentPassword;
    
    @NotBlank
    @Size(min = 6)
    private String newPassword;
    
    @NotBlank
    private String confirmPassword;
}
```

Endpoint in `AuthController.java`: Take the current user from the SecurityContext and call the service to change the password.
```java
@PutMapping("/change-password")
public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    userService.changePassword(username, dto);
    
    Map<String, String> response = new HashMap<>();
    response.put("message", "Password changed successfully");
    return ResponseEntity.ok(response);
}
```
Logic in `UserServiceImpl`.java: Verify the current password, check if the new password and confirm password match, and update the password.
```java
@Override
public void changePassword(String username, ChangePasswordDTO dto) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));    
    if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
        throw new IllegalArgumentException("Current password is incorrect");
    }
    if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
        throw new IllegalArgumentException("New password and confirm password do not match");
    }
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userRepository.save(user);
}
```
- Task 6.2: Forgot Password
![image](./Homework/6.2.png)

`ForgotPasswordDTO.java` - DTO for forgot password requests: It contains the email field.
```java
public class ForgotPasswordDTO {
    @NotBlank
    @Email
    private String email;
}
```
Endpoint in `AuthController.java`: Accept the email and call the service to handle the forgot password logic.
```java
@PostMapping("/forgot-password")
public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
    userService.forgotPassword(dto.getEmail());
    Map<String, String> response = new HashMap<>();
    response.put("message", "Password reset link sent to email if it exists");
    return ResponseEntity.ok(response);
}
```
Logic in `UserServiceImpl.java`: Generate a reset token, save it to the user, and simulate sending an email with the reset link.
```java
@Override
public void forgotPassword(String email) {
    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);
        
        // Simulate sending email
        System.out.println("Password reset link: http://localhost:8080/api/auth/reset-password?token=" + resetToken);
    }
}
```
`ResetPasswordDTO.java` - DTO for reset password requests: It contains the reset token, new password, and confirm password fields.
```java
public class ResetPasswordDTO {
    @NotBlank
    private String token;   
    @NotBlank
    @Size(min = 6)
    private String newPassword;    
    @NotBlank
    private String confirmPassword;
}
```
Endpoint in `AuthController.java`: Accept the reset token and new password, and call the service to reset the password.
```java
@PostMapping("/reset-password")
public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
    userService.resetPassword(dto);
    Map<String, String> response = new HashMap<>();
    response.put("message", "Password has been reset successfully");
    return ResponseEntity.ok(response);
}
```
Logic in `UserServiceImpl.java`: Validate the reset token, check if the new password and confirm password match, and update the password.
```java
@Override
public void resetPassword(ResetPasswordDTO dto) {
    User user = userRepository.findByResetToken(dto.getToken())
            .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));    
    if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
        throw new IllegalArgumentException("New password and confirm password do not match");
    }
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    user.setResetToken(null); // Clear the reset token
    userRepository.save(user);
}
``` 

#### EXERCISE 7: USER PROFILE MANAGEMENT

**Task 7.1: View Profile** - Endpoint in `UserController.java`: Get the current user's profile from the SecurityContext.
```java
@GetMapping("/profile")
public ResponseEntity<UserResponseDTO> getProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    UserResponseDTO user = userService.getCurrentUser(username);
    return ResponseEntity.ok(user);
}
```
![image](./Homework/7.1.png)
**Task 7.2: Update Profile** - `UpdateProfileDTO.java`: Contains full name and email fields.
```java
public class UpdateProfileDTO {
    @NotBlank
    private String fullName;
    
    @Email
    private String email;
}
```

Endpoint in `UserController.java`: Update the user's profile with new information.
```java
@PutMapping("/profile")
public ResponseEntity<UserResponseDTO> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    UserResponseDTO updatedUser = userService.updateProfile(username, dto);
    return ResponseEntity.ok(updatedUser);
}
```

Logic in `UserServiceImpl.java`: Check if email is changed and not already taken, then update the user's profile.
```java
@Override
public UserResponseDTO updateProfile(String username, UpdateProfileDTO dto) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
    if (!user.getEmail().equals(dto.getEmail())) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
    }
    
    user.setFullName(dto.getFullName());
    user.setEmail(dto.getEmail());
    User updatedUser = userRepository.save(user);
    return convertToDTO(updatedUser);
}
```
![image](./Homework/7.2.png)


**Task 7.3: Delete Account (Soft Delete)** - Endpoint in `UserController.java`: Verify password and deactivate the user account.
```java
@DeleteMapping("/account")
public ResponseEntity<Map<String, String>> deleteAccount(@RequestParam String password) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    userService.deleteAccount(username, password);
    
    Map<String, String> response = new HashMap<>();
    response.put("message", "Account deactivated successfully");
    return ResponseEntity.ok(response);
}
```

Logic in `UserServiceImpl.java`: Verify password and set isActive to false for soft delete.
```java
@Override
public void deleteAccount(String username, String password) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
    if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new IllegalArgumentException("Password is incorrect");
    }
    
    user.setIsActive(false);
    userRepository.save(user);
}
```
![image](./Homework/7.3.png)

#### EXERCISE 8: ADMIN ENDPOINTS

**Task 8.1: List All Users** - Endpoint in `AdminController.java`: Get all users (admin only).
```java
@GetMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
    List<UserResponseDTO> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
}
```

Logic in `UserServiceImpl.java`: Return all users from the database.
```java
@Override
public List<UserResponseDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}
```
![image](./Homework/8.1.png)


**Task 8.2: Update User Role** - `UpdateRoleDTO.java`: Contains the role field.
```java
public class UpdateRoleDTO {
    @NotNull
    private Role role;
}
```

Endpoint in `AdminController.java`: Update a user's role by ID.
```java
@PutMapping("/users/{id}/role")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<UserResponseDTO> updateUserRole(
        @PathVariable Long id,
        @Valid @RequestBody UpdateRoleDTO dto) {
    UserResponseDTO updatedUser = userService.updateUserRole(id, dto);
    return ResponseEntity.ok(updatedUser);
}
```

Logic in `UserServiceImpl.java`: Find the user and update their role.
```java
@Override
public UserResponseDTO updateUserRole(Long userId, UpdateRoleDTO dto) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
    user.setRole(dto.getRole());
    User updatedUser = userRepository.save(user);
    return convertToDTO(updatedUser);
}
```
![image](./Homework/8.2.png)


**Task 8.3: Toggle User Status** - Endpoint in `AdminController.java`: Toggle user's active status.
```java
@PatchMapping("/users/{id}/status")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<UserResponseDTO> toggleUserStatus(@PathVariable Long id) {
    UserResponseDTO updatedUser = userService.toggleUserStatus(id);
    return ResponseEntity.ok(updatedUser);
}
```

Logic in `UserServiceImpl.java`: Toggle the user's isActive status.
```java
@Override
public UserResponseDTO toggleUserStatus(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
    user.setIsActive(!user.getIsActive());
    User updatedUser = userRepository.save(user);
    return convertToDTO(updatedUser);
}
```
![image](./Homework/8.3.1.png)
![image](./Homework/8.3.2.png)


#### EXERCISE 9: REFRESH TOKEN

**Task 9.1: Create Refresh Token Entity** - `RefreshToken.java`: Entity to store refresh tokens.
```java
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(unique = true, nullable = false)
    private String token;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
```

![image](./Homework/9.1.png)