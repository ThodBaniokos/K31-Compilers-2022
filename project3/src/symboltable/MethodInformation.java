package symboltable;

public class MethodInformation {
    String methodName;
    int hashCode;

    public String getMethodName() { return this.methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }

    // methods to allow us to use this class as a key to a map
    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        MethodInformation that = (MethodInformation) obj;
        return this.methodName.equals(that.methodName);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return this.methodName;
    }
}
