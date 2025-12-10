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