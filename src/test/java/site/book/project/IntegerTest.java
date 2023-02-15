package site.book.project;

import org.junit.jupiter.api.Test;

public class IntegerTest {
    
    @Test
    public void test() {
        int n1 = 1;
        Integer n2 = Integer.valueOf(1);
        System.out.println(n1 == n2);
    }

}
