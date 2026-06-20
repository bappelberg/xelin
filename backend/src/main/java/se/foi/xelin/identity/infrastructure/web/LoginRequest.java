package se.foi.xelin.identity.infrastructure.web;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest() {} // Denna MÅSTE finnas här för att Spring/Jackson ska kunna skapa objektet automatiskt

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters - används av din AuthController
    public String getUsername() {
        return username;
    }

    // Setters - används av Spring/Jackson i bakgrunden vid JSON-inläsning för att omvandla jsontext till java objekt LoginRequest. Läser nyckeln username och letar efter metod som heter setUsername()
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
