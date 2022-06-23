package symboltable;

public class VariableInformation {

    int hashCode;

    String varName;

    public String getVarName() {
        return this.varName;
    }

    public void setVarName(String fieldName) {
        this.varName = fieldName;
    }

    // methods to allow us to use this class as a key to a map
    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        VariableInformation that = (VariableInformation) obj;
        return this.varName.equals(that.varName);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return this.varName;
    }
}
