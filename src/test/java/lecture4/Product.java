package lecture4;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class Product {

    private String name;
    private int amount;
    private String price;

    public Product() {
        this.name = getRandomWord();
        this.amount = 1 + (int) (Math.random() * 100);
        double startPrice = 0.1 + (Math.random() * 100);
        this.price = String.valueOf(new BigDecimal(startPrice).setScale(2, RoundingMode.HALF_UP).floatValue());
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public String getPrice() {
        return price;
    }

    private static String getRandomWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (3 + (int) (Math.random() * 15)); i++) {
            char c = (char)(new Random().nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }

}
