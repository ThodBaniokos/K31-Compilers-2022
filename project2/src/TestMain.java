class TestMain {
    public static void main(String[] args) {
    }
}

class A{
    int i;
    boolean flag;
    int j;
    public int foo() {return 0;}
    public boolean fa() { return false; }
}

class B extends A{
    A type;
    int k;
    public int foo() {return 0;}
    public boolean bla() { return false; }
}