public class Nod0 {
    private final Token dato;
    private Nod0 siguiente;

    public Nod0(Token dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Token getDato() {
        return dato;
    }

    public Nod0 getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nod0 siguiente) {
        this.siguiente = siguiente;
    }
}
