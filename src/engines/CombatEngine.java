package engines;

import com.ender.game.model.*;

import java.util.List;

import static utilities.HelperUtils.*;

public class CombatEngine implements Engine{

    private static CombatEngine combatEngine = null;

    List<Unit> warriors;
    List<Unit> archers;
    Player me;
    Grid grid;

    private CombatEngine () {
    }

    public static CombatEngine getInstance() {
        if(combatEngine == null)
            combatEngine = new CombatEngine();

        return combatEngine;
    }

    @Override
    public void execute(Player me, Grid grid) {
        warriors = getUnitsByType(me, grid, UnitType.WARRIOR);
        this.me = me;
        this.grid = grid;
        moveWarriors();
    }

    private void moveWarriors() {
        if (!warriors.isEmpty()) {
            Unit dave = warriors.get(0);
            Tile targetTile = determineWarriorTargetTile(dave);
            if (unitAtTargetTile(dave, targetTile) && dave.attackCooldown < 0) {
                dave.attack(findEnemyBase());
            } else if (!unitAtTargetTile(dave, targetTile)) {
                if (dave.movementCooldown < 0) {
                    moveUnit(dave, targetTile);
                }
            }
        }
    }

    private Tile determineWarriorTargetTile(Unit warrior) {
        return targetAdjacentTile(warrior, findEnemyBase());
    }

    /**
     * Locates the enemy base
     * @return
     */
    private Tile findEnemyBase() {
        for (Tile tile : grid.getTiles()) {
            if (tile.entity.isPresent()) {
                if (tile.entity.get() instanceof Base && tile.entity.get().owner != me) {
                    return tile;
                }
            }
        }
        return null;
    }
}
