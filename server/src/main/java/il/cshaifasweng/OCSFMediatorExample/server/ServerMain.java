package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        try {
            App.main(args);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
