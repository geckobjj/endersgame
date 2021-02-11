package utilities;

import com.ender.game.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class HelperUtils {
    /**
     * Moves a Unit one step closer to the target Tile
     * @param unit
     * @param targetTile
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
            unit.move(Direction.NORTH);
        } else if (south && unit.tile.getAdjacentTile(Direction.SOUTH).get().canReceiveUnit()) {
            unit.move(Direction.SOUTH);
        } else if (east && unit.tile.getAdjacentTile(Direction.EAST).get().canReceiveUnit()) {
            unit.move(Direction.EAST);
        } else if (west && unit.tile.getAdjacentTile(Direction.WEST).get().canReceiveUnit()) {
            unit.move(Direction.WEST);
        }
        else {
            // So I came up with this contingency tile to try to escape being trapped in
            // an east to west row...  It doesn't work well at all...
            /*
            Tile contingencyTile = unit.tile.getAdjacentTiles().stream()
                    .filter(Tile::canReceiveUnit)
                    .findFirst()
                    .get();
            moveUnit(unit, contingencyTile);*/
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
     * @param tileA
     * @param tileB
     * @return
     */
    public static double calculateTileDistance(Tile tileA, Tile tileB) {
        double a = Math.abs(tileA.i - tileB.i);
        double b = Math.abs(tileA.j - tileB.j);
        return Math.hypot(a, b);
    }

    /**
     * Checks to see if the unit has arrived at it's target
     * @param unit
     * @param targetTile
     * @return
     */
    public static boolean unitAtTargetTile(Unit unit, Tile targetTile) {
        return unit.tile.i == targetTile.i && unit.tile.j == targetTile.j;
    }

    public static ArrayList<Unit> getUnitsByType(Player me, Grid grid, UnitType unitType) {
        return (ArrayList<Unit>) grid.getUnits(me).stream().filter(unit -> unit.type== unitType).collect(Collectors.toList());
    }

    public static String generateSalt() {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < 6; i++) {
            buffer.append((char)(random.nextInt(122-97)+97));
        }
        return buffer.toString();
    }
}
