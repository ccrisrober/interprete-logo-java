package Dependencies.PR1;

import Dependencies.PR1.Symbols.V;
import Dependencies.PR1.Symbols.VN;
import Dependencies.PR1.Symbols.VT;
import Dependencies.PR6.Production;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileReader {

    //Atributos.
    protected String path;

    //Constructores.
    public FileReader(String path) {
        this.path = path;
    }

    //Getter & Setter.
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //Métodos.
    /**
     * Utilizado en la Práctica 3.
     *
     * @param lineas
     */
    public List<String> devolverLineas() {

        List<String> lineas = new ArrayList<String>();

        try {

            FileInputStream file = new FileInputStream(this.path);
            InputStreamReader isr = new InputStreamReader(file, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.length() > 0) {
                    lineas.add(linea);
                }
            }

        }
        catch (Exception e) {
            throw new IOException("Error de parseo");
        }
        finally {
            return lineas;
        }

    }

    /**
     * Utilizado en la Práctica 1.
     *
     * @param listaVN
     * @param listaVT
     * @param producciones
     * @param simbInicial
     */
    public void analizarFichero(Collection<VN> listaVN, Collection<VT> listaVT, Collection<Production> producciones, VN simbInicial) throws IOException {

        try {

            FileInputStream file = new FileInputStream(this.path);
            InputStreamReader isr = new InputStreamReader(file, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String aux;
            String[] split;


            aux = br.readLine(); //Línea que describe la gramática

            //Tratamos VN.

            aux = br.readLine(); //Línea de VN.
            String fragmento_VN = aux.substring(6, aux.length() - 1);
            split = fragmento_VN.split(", ");
            for (String s : split) {
                listaVN.add(new VN(s));
            }

            //Tratamos VT.

            aux = br.readLine(); //Línea de VT.
            String linea_VT = aux.substring(6, aux.length() - 1);
            split = linea_VT.split(", ");
            for (String s : split) {
                listaVT.add(new VT(s));
            }

            //Tratamos P.

            aux = br.readLine(); //Línea de P.

            while ((aux = br.readLine()).compareTo("}") != 0) {
                if (!aux.startsWith("//") && aux.length() != 0) {
                    split = aux.split(" -> ");
                    VN antecedente = new VN(split[0]);
                    List<V> consecuentes = analizarConsecuentes(split[1], listaVN, listaVT);
                    producciones.add(new Production(antecedente, consecuentes));
                }
            }

            //Tratamos S.

            aux = br.readLine(); //Línea de S.
            String simI = aux.substring(4);
            simbInicial.setV(simI);

        }
        catch (Exception e) {
            throw new IOException("Error de parseo");
        }

    }

    private List<V> analizarConsecuentes(String consecuentes, Collection<VN> listaVN, Collection<VT> listaVT) {
        List<V> toReturn = new ArrayList<V>();
        String[] sp = consecuentes.split(" ");
        for (int i = 0; i < sp.length; i++) {
            String c = sp[i];//consecuentes.charAt(i);
            if (esTerminal(c, listaVN, listaVT)) {
                VT consecuente = new VT(c + "");
                toReturn.add(consecuente);
            }
            else {
                toReturn.add(new VN(c + ""));
            }
        }
        return toReturn;
    }

    private boolean esTerminal(String simb, Collection<VN> listaVN, Collection<VT> listaVT) {
        VT v = new VT(simb);
        return listaVT.contains(v);//!(Character.isUpperCase(simb));
    }

    /**
     * Utilizado en la Práctica 10.
     *
     * @param listaVN
     * @param listaVT
     * @param producciones
     * @param simbInicial
     */
    public void analizarFicheroPR_10(Collection<VN> listaVN, Collection<VT> listaVT, Collection<Production> producciones, VN simbInicial) throws IOException {

        try {

            FileInputStream file = new FileInputStream(this.path);
            InputStreamReader isr = new InputStreamReader(file, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String aux;
            String[] split;


            aux = br.readLine(); //Línea que describe la gramática

            //Tratamos VN.

            aux = br.readLine(); //Línea de VN.
            String fragmento_VN = aux.substring(6, aux.length() - 1);
            split = fragmento_VN.split(", ");
            for (String s : split) {
                listaVN.add(new VN(s));
            }

            //Tratamos VT.

            aux = br.readLine(); //Línea de VT.
            String linea_VT = aux.substring(6, aux.length() - 1);
            split = linea_VT.split(", ");
            for (String s : split) {
                listaVT.add(new VT(s));
            }

            //Tratamos P.

            aux = br.readLine(); //Línea de P.

            while ((aux = br.readLine()).compareTo("}") != 0) {
                if (!aux.startsWith("//") && aux.length() != 0) {
                    split = aux.split(" ::= ");
                    VN antecedente = new VN(split[0]);
                    List<V> consecuentes = analizarConsecuentes(split[1], listaVN, listaVT);
                    producciones.add(new Production(antecedente, consecuentes));
                }
            }

            //Tratamos S.

            aux = br.readLine(); //Línea de S.
            String simI = aux.substring(4);
            simbInicial.setV(simI);

        }
        catch (Exception e) {
            throw new IOException("Error de parseo");
        }

    }

    /**
     * Utilizado en la Práctica 10.
     *
     * @return List<String>
     */
    public List<String> devolverProducciones() throws IOException {
        try {

            FileInputStream file = new FileInputStream(this.path);
            InputStreamReader isr = new InputStreamReader(file, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String aux;

            br.readLine();
            
            //Saltamos VN.
            aux = br.readLine(); //Línea de VN.

            //Saltamos VT.
            aux = br.readLine(); //Línea de VT.

            //Tratamos P.
            aux = br.readLine(); //Línea de P.

            List<String> listaProducciones = new ArrayList<String>();
            while ((aux = br.readLine()).compareTo("}") != 0) {
                listaProducciones.add(aux);
            }

            //Saltamos S.
            aux = br.readLine(); //Línea de S.

            return listaProducciones;

        }
        catch (Exception e) {
            throw new IOException("Error de parseo");
        }
    }
    
    public String[] palabrasReservadas () throws IOException {
        try {

            FileInputStream file = new FileInputStream(this.path);
            InputStreamReader isr = new InputStreamReader(file, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            
            String aux = br.readLine().substring(1);
            String[] split = aux.split(", ");
            return split;
        }
        catch (Exception e) {
            throw new IOException("Error de parseo");
        }
    }
}