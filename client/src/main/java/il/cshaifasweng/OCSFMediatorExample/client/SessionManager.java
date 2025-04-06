package il.cshaifasweng.OCSFMediatorExample.client;

public class SessionManager {
    private static int loggedInUserId;
    private static int loggedInUserPermissionLevel;
    private static String loggedInUsername;
    private static String password;
    private static int userId;
    private static int permissionLevel;

    public static void setLoggedInUser(int userId, String username, int permissionLevel) {
        loggedInUserId = userId;
        loggedInUsername = username;
        loggedInUserPermissionLevel = permissionLevel;
    }

    public static void setSession(String password, int userId, int permissionLevel) {
        SessionManager.password = password;
        SessionManager.userId = userId;
        SessionManager.permissionLevel = permissionLevel;
    }

    public static String getPassword() {
        return password;
    }

    public static int getUserId() {
        return userId;
    }

    public static int getPermissionLevel() {
        return permissionLevel;
    }

    public static void clearSession() {
        password = null;
        userId = -1;
        permissionLevel = -1;
    }
}

