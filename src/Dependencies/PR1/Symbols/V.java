package Dependencies.PR1.Symbols;

/**
 * Vocabulario.
 */

public abstract class V{
    
    //Atributos.
    
    private String contenido;
    public Object valor;
    //Contructores.
    
    public V() {
        this.contenido = "";
    }
    
    public V(String v) {
        this.contenido = v;
    }
    
    //Getter & Setter.

    public String getV() {
        return contenido;
    }

    public void setV(String v) {
        this.contenido = v;
    }
    
    //Métodos.
    
    @Override
    public String toString(){
        return contenido;
    }
    
    @Override
    public int hashCode (){
        return this.contenido.hashCode();
    }
    
    @Override
    public boolean equals (Object o){
        V simb = new V("") {};
        try{
            simb=(V)o;
        }catch(ClassCastException c){
            System.err.println("Error de casteo.");
        }
        return this.hashCode()==simb.hashCode();
    }
}
