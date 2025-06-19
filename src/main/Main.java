package main;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        JFrame ventana = new JFrame();
        GamePanel gamePanel = new GamePanel();
        ventana.setResizable(false);
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Añadir fondo
        ventana.add(gamePanel);
        //El metodo pack permite a la ventana ajustar su tamaño al fondo añadido
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);

        gamePanel.launchGame();
    }

}