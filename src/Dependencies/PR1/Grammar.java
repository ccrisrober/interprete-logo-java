package Dependencies.PR1;

import Dependencies.PR1.Symbols.V;
import Dependencies.PR1.Symbols.VN;
import Dependencies.PR1.Symbols.VT;
import Dependencies.PR6.Production;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Grammar {

    //Atributos.
    private Collection<VN> listaVN;
    private Collection<VT> listaVT;
    private Collection<Production> producciones;
    private VN simbInicial;
    private String path;

    //Constructores.
    public Grammar(Collection<VN> listaVN, Collection<VT> listaVT, Collection<Production> producciones, VN simbInicial) {
        this.listaVN = listaVN;
        this.listaVT = listaVT;
        this.producciones = producciones;
        this.simbInicial = simbInicial;
    }

    
    public Grammar(String path) throws IOException {
        this.path = path;
        //Inicialización de variables.

        listaVN = new ArrayList<VN>();
        listaVT = new ArrayList<VT>();
        producciones = new ArrayList<Production>();
        simbInicial = new VN();

        //Lectura de fichero.

        FileReader lF = new FileReader(path);
        lF.analizarFichero(listaVN, listaVT, producciones, simbInicial);

    }

    //Getter & Setter.
    public Collection<VN> getListaVN() {
        return listaVN;
    }

    public void setListaVN(Collection<VN> listaVN) {
        this.listaVN = listaVN;
    }

    public Collection<VT> getListaVT() {
        return listaVT;
    }

    public void setListaVT(Collection<VT> listaVT) {
        this.listaVT = listaVT;
    }

    public Collection<Production> getProducciones() {
        return producciones;
    }

    public void setProducciones(Collection<Production> producciones) {
        this.producciones = producciones;
    }

    public VN getSimbInicial() {
        return simbInicial;
    }

    public void setSimbInicial(VN simbInicial) {
        this.simbInicial = simbInicial;
    }

    //Métodos.
    @Override
    public String toString() {
        return ("Lista de simbolos no terminales:\n" + listaVN.toString()
                + "\nLista de simbolos terminales:\n" + listaVT.toString()
                + "\nProducciones:\n" + mostrarProducciones()
                + "Simbolo inicial: " + simbInicial.toString());
    }
    
    private String devolverConsecuente (Collection<V> cons) {
        String aux = "";
        for (V vv: cons) {
            aux += " " + vv + "";
        }
        aux = aux.substring(1);
        return aux;
    }
    
    private String mostrarProducciones() {
        String toReturn = "";
        for (VN v : listaVN) {
            Collection<Collection<V>> consecuentes = this.devolverConsecuentes(v);
            toReturn += v + " -> ";
            int fin = consecuentes.size() - 1;
            for(Collection<V> cons: consecuentes) {
                toReturn += devolverConsecuente(cons);
                if (fin != 0) {
                    toReturn += "|";
                }
                --fin;
            }
            toReturn += "\n";//p.toString() + "\n";
        }
        return toReturn;
    }

    public Collection<Collection<V>> devolverConsecuentes(VN antecedente) {
        Collection<Collection<V>> cons = new ArrayList<Collection<V>>();
        for (Production p : producciones) {
            if (p.getAntecedente().equals(antecedente)) {
                cons.add(p.getConsecuente());
            }
        }
        return cons;
    }

    public Production obtenerProduccion(int n) {
        ArrayList<Production> oP = (ArrayList) this.producciones;
        return oP.get(--n);
    }

    public List<Production> obtenerProducionesConUnNoTerminalEnLosConsecuentes(VN v) {
        List<Production> l = new ArrayList<Production>();
        for (Production p : this.producciones) {
            if (p.getConsecuente().contains(v)) {
                l.add(p);
            }
        }
        return l;
    }
    
    
    public void addProduccion(int i, Production p) {
        ((ArrayList<Production>)this.producciones).add(i, p);
    }
    
    public void addVN (VN v) {
        this.listaVN.add(v);
    }
    
    public void addVT (VT v) {
        this.listaVT.add(v);
    }
    
    public String getPath() {
        return path;
    }
}
