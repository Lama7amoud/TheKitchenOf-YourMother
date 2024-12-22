package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.Serializable;

public class TurnInfo implements Serializable {
    private int btnIndex;
    private String playerSymbol;


    public TurnInfo(int btnIndex, String playerSymbol) {
        this.btnIndex = btnIndex;
        this.playerSymbol = playerSymbol;
    }
    public int getBtnIndex() {
        return btnIndex;
    }

    public String getPlayerSymbol() {
        return playerSymbol;
    }
}