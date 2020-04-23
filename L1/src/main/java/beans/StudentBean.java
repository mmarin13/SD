package beans;

public class StudentBean implements java.io.Serializable {
    private int nrMatricol = 0;
    private String nume = null;
    private String prenume = null;
    private int varsta = 0;

    public StudentBean() {
    }

    public int getNrMatricol() {
        return nrMatricol;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public int getVarsta() {
        return varsta;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public void setVarsta(int varsta) {
        this.varsta = varsta;
    }

    public void setNrMatricol(int nrMatricol) {
        this.nrMatricol = nrMatricol;
    }
}
