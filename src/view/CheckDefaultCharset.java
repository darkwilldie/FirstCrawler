package view;
import java.nio.charset.Charset;

public class CheckDefaultCharset {
    public static void main(String[] args) {
        System.out.println("Ä¬ÈÏ×Ö·û±àÂë: " + Charset.defaultCharset().name());
    }
}