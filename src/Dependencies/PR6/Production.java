package Dependencies.PR6;

import Dependencies.PR1.Symbols.V;
import Dependencies.PR1.Symbols.VN;
import Dependencies.PR1.Symbols.VT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Production {

    //Atributos.
    public VN antecedente;
    public List<V> consecuente;

    //Constructores.
    public Production(VN antecedente, List<V> consecuente) {
        this.antecedente = antecedente;
        this.consecuente = consecuente;
    }

    public Production() {
        this.antecedente = new VN("");
        this.consecuente = new ArrayList<V>();
    }

    //Getter & Setter.
    public VN getAntecedente() {
        return antecedente;
    }

    public void setAntecedente(VN antecedente) {
        this.antecedente = antecedente;
    }

    public List<V> getConsecuente() {
        return consecuente;
    }

    public void setConsecuente(List<V> consecuente) {
        this.consecuente = consecuente;
    }

    @Override
    public String toString() {
        String aux = "";
        for (V v : consecuente) {
            aux += v + " ";
        }
        return antecedente + "::= " + aux;
    }

    @Override
    public boolean equals(Object o) {
        Production p = null;
        try {
            p = (Production) o;
        }
        catch (ClassCastException c) {
            System.err.println("Error de casteo.");
        }
        boolean consecuentesIguales = true;
        if (p.getConsecuente().size() != this.getConsecuente().size()) {
            consecuentesIguales = false;
        }
        else {
            for (int i = 0; i < this.getConsecuente().size(); i++) {
                if (!this.getConsecuente().get(i).equals(p.getConsecuente().get(i))) {
                    consecuentesIguales = false;
                }
            }
        }
        return ((this.getAntecedente().equals(p.getAntecedente())) && consecuentesIguales);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.antecedente);
        hash = 23 * hash + Objects.hashCode(this.consecuente);
        return hash;
    }

    @Override
    public Production clone() {
        return new Production(getAntecedente().clone(), clonarConsecuentes(getConsecuente()));
    }

    public List<V> clonarConsecuentes(List<V> consecuente) {
        List<V> toReturn = new ArrayList<V>();
        for (V cons : consecuente) {
            if (cons instanceof VN) {
                VN transf = (VN) cons;
                VN copia = transf.clone();
                toReturn.add(copia);
            }
            else {
                VT transf = (VT) cons;
                VT copia = transf.clone();
                toReturn.add(copia);
            }
        }
        return toReturn;
    }

    public boolean consecuentesIguales(Collection<V> col) {
        boolean iguales = false;
        List<V> interno = (List<V>) consecuente;
        List<V> externo = (List<V>) col;
        for (int i = 0; i < interno.size(); i++) {
            try {
                if (!interno.get(i).equals(externo.get(i))) {
                    return false;
                }
            }
            catch (Exception ee) {
                return false;
            }
        }
        return true;
    }
}
