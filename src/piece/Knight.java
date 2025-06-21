package piece;

import main.GamePanel;

public class Knight extends Piece{
    public Knight(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE)
        {
            image = getImage("/piezas/3");
        }
        else
        {
            image = getImage("/piezas/12");
        }
    }
}
