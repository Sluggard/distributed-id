import lombok.extern.slf4j.Slf4j;

/**
 * Main
 *
 * @author Jun.An3
 * @date 2022/08/24
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info(String.valueOf(3 | 3));
        //00000001
        //00000001
        //00000001
        log.info(String.valueOf(1 | 4));
        //00000001
        //00000100
        //00000101
        log.info(String.valueOf(3 | 6));
        //00000011
        //00000110
        //00000111
        log.info(String.valueOf((1 & 0) == 0));
        //00000001
        //00000000
        //00000000
        print("1","2","3");
    }

    public static void print(String... args){
        for (String arg : args) {
            log.info(arg);
        }
    }

}
