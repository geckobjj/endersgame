package engines;

import com.ender.game.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static utilities.HelperUtils.*;

public class EconomyEngine implements Engine{

    private static EconomyEngine economyEngine = null;

    List<Unit> workers;
    Grid grid;
    Base base;


    private EconomyEngine() {
    }

    // Singleton implementation
    public static EconomyEngine getInstance() {
        if(economyEngine == null)
            economyEngine = new EconomyEngine();

        return economyEngine;
    }


    @Override
    public void execute(Player me, Grid grid) {
        this.grid = grid;
        this.base = grid.getBase(me);
        this.workers = getUnitsByType(me, grid, UnitType.WORKER);
        moveWorkers(me);
        runEconomy(me.dollars, grid.getBase(me), workers);
    }

    private void moveWorkers(Player me) {
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

    private void runEconomy(int dollars, Base base, List<Unit> workers) {
        if(workers.size() < 2) {
            if(dollars >= 10) {
                base.construct(UnitType.WORKER);
            }
        } else {
            if(dollars >= 50) {
                base.construct(UnitType.WARRIOR);
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

            // Target a tile with resources
            List<Tile> resourceTiles = findResources(grid);
            List<Tile> goldTiles = resourceTiles.stream()
                    .filter(tile -> tile.resourceType.get() == Tile.Resource.GOLD).collect(Collectors.toList());
            List<Tile> silverTiles = resourceTiles.stream()
                    .filter(tile -> tile.resourceType.get() == Tile.Resource.SILVER).collect(Collectors.toList());
            List<Tile> copperTiles = resourceTiles.stream()
                    .filter(tile -> tile.resourceType.get() == Tile.Resource.COPPER).collect(Collectors.toList());

            // Set priority
            if (!goldTiles.isEmpty()) {
                targetTile = goldTiles.get(0);
            }
            else if (!silverTiles.isEmpty()) {
                targetTile = silverTiles.get(0);
            }
            else {
                targetTile = copperTiles.get(0);
            }
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

    private boolean isMissingCargo(Unit unit) {
        return unit.cargo.isEmpty();
    }
}
