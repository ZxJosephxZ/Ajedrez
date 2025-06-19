package main;

import piece.Pawn;
import piece.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{
    private static final int WIDTH = 1100;
    private static final int HEIGHT = 800;
    private int FPS = 60;
    Thread gameThread;
    Board board = new Board();

    //Color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //Piezas
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();


    public GamePanel()
    {
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.BLACK);
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

        //BLACK
        pieces.add(new Pawn(BLACK,0,6));
        pieces.add(new Pawn(BLACK,1,6));
        pieces.add(new Pawn(BLACK,2,6));
        pieces.add(new Pawn(BLACK,3,6));
        pieces.add(new Pawn(BLACK,4,6));
        pieces.add(new Pawn(BLACK,5,6));
        pieces.add(new Pawn(BLACK,6,6));
        pieces.add(new Pawn(BLACK,7,6));
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
    {

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
    }

}
