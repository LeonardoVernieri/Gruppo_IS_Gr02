package main;

import boundary.FormLogin;

import boundary.FormRegistrazione;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;


public class Main extends JFrame {
    public static void main(String[] args) {

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("bibliotecaSys");

        emf.close();

        System.out.println("Avvio di Hibernate completato.");

        JFrame frame = new JFrame();

        frame.setTitle("Login");
        frame.setContentPane(new FormLogin().getContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


}

