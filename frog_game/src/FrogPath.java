public class FrogPath {
    private Pond pond;
    private int fliesEaten = 0;
    private Hexagon[] visitedCells;
    private int visitedCount = 0;

    public FrogPath(String filename) {
        try {
            pond = new Pond(filename);
            visitedCells = new Hexagon[100];
        } catch (Exception e) {
            System.out.println("Failed to initialize pond: " + e.getMessage());
        }
    }

    public Hexagon findBest(Hexagon curCell) {
        ArrayUniquePriorityQueue<Hexagon> upq = new ArrayUniquePriorityQueue<>();

        for (int i = 0; i < 6; i++) {
            Hexagon neighbour = curCell.getNeighbour(i);
            if (neighbour != null && canVisit(neighbour)){
                double priority = calculatePriority(curCell, neighbour, false);
                upq.add(neighbour, priority);
            }
        }
        if (curCell.isLilyPadCell()) {
            for (int i = 0; i < 6; i++) {
                Hexagon neighbour = curCell.getNeighbour(i);
                if (neighbour != null) {
                    for (int j = 0; j < 6; j++) {
                        Hexagon secondNeighbour = neighbour.getNeighbour(i);
                        if (secondNeighbour != null && secondNeighbour != curCell && canVisit(secondNeighbour)) {
                            boolean isInStraightLine = (j == i || j == (i + 3) % 6);
                            double priority = calculatePriority(curCell, secondNeighbour, !isInStraightLine);
                            upq.add(secondNeighbour, priority);
                        }
                    }
                }
            }
        }
        if (!upq.isEmpty()) {
            return upq.removeMin();
        } else {
            return null;
        }
    }

    private boolean canVisit(Hexagon cell) {
        if (cell.isMudCell()) {
            return false;
        }
        if (cell.isAlligator()) {
            return false;
        }
        if (!cell.isReedsCell()) {
            for(int i = 0; i < 6; i++) {
                try {
                    Hexagon neighbour = cell.getNeighbour(i);
                    if (neighbour != null && neighbour.isAlligator()) {
                        return false;
                    }
                } catch (InvalidNeighbourIndexException e) {
              System.err.println("Invalid neighbor index accessed: " + e.getMessage());
              e.printStackTrace();  
                }   
            }
        }
        if (cell.isMarked()) {
            return false;
        }
        return true;
    }

    private double calculatePriority(Hexagon currCell, Hexagon targetCell, boolean isDiagonal) {
        double priority = 0.0;
    
        if (targetCell instanceof FoodHexagon) {
            FoodHexagon foodCell = (FoodHexagon) targetCell;
            int numFlies = foodCell.getNumFlies();
            switch (numFlies) {
                case 3:
                    priority = 0.0;
                    break;
                case 2:
                    priority = 1.0;
                    break;
                case 1:
                    priority = 2.0; 
                    break;
                default:
                    priority = 6.0; 
                    break;
            }
        } else if (targetCell.isLilyPadCell()) {
            priority = 4.0;
        } else if (targetCell.isWaterCell()) {
            priority = 6.0;
        } else if (targetCell.isReedsCell()) {
            priority = 5.0;
        } else if (targetCell.isAlligator()) {
            priority = Double.MAX_VALUE; 
        } else if (targetCell.isEnd()) {
            priority = 3.0;
        } 
        
        if (isDiagonal) {
            priority += 1.0;
        } else {
            priority += 0.5;
        }
    
        return priority;
    }

    public String findPath() {
        Hexagon startCell = pond.getStart();
        if (startCell == null) return "No solution";

        ArrayStack<Hexagon> pathStack = new ArrayStack<>();
        pathStack.push(startCell);
        addVisitedCell(startCell); 

        Hexagon currentCell = startCell;
        
        while (!pathStack.isEmpty()) {
            if (currentCell.isEnd()) {
                return buildPathDescription() + "ate " + fliesEaten + " flies";
            }

            Hexagon nextCell = findBest(currentCell);

            if (nextCell == null || visitedContains(nextCell)) {
                if (!pathStack.isEmpty()) {
                    currentCell = pathStack.pop();
                }
            } else {
                pathStack.push(nextCell);
                addVisitedCell(nextCell);
                currentCell = nextCell;

                if (currentCell instanceof FoodHexagon) {
                    fliesEaten += ((FoodHexagon)currentCell).getNumFlies();
                }
            }
        }

        return "No solution";
    }

    private boolean visitedContains(Hexagon cell) {
        for (int i = 0; i < visitedCount; i++) {
            if (visitedCells[i].equals(cell)) {
                return true;
            }
        }
        return false;
    }

    private void addVisitedCell(Hexagon cell) {
        if (visitedCount == visitedCells.length) {
            Hexagon[] newArray = new Hexagon[visitedCells.length * 2];
            System.arraycopy(visitedCells, 0, newArray, 0, visitedCells.length);
            visitedCells = newArray;
        }
        visitedCells[visitedCount++] = cell;
    }

    private String buildPathDescription() {
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < visitedCount; i++) {
            description.append(visitedCells[i].getID()).append(" ");
        }
        return description.toString();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("No map file specified in the arguments");
            return;
        }
        FrogPath fp = new FrogPath(args[0]);
        String result = fp.findPath();
        System.out.println(result);
    }
}
