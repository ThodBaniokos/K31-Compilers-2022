class Example {
    public static void main(String[] args) {
        A test;

        test = new C();
        System.out.println(test.foo(10, 122));
        System.out.println(new B().foobar(true));
    }
}

class E {
    int k;
    int[] l;

    public int foo(int a, int b) {
	k = 1;
        return a + b;
    }
}
class A {
    int i;
    int j;
    int k;
    int l;
    int g;
    A a;

    public int foo(int i, int j) { return i+j; }
    public int bar(){ return 1; }
}

class B extends A {
    int i;
    int k;
    int asfd;
    int asfdasd;
    int s;

    public int foobar(boolean k) {return 1;}
}



class C extends B {
    int l;

    public int foo(int as, int te) { return as + te; }
}
