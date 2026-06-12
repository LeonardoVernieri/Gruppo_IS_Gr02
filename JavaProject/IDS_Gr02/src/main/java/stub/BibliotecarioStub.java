package stub;

public class BibliotecarioStub {
    private String nome;
    private String cognome;

    public BibliotecarioStub(String nome, String cognome){
        this.nome = nome;
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }
}
