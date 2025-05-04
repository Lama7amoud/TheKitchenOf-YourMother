    package il.cshaifasweng.OCSFMediatorExample.entities;

    import javax.persistence.*;
    import java.io.Serial;
    import java.io.Serializable;
    import java.util.List;

    @Entity
    @Table(name = "restaurants")
    public class Restaurant implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        private String name;
        private String address;
        private String location;
        private String phoneNumber;
        private String holidays;
        private double openingTime;
        private double closingTime;


        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        private List<HostingTable> hostingTables;

        @ManyToMany(mappedBy = "restaurants")
        private List<Meal> meals;

        @ManyToOne
        @JoinColumn(name = "business_id")
        private Business business;


        public Restaurant() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getHolidays() {
            return holidays;
        }

        public void setHolidays(String holidays) {
            this.holidays = holidays;
        }

        public double getOpeningTime() {
            return openingTime;
        }

        public void setOpeningTime(double openingTime) {
            this.openingTime = openingTime;
        }

        public double getClosingTime() {
            return closingTime;
        }

        public void setClosingTime(double closingTime) {
            this.closingTime = closingTime;
        }

        public List<HostingTable> getHostingTables() {
            return hostingTables;
        }

        public void setHostingTables(List<HostingTable> hostingTables) {
            this.hostingTables = hostingTables;
        }

        public List<Meal> getMeals() {
            return meals;
        }

        public void setMeals(List<Meal> meals) {
            this.meals = meals;
        }

        public Business getBusiness() {
            return business;
        }

        public void setBusiness(Business business) {
            this.business = business;
        }


        public void addMeal(Meal meal) {
            this.meals.add(meal);
        }

        public void copyRestaurant(Restaurant restaurant) {
            this.setId(restaurant.getId());
            this.setName(restaurant.getName());
            this.setAddress(restaurant.getAddress());
            this.setLocation(restaurant.getLocation());
            this.setPhoneNumber(restaurant.getPhoneNumber());
            this.setHolidays(restaurant.getHolidays());
            this.setOpeningTime(restaurant.getOpeningTime());
            this.setClosingTime(restaurant.getClosingTime());
            this.setHostingTables(restaurant.getHostingTables());
            this.setMeals(restaurant.getMeals());
            this.setBusiness(restaurant.getBusiness());
        }

        public void resetAttributes() {
            this.setId(0);
            this.setName(null);
            this.setAddress(null);
            this.setLocation(null);
            this.setPhoneNumber(null);
            this.setHolidays(null);
            this.setOpeningTime(0.0);
            this.setClosingTime(0.0);
            this.setHostingTables(null);
            this.setMeals(null);
            this.setBusiness(null);
        }
    }
