package il.cshaifasweng.OCSFMediatorExample.entities;

public class Employee {
        private int id;
        private String fullName;
        private String username;
        private String password;
        private Integer role;
        private Integer restaurantId; // null or 0 means "all restaurants"

        public Employee(int id, String fullName, String username, String password, Integer role, Integer restaurantId) {
            this.id = id;
            this.fullName = fullName;
            this.username = username;
            this.password = password;
            this.role = role;
            this.restaurantId = restaurantId;
        }

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public Integer getRole() { return role; }
        public void setRole(Integer role) { this.role = role; }

        public Integer getRestaurantId() { return restaurantId; }
        public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

}
