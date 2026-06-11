package entity;

import jakarta.persistence.*;

@Entity
public class Studente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matricola;

    private String nome;
    private String cognome;
    private String email;
    private String password;

    public Studente() {}

    public Studente(Long matricola, String nome,
                    String cognome, String email,
                    String password) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
}
