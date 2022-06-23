package symboltable;

// custom pair implementation
public class CustomPair<U, V> {

    public final U firstObj;
    public final V secondObj;

    // Pair constructor
    public CustomPair(U firstObj, V secondObj) {
        this.firstObj = firstObj;
        this.secondObj = secondObj;
    }

    @Override
    // Checks specified object is "equal to" the current object or not
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CustomPair<?, ?> pair = (CustomPair<?, ?>) o;

        // call `equals()` method of the underlying objects
        if (!firstObj.equals(pair.firstObj)) {
            return false;
        }
        return secondObj.equals(pair.secondObj);
    }

    @Override
    // Computes hash code for an object to support hash tables
    public int hashCode()
    {
        // use hash codes of the underlying objects
        return 31 * firstObj.hashCode() + secondObj.hashCode();
    }

    @Override
    public String toString() {
        return "(" + firstObj + ", " + secondObj + ")";
    }
}
