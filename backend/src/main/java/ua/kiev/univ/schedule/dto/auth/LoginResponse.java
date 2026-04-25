package ua.kiev.univ.schedule.dto.auth;

public class LoginResponse {
    private String token;
    private String role;

    public LoginResponse() {}

    public LoginResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }

    public static class LoginResponseBuilder {
        private String token;
        private String role;

        public LoginResponseBuilder token(String token) { this.token = token; return this; }
        public LoginResponseBuilder role(String role) { this.role = role; return this; }
        public LoginResponse build() { return new LoginResponse(token, role); }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
