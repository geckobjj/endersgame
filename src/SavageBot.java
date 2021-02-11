import com.ender.game.client.Bot;
import com.ender.game.client.EndersGameClient;
import com.ender.game.model.Grid;
import com.ender.game.model.Player;
import engines.CombatEngine;
import engines.EconomyEngine;

public class SavageBot implements Bot {

    @Override
    public String getName() {
        return "SavageBot"; //+ HelperUtils.generateSalt();
    }

    @Override
    public String getEmail() {
        return "geckobjj@gmail.com";
    }

    @Override
    public String getToken() {
        return "geckobjj@gmail.com";
    }

    @Override
    public void act(Player me, Grid grid) {
        EconomyEngine.getInstance().execute(me, grid);
        CombatEngine.getInstance().execute(me, grid); // Execute combat engine
    }

    public static void main(String[] args) {
        // Option 1: play against StupidBot
        EndersGameClient.run(new SavageBot(), "StupidBot")
           .openWebBrowserWhenMatchStarts();

        // Option 2: wait in a queue to play against other people's bots
        // EndersGameClient.run(new SavageBot())
        // .openWebBrowserWhenMatchStarts();
    }
}
