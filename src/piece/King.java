package piece;

import main.GamePanel;
import main.Main;

public class King extends Piece{
    public King(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE)
        {
            image = getImage("/piezas/1");
        }
        else
        {
            image = getImage("/piezas/14");
        }
    }
    public boolean canMove(int targetCol, int targetRow)
    {
        if (isWithinBoard(targetCol,targetRow))
        {
            if (Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 ||
                Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1)
            {
                return true;
            }
        }
        return false;
    }
}
