package irgeneration;

public class RegisterInformation {

    String registerName;
    String registerType;

    int hashCode;

    public String getRegisterName() { return this.registerName; }
    public void setRegisterName(String registerName) { this.registerName = registerName; }
    public String getRegisterType() { return this.registerType; }
    public void setRegisterType(String registerType) { this.registerType = registerType; }

    // methods to allow us to use this class as a key to a map
    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RegisterInformation that = (RegisterInformation) obj;
        return this.registerName.equals(that.registerName);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return this.registerName;
    }
}
