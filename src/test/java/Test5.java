import java.util.Random;

/**
 * @ClassName: Test5
 * @Description:
 * @Author: GuLe
 * @Date: 2022/3/29 11:17
 */
public class Test5 {
    public static void main(String[] args) {
        Random random = new Random();
        double c = 0.0;
        for (int i = 0; i < 100000; i++) {
            double x = random.nextDouble() * 2 - 1;
            double y = random.nextDouble() * 2 - 1;
            if (x * x + y * y < 1) c += 1;
        }
        System.out.println(4*c/100000);
    }
}
