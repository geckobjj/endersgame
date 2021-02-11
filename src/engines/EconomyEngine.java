package engines;

import com.ender.game.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static utilities.HelperUtils.*;

//TODO Calculate node value for worker prioritization based on distance and value
public class EconomyEngine implements Engine{

    private static EconomyEngine economyEngine = null;

    List<Unit> workers;
    List<Unit> warriors;
    List<Unit> archers;
    Grid grid;
    Base base;
    Player me;
    int dollars;

    private EconomyEngine() {}

    public static EconomyEngine getInstance() {
        if(economyEngine == null)
            economyEngine = new EconomyEngine();

        return economyEngine;
    }


    @Override
    public void execute(Player me, Grid grid) {
        this.grid = grid;
        this.me = me;
        this.base = grid.getBase(me);
        this.dollars = me.dollars;
        this.workers = getUnitsByType(me, grid, UnitType.WORKER);
        this.warriors = getUnitsByType(me, grid, UnitType.WARRIOR);
        this.archers = getUnitsByType(me, grid, UnitType.ARCHER);

        moveWorkers();
        // 3,1,0 works well so far
        runEconomy(3, 1, 1);
    }

    /**
     * Moves all workers on the grid.
     */
    private void moveWorkers() {
        for (Unit worker : workers) {
            Tile targetLocation = determineWorkerTargetTile(worker, grid, grid.getBase(me));
            if (unitAtTargetTile(worker, targetLocation)) {
                if (isMissingCargo(worker)) {
                    worker.extractResource();
                }
                else {
                    worker.depositResource();
                }
            }
            else {
                moveUnit(worker, targetLocation);
            }
        }
    }

    /**
     * Handles purchasing decisions and actions.
     */
    private void runEconomy(int workerMax, int warriorMax, int archerMax) {
        // Need to see if 2 or 3 workers is faster.
        // Always replenish workers first.
        if(workers.size() < workerMax) {
            if(dollars >= 10) {
                base.construct(UnitType.WORKER);
            }
        } else {
            if(dollars >= 50 && warriors.size() < warriorMax) {
                base.construct(UnitType.WARRIOR);
            }
            if(dollars >= 75 && archers.size() < archerMax) {
                base.construct(UnitType.ARCHER);
            }
        }
    }

    /**
     * Determine the tile that a worker is going to target for travel
     * @param worker The worker unit
     * @param grid The game grid
     * @param base The player base where worker deposits resources
     * @return Tile that the worker will travel to
     */
    private Tile determineWorkerTargetTile(Unit worker, Grid grid, Base base) {
        Tile targetTile;
        // Check to see if worker is carrying resources
        if(isMissingCargo(worker)) {

            // Target a tile with resources.
            // If all resources are mined, set target to block the base.
            List<Tile> resourceTiles = findResources(grid);
            Optional<Tile> resourceTile = targetHighValueResource(findResources(grid));
            targetTile = resourceTile.orElseGet(() -> targetAdjacentTile(worker, base.tile));
        }
        else {
            //Target a tile adjacent to the base to deposit resources
            targetTile = targetAdjacentTile(worker, base.tile);
        }
        return targetTile;
    }

    /**
     * Return a List of all tiles with resources
     * @param grid The game grid
     * @return A Tile List of every tile containing resources
     */
    private List<Tile> findResources(Grid grid) {
        List<Tile> resources = new ArrayList<>();
        for (Tile tile : grid.getTiles()) {
            if (tile.resourceType.isPresent())
                resources.add(tile);
        }
        return resources;
    }

    /**
     * Prioritizes resources in order of value (not distance)
     * @param resourceTiles List of all tiles containing resources
     * @return Optional of a Tile with the highest resource value
     */
    private Optional<Tile> targetHighValueResource(List<Tile> resourceTiles) {
        List<Tile> goldTiles = resourceTiles.stream()
                .filter(tile -> tile.resourceType.get() == Tile.Resource.GOLD).collect(Collectors.toList());
        List<Tile> silverTiles = resourceTiles.stream()
                .filter(tile -> tile.resourceType.get() == Tile.Resource.SILVER).collect(Collectors.toList());
        List<Tile> copperTiles = resourceTiles.stream()
                .filter(tile -> tile.resourceType.get() == Tile.Resource.COPPER).collect(Collectors.toList());

        // Return based on priority of value
        if (!goldTiles.isEmpty()) {
            return Optional.of(goldTiles.get(0));
        }
        else if (!silverTiles.isEmpty()) {
            return Optional.of(silverTiles.get(0));
        }
        else if (!copperTiles.isEmpty()) {
            return Optional.of(copperTiles.get(0));
        }
        return Optional.empty();
    }

    /**
     * Check to see if a unit has no cargo.
     * @param unit Unit to check
     * @return boolean
     */
    private boolean isMissingCargo(Unit unit) {
        return unit.cargo.isEmpty();
    }
}
