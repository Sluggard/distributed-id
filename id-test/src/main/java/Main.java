/**
 * Main
 *
 * @author Jun.An3
 * @date 2022/08/24
 */
public class Main {

    public static void main(String[] args) {
        System.out.println(3 | 3);
        //00000001
        //00000001
        //00000001
        System.out.println(1 | 4);
        //00000001
        //00000100
        //00000101
        System.out.println(3 | 6);
        //00000011
        //00000110
        //00000111
        System.out.println((1 & 0) == 0);
        //00000001
        //00000000
        //00000000
        print("1","2","3");
    }

    public static void print(String... args){
        for (String arg : args) {
            System.out.println(arg);
        }
    }

}
