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

    Piece activeP;

    //Boolean
    boolean canMove;
    boolean validSquare;


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
            delta += (currentTime - lastTime);
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
        pieces.add(new Pawn(WHITE,0,1));
        pieces.add(new Pawn(WHITE,1,1));
        pieces.add(new Pawn(WHITE,2,1));
        pieces.add(new Pawn(WHITE,3,1));
        pieces.add(new Pawn(WHITE,4,1));
        pieces.add(new Pawn(WHITE,5,1));
        pieces.add(new Pawn(WHITE,6,1));
        pieces.add(new Pawn(WHITE,7,1));

        pieces.add(new Rook(WHITE,0,0));
        pieces.add(new Rook(WHITE,7,0));
        pieces.add(new Knight(WHITE,1,0));
        pieces.add(new Knight(WHITE,6,0));
        pieces.add(new Bishop(WHITE,2,0));
        pieces.add(new Bishop(WHITE,5,0));
        pieces.add(new King(WHITE,3,0));
        pieces.add(new Queen(WHITE,4,0));

        //BLACK
        pieces.add(new Pawn(BLACK,0,6));
        pieces.add(new Pawn(BLACK,1,6));
        pieces.add(new Pawn(BLACK,2,6));
        pieces.add(new Pawn(BLACK,3,6));
        pieces.add(new Pawn(BLACK,4,6));
        pieces.add(new Pawn(BLACK,5,6));
        pieces.add(new Pawn(BLACK,6,6));
        pieces.add(new Pawn(BLACK,7,6));

        pieces.add(new Rook(BLACK,0,7));
        pieces.add(new Rook(BLACK,7,7));
        pieces.add(new Knight(BLACK,1,7));
        pieces.add(new Knight(BLACK,6,7));
        pieces.add(new Bishop(BLACK,2,7));
        pieces.add(new Bishop(BLACK,5,7));
        pieces.add(new King(BLACK,3,7));
        pieces.add(new Queen(BLACK,4,7));
    }

    private void copyPieces(ArrayList<Piece>source, ArrayList<Piece>target)
    {
        target.clear();
        for (int i = 0; i< source.size();i++)
        {
            target.add(source.get(i));
        }
    }

//Metodo para actualizar figuras
    private void update()
    {//Comprobamos que se mantenga un click
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
                    activeP.updatePosition();
                }
                else {
                    activeP.resetPosition();
                    activeP = null;
                }
            }
        }
    }

    public void simulate()
    {
        canMove = false;
        validSquare = false;
        //Cambiamos las posiciones a la del mouse (restamos las dimensiones para que el cursosr este en el centro de la pieza)
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);
        if (activeP.canMove(activeP.col, activeP.row))
        {
            canMove = true;
            validSquare = true;
        }
    }

//Metodo para dibujar en el jpanel que implementa un constructor de jcomponent
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //tablero
        board.draw(g2);
        //piezas
        for (Piece p: simPieces)
        {
            p.draw(g2);
        }

        if (activeP != null)
        {//Metodos para señalar o colorear a la posicion que se movera la pieza
            g2.setColor(Color.WHITE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            activeP.draw(g2);
        }
    }

}
