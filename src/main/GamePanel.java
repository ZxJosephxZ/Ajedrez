package main;

import piece.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{
    private static final int WIDTH = 1100;
    private static final int HEIGHT = 800;
    private int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //Color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //Piezas
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();

    Piece activeP, checkingP;
    public static Piece castlingP;
    //Boolean
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver;
    boolean statement;

    public GamePanel()
    {
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        setPiece();
        copyPieces(pieces,simPieces);
    }
//Lanzamos el hilo
    public void launchGame()
    {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
    //GAME LOOP
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null)
        {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;
            if (delta >= 1)
            {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void setPiece()
    {
        //WHITE
        pieces.add(new Pawn(BLACK,0,1));
        pieces.add(new Pawn(BLACK,1,1));
        pieces.add(new Pawn(BLACK,2,1));
        pieces.add(new Pawn(BLACK,3,1));
        pieces.add(new Pawn(BLACK,4,1));
        pieces.add(new Pawn(BLACK,5,1));
        pieces.add(new Pawn(BLACK,6,1));
        pieces.add(new Pawn(BLACK,7,1));

        pieces.add(new Rook(BLACK,0,0));
        pieces.add(new Rook(BLACK,7,0));
        pieces.add(new Knight(BLACK,1,0));
        pieces.add(new Knight(BLACK,6,0));
        pieces.add(new Bishop(BLACK,2,0));
        pieces.add(new Bishop(BLACK,5,0));
        pieces.add(new King(BLACK,4,0));
        pieces.add(new Queen(BLACK,3,0));

        //BLACK
        pieces.add(new Pawn(WHITE,0,6));
        pieces.add(new Pawn(WHITE,1,6));
        pieces.add(new Pawn(WHITE,2,6));
        pieces.add(new Pawn(WHITE,3,6));
        pieces.add(new Pawn(WHITE,4,6));
        pieces.add(new Pawn(WHITE,5,6));
        pieces.add(new Pawn(WHITE,6,6));
        pieces.add(new Pawn(WHITE,7,6));

        pieces.add(new Rook(WHITE,0,7));
        pieces.add(new Rook(WHITE,7,7));
        pieces.add(new Knight(WHITE,1,7));
        pieces.add(new Knight(WHITE,6,7));
        pieces.add(new Bishop(WHITE,2,7));
        pieces.add(new Bishop(WHITE,5,7));
        pieces.add(new King(WHITE,4,7));
        pieces.add(new Queen(WHITE,3,7));
    }

    private boolean canPromote()
    {
        if (activeP.type == Type.PAWN)
        {
            if (currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7)
            {
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor,9,2));
                promoPieces.add(new Knight(currentColor,9,3));
                promoPieces.add(new Bishop(currentColor,9,4));
                promoPieces.add(new Queen(currentColor,9,5));
                return true;
            }
        }
        return false;
    }

    private void checkCastling()
    {
        if (castlingP != null)
        {
            if (castlingP.col == 0)
            {
                castlingP.col += 3;
            }
            else if (castlingP.col == 7)
            {
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    private boolean isStatement()
    {
        int count = 0;
        for (Piece piece : simPieces)
        {
            if (piece.color != currentColor)
            {
                count++;
            }
        }
        if (count == 1)
        {
            if (kingCanMove(getKing(true)) == false)
            {
                return true;
            }
        }
        return false;
    }

    private void changePlayer()
    {
        if (currentColor == WHITE)
        {
            currentColor = BLACK;
            for (Piece piece : pieces)
            {
                if (piece.color == BLACK)
                {
                    piece.twoStepped = false;
                }
            }
        }
        else
        {
            currentColor = WHITE;
            for (Piece piece : pieces)
            {
                if (piece.color == WHITE)
                {
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target)
    {
        target.clear();
        for (int i = 0; i< source.size();i++)
        {
            target.add(source.get(i));
        }
    }

//Metodo para actualizar figuras
    private void update()
    {
        if (promotion)
        {
            promoting();
        }
        else if (gameOver == false && statement == false)
        {
            //Comprobamos que se mantenga un click
            if (mouse.pressed)
            {//Comprobamos activeP que es la variable que nos indica si esta presionando una pieza
                if (activeP == null)
                {//Seleccionamos la pieza
                    for (Piece piece : simPieces)
                    {
                        if (piece.color == currentColor && piece.col == mouse.x/Board.SQUARE_SIZE &&
                                piece.row == mouse.y/Board.SQUARE_SIZE)
                        {
                            activeP = piece;
                        }
                    }
                }
                else{
                    simulate();
                }
            }
            if (mouse.pressed == false)
            {
                if (activeP != null)
                {
                    if (validSquare)
                    {
                        //actualizamos la lista por si es capturada alguna pieza
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        if (castlingP != null)
                        {
                            castlingP.updatePosition();
                        }
                        if (isKingInCheck() && isCheckmate())
                        {
                            gameOver = true;
                        }
                        else if (isStatement() && isKingInCheck() == false)
                        {
                            statement = true;
                        }
                        else {
                            if (canPromote())
                            {
                                promotion = true;
                            }
                            else
                            {
                                changePlayer();
                            }
                        }
                    }
                    else {
                        //en caso de hacer algo invalido reseteamos
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                }
            }
        }
    }

    private boolean isKingInCheck()
    {
        Piece king = getKing(true);
        if (activeP.canMove(king.col,king.row))
        {
            checkingP = activeP;
            return true;
        }
        else
        {
            checkingP = null;
        }
        return false;
    }

    private boolean opponentCanCaptureKing()
    {
        Piece king = getKing(false);
        for (Piece piece : simPieces)
        {
            if (piece.color != king.color && piece.canMove(king.col,king.row))
            {
                return true;
            }
        }
        return false;
    }

    private Piece getKing(boolean opponent)
    {
        Piece king = null;
        for (Piece piece : simPieces)
        {
            if (opponent)
            {
                if (piece.type == Type.KING && piece.color != currentColor)
                {
                    king = piece;
                }
            }
            else
            {
                if (piece.type == Type.KING && piece.color == currentColor)
                {
                    king = piece;
                }
            }
        }
        return king;
    }

    private void promoting()
    {
        if (mouse.pressed)
        {
            for (Piece piece : promoPieces)
            {
                if (piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE)
                {
                    switch (piece.type){
                        case ROOK: simPieces.add(new Rook(currentColor, activeP.col, activeP.row));break;
                        case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row));break;
                        case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));break;
                        case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row));break;
                        default:break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces,pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    private void simulate()
    {
        canMove = false;
        validSquare = false;
        //esto es basicamente para restaurar la pieza removida durante la ejecucion
        copyPieces(pieces, simPieces);
        if (castlingP != null)
        {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }
        //Cambiamos las posiciones a la del mouse (restamos las dimensiones para que el cursosr este en el centro de la pieza)
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);
        if (activeP.canMove(activeP.col, activeP.row))
        {
            canMove = true;
            if (activeP.hittingP != null)
            {
                simPieces.remove(activeP.hittingP.getIndex());
            }
            checkCastling();
            if (isIlegal(activeP) == false && opponentCanCaptureKing() == false)
            {
                validSquare = true;
            }
        }
    }

    private boolean isIlegal(Piece king)
    {
        if (king.type == Type.KING)
        {
            for (Piece piece : simPieces)
            {
                if ( piece != king && piece.color != king.color && piece.canMove(king.col,king.row))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCheckmate()
    {
        Piece king = getKing(true);
        if (kingCanMove(king))
        {
            return false;
        }
        else
        {
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);
            if (colDiff == 0)
            {
                if (checkingP.row < king.row)
                {
                    for (int row = checkingP.row; row <king.row;row++)
                    {
                        for (Piece piece : simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col,row))
                            {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.row > king.row)
                {
                    for (int row = checkingP.row; row >king.row;row--)
                    {
                        for (Piece piece : simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col,row))
                            {
                                return false;
                            }
                        }
                    }
                }
            }
            else if(rowDiff == 0)
            {
                if (checkingP.col < king.col)
                {
                    for (int col = checkingP.col; col <king.col;col++)
                    {
                        for (Piece piece : simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col,col))
                            {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col)
                {
                    for (int col = checkingP.col; col > king.col;col--)
                    {
                        for (Piece piece : simPieces)
                        {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col,col))
                            {
                                return false;
                            }
                        }
                    }
                }
            }
            else if(colDiff == rowDiff)
            {
                if (checkingP.row < king.row)
                {
                    if (checkingP.col < king.col)
                    {
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++,row++)
                        {
                            for (Piece piece : simPieces)
                            {
                                if (piece != king && piece.color != currentColor && piece.canMove(col,row))
                                {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col)
                    {
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--,row++)
                        {
                            for (Piece piece : simPieces)
                            {
                                if (piece != king && piece.color != currentColor && piece.canMove(col,row))
                                {
                                    return false;
                                }
                            }
                        }
                    }
                }
                if (checkingP.row > king.row)
                {
                    if (checkingP.col < king.col)
                    {
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++,row--)
                        {
                            for (Piece piece : simPieces)
                            {
                                if (piece != king && piece.color != currentColor && piece.canMove(col,row))
                                {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col)
                    {
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--,row--)
                        {
                            for (Piece piece : simPieces)
                            {
                                if (piece != king && piece.color != currentColor && piece.canMove(col,row))
                                {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean kingCanMove(Piece king)
    {
        if (isValidMove(king, -1,-1)) return true;
        if (isValidMove(king, 0,-1)) return true;
        if (isValidMove(king, 1,-1)) return true;
        if (isValidMove(king, -1,0)) return true;
        if (isValidMove(king, 1,0)) return true;
        if (isValidMove(king, -1,1)) return true;
        if (isValidMove(king, 0,1)) return true;
        if (isValidMove(king, 1,1)) return true;
        return false;
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus)
    {
        boolean isValidMove = false;
        king.col += colPlus;
        king.row += rowPlus;
        if (king.canMove(king.col,king.row))
        {
            if (king.hittingP != null)
            {
                simPieces.remove(king.hittingP.getIndex());
            }
            if (isIlegal(king) == false)
            {
                isValidMove = true;
            }
        }
        king.resetPosition();
        copyPieces(pieces,simPieces);
        return isValidMove;
    }

//Metodo para dibujar en el jpanel que implementa un constructor de jcomponent
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //tablero
        board.draw(g2);
        //piezas
        for (Piece p : simPieces)
        {
            p.draw(g2);
        }

        if (activeP != null)
        {//Metodos para señalar o colorear a la posicion que se movera la pieza
            if (canMove) {
                if (isIlegal(activeP) || opponentCanCaptureKing())
                {
                    g2.setColor(Color.gray);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                }
                else {
                    g2.setColor(Color.WHITE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }
            activeP.draw(g2);
        }
        //status message
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua",Font.PLAIN, 40));
        g2.setColor(Color.WHITE);

        if (promotion)
        {
            g2.drawString("Promote to:",840,150);
            for (Piece piece : promoPieces)
            {
                g2.drawImage(piece.image, piece.getX(piece.col),piece.getY(piece.row),Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE, null);
            }
        }
        else
        {
            if (currentColor == WHITE)
            {
                g2.drawString("White's turn",840,550);
                if (checkingP != null && checkingP.color == BLACK)
                {
                    g2.setColor(Color.RED);
                    g2.drawString("The king",840,650);
                    g2.drawString("is in check!",840,700);
                }
            }
            else
            {
                g2.drawString("Black's turn", 840, 250);
                if (checkingP != null && checkingP.color == WHITE)
                {
                    g2.setColor(Color.RED);
                    g2.drawString("The king",840,100);
                    g2.drawString("is in check!",840,150);
                }
            }
        }
        if (gameOver)
        {
            String a = "";
            if (currentColor == WHITE)
            {
                a = "White wins";
            }
            else
            {
                a = "Black wins";
            }
            g2.setFont(new Font("Arial",Font.PLAIN, 90));
            g2.setColor(Color.green);
            g2.drawString(a,200,420);
        }
        if (statement)
        {
            g2.setFont(new Font("Arial",Font.PLAIN, 90));
            g2.setColor(Color.lightGray);
            g2.drawString("Stalemate",200,420);
        }
    }

}
