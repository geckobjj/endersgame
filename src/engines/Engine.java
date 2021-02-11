package engines;

import com.ender.game.model.Grid;
import com.ender.game.model.Player;

public interface Engine {
    public void execute(Player me, Grid grid);
}
