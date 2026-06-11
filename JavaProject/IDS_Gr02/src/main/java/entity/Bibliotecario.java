package entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Bibliotecario{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codiceInterno;

    private String nome;
    private String cognome;
    private String email;
    private String password;

    @OneToMany
    private List<SalaStudio> saleStudio;

    public Bibliotecario() {}

    public Bibliotecario(Long codiceInterno, String nome,
                         String cognome, String email,
                         String password) {
        this.codiceInterno = codiceInterno;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
}