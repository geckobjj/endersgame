package utilities;

import com.ender.game.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

//TODO Moving workers need to recognize one another and avoid graciously.
public class HelperUtils {

    /**
     * Moves a Unit one step closer to the target Tile
     * @param unit The unit to move
     * @param targetTile The unit's destination
     */
    public static void moveUnit(Unit unit, Tile targetTile) {
        // Determine direction
        boolean north, south, east, west;
        north = unit.tile.j > targetTile.j && unit.tile.getAdjacentTile(Direction.NORTH).isPresent();
        south = unit.tile.j < targetTile.j && unit.tile.getAdjacentTile(Direction.SOUTH).isPresent();
        east = unit.tile.i < targetTile.i && unit.tile.getAdjacentTile(Direction.EAST).isPresent();
        west = unit.tile.i > targetTile.i && unit.tile.getAdjacentTile(Direction.WEST).isPresent();

        // isPresent check not needed due to already checking to see if tile exists that direction
        if(north && unit.tile.getAdjacentTile(Direction.NORTH).get().canReceiveUnit()) {
            // Attempt to clear east/west blocking
            if(east) {
                Tile easternTile = unit.tile.getAdjacentTile(Direction.EAST).get();
                Tile northEasternTile = easternTile.getAdjacentTile(Direction.NORTH).get();
                if(!northEasternTile.canReceiveUnit() && easternTile.canReceiveUnit()){
                    unit.move(Direction.EAST);
                }
                else {
                    unit.move(Direction.NORTH);
                }
            }
            else if(west) {
                Tile westernTile = unit.tile.getAdjacentTile(Direction.WEST).get();
                Tile northWesternTile = westernTile.getAdjacentTile(Direction.NORTH).get();
                if(!northWesternTile.canReceiveUnit() && westernTile.canReceiveUnit()){
                    unit.move(Direction.WEST);
                }
                else {
                    unit.move(Direction.NORTH);
                }
            }
            else {
                unit.move(Direction.NORTH);
            }
        } else if (south && unit.tile.getAdjacentTile(Direction.SOUTH).get().canReceiveUnit()) {
            // Attempt to clear east/west blocking
            if(east) {
                Tile easternTile = unit.tile.getAdjacentTile(Direction.EAST).get();
                Tile southEasternTile = easternTile.getAdjacentTile(Direction.SOUTH).get();
                if(!southEasternTile.canReceiveUnit() && easternTile.canReceiveUnit()){
                    unit.move(Direction.EAST);
                }
                else {
                    unit.move(Direction.SOUTH);
                }
            }
            else if (west) {
                Tile westernTile = unit.tile.getAdjacentTile(Direction.WEST).get();
                Tile southWesternTile = westernTile.getAdjacentTile(Direction.SOUTH).get();
                if(!southWesternTile.canReceiveUnit() && westernTile.canReceiveUnit()){
                    unit.move(Direction.WEST);
                }
                else {
                    unit.move(Direction.SOUTH);
                }
            }
            else {
                unit.move(Direction.SOUTH);
            }
        } else if (east && unit.tile.getAdjacentTile(Direction.EAST).get().canReceiveUnit()) {
            unit.move(Direction.EAST);
        } else if (west && unit.tile.getAdjacentTile(Direction.WEST).get().canReceiveUnit()) {
            unit.move(Direction.WEST);
        }
        else {
            // Contingency wildcard tile to break out of sticky situations!
            Random random = new Random();
            List<Tile> availableTiles = unit.tile.getAdjacentTiles()
                    .stream()
                    .filter(Tile::canReceiveUnit)
                    .collect(Collectors.toList());
            moveUnit(unit, availableTiles.get(random.nextInt(availableTiles.size())));
        }
    }

    /**
     * Calculates the distance between two tiles
     * @param tileA A tile
     * @param tileB Another tile
     * @return The double hypotenuse of the two tiles
     */
    public static double calculateTileDistance(Tile tileA, Tile tileB) {
        double a = Math.abs(tileA.i - tileB.i);
        double b = Math.abs(tileA.j - tileB.j);
        return Math.hypot(a, b);
    }

    /**
     * Checks to see if the unit has arrived at it's target
     * @param unit The unit to check
     * @param targetTile The unit's destination
     * @return boolean has unit arrived?
     */
    public static boolean unitAtTargetTile(Unit unit, Tile targetTile) {
        return unit.tile.i == targetTile.i && unit.tile.j == targetTile.j;
    }

    public static ArrayList<Unit> getUnitsByType(Player me, Grid grid, UnitType unitType) {
        return (ArrayList<Unit>) grid.getUnits(me).stream().filter(unit -> unit.type== unitType).collect(Collectors.toList());
    }

    /**
     * Calculate which adjacent tile is closest to a unit
     * @param unit unit that is looking for available adjacent tiles to the target
     * @param centerTile the tile in the center.  We want to target its available (max 4) adjacent tiles.
     * @return tile that is the closest to the unit.
     */
    public static Tile targetAdjacentTile(Unit unit, Tile centerTile) {
        List<Tile> adjacentTiles = new ArrayList<>();
        for(Tile tile : centerTile.getAdjacentTiles()) {
            if (tile.canReceiveUnit() || tile.entity.isPresent()) {
                if (tile.entity.isPresent()) {
                    if (tile.entity.get().equals(unit)) {
                        adjacentTiles.add(tile);
                    }
                }
                else {
                    adjacentTiles.add(tile);
                }
            }
        }
        Tile closestTile = adjacentTiles.get(0);
        double distance = calculateTileDistance(unit.tile, closestTile);
        for (Tile tile : adjacentTiles) {
            double tileDistance = calculateTileDistance(unit.tile, tile);
            if (tileDistance < distance) {
                closestTile = tile;
                distance = tileDistance;
            }
        }
        return closestTile;
    }
}
