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
    Player me;


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
        if(workers.stream().count() < 2) {
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
     * @param unit
     * @param grid
     * @param base
     * @return
     */
    private Tile determineWorkerTargetTile(Unit unit, Grid grid, Base base) {
        Tile targetTile = null;
        if(isMissingCargo(unit)) {

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
            //Target a tile adjacent to the base
            Tile baseTile = base.tile;
            targetTile = baseTile.getAdjacentTiles().get(0);
        }
        return targetTile;
    }

    /**
     * Return a List of all tiles with resources
     * @param grid
     * @return
     */
    private List<Tile> findResources(Grid grid) {
        List<Tile> resources = new ArrayList<Tile>();
        for (Tile tile : grid.getTiles()) {
            if (tile.resourceType.isPresent())
                resources.add(tile);
        }
        return resources;
    }

    private boolean isMissingCargo(Unit unit) {
        return unit.cargo.isEmpty();
    }

    private void updateWorkerList() {

    }
}
