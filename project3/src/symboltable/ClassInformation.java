package symboltable;

public class ClassInformation {
    String className;
    int hashCode;

    public String getClassName() { return this.className; }
    public void setClassName(String className) { this.className = className; }

    // methods to allow us to use this class as a key to a map
    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ClassInformation that = (ClassInformation) obj;
        return this.className.equals(that.className);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return this.className;
    }
}
