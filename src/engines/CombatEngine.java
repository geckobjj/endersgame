package engines;

import com.ender.game.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static utilities.HelperUtils.*;

public class CombatEngine implements Engine{

    private static CombatEngine combatEngine = null;

    List<Unit> warriors;
    List<Unit> archers;
    Player me;
    Grid grid;

    private CombatEngine () {}

    public static CombatEngine getInstance() {
        if(combatEngine == null)
            combatEngine = new CombatEngine();

        return combatEngine;
    }

    @Override
    public void execute(Player me, Grid grid) {
        warriors = getUnitsByType(me, grid, UnitType.WARRIOR);
        archers = getUnitsByType(me, grid, UnitType.ARCHER);
        this.me = me;
        this.grid = grid;

        moveWarriors();
        moveArchers();
    }

    /**
     * Move all warriors on the grid.
     */
    private void moveWarriors() {
        if (!warriors.isEmpty()) {
            for(Unit warrior : warriors) {
                Tile targetTile = determineWarriorTargetTile(warrior);
                if (unitAtTargetTile(warrior, targetTile) && warrior.attackCooldown <= 0) {
                    warrior.attack(findEnemyBase());
                } else if (!unitAtTargetTile(warrior, targetTile)) {
                    if (warrior.movementCooldown <= 0) {
                        moveUnit(warrior, targetTile);
                    }
                }
            }
        }
    }

    /**
     * Move all archers on the grid.
     */
    private void moveArchers() {
        if (!archers.isEmpty()) {
            for(Unit archer: archers) {
                Tile targetTile = determineArcherTargetTile(archer);
                // Scan for enemies and shoot if they exist
                // Otherwise move toward the goal
                if (scanForArcherTargets(archer).isPresent() && archer.attackCooldown <= 0) {
                    archer.attack(scanForArcherTargets(archer).get());
                } else if (!unitAtTargetTile(archer, targetTile)) {
                    if (archer.movementCooldown <= 0) {
                        moveUnit(archer, targetTile);
                    }
                }
            }
        }
    }

    /**
     * Sets the target tile for a warrior.
     * @param warrior Warrior to set target for.
     * @return Tile
     */
    private Tile determineWarriorTargetTile(Unit warrior) {
        return targetAdjacentTile(warrior, findEnemyBase());
    }

    /**
     * Sets the target tile for an archer.
     * @param archer Archer unit
     * @return Tile
     */
    private Tile determineArcherTargetTile(Unit archer) {
        return targetAdjacentTile(archer, findEnemyBase());
    }
    /**
     * Locates the enemy base
     * @return Tile of the enemy base
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

    /**
     * Scans all tiles 1-2 steps away from the archer for enemies to fire upon.
     * @param archer the archer
     * @return Optional Tile of a potential enemy location
     */
    private Optional<Tile> scanForArcherTargets(Unit archer) {
        List<Tile> closeTiles = new ArrayList<>(archer.tile.getAdjacentTiles());
        List<Tile> allTiles = new ArrayList<>(closeTiles);
        for(Tile tile : closeTiles) {
            allTiles.addAll(tile.getAdjacentTiles());
        }
        List<Tile> enemyTiles = allTiles.stream()
                .distinct()
                .filter(tile -> tile.entity.isPresent())
                .filter(tile -> tile.entity.get().owner != me)
                .collect(Collectors.toList());

        if(enemyTiles.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(enemyTiles.get(0));
    }
}
