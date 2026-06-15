package main;

import boundary.FormSplash;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;


public class Main extends JFrame {
    public static void main(String[] args) {

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("biblioteca_db");

        emf.close();

        System.out.println("Avvio di Hibernate completato.");

        SwingUtilities.invokeLater(() -> new FormSplash());
    }


}

