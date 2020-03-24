package test;

public class A {
    public static void main(String[] args) {
        String s = "aaa/bbbb/cc/";
        boolean f = false;
        if (s.charAt(s.length() - 1) == '/') {
            f = true;
        }
        int i1 = s.lastIndexOf("/", s.length() - 2);
        int i2 = s.lastIndexOf("/", i1 - 1);
        int index = f?s.length() - 1:s.length();
        System.out.println(s.substring(i2 + 1, index) );
    }
}
