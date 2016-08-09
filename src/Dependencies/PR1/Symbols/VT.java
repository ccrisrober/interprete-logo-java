package Dependencies.PR1.Symbols;

/**
 * Símbolo terminal.
 */

public class VT extends V {
    
    //Constructores.
    
    public VT () {
        super();
    }
    
    public VT (String vn) {
        super(vn);
    }
    
    @Override
    public VT clone(){
        return new VT(getV());
    }
    
}
