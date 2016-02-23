import java.util.HashSet;
import java.util.Set;

public class HashMapQuestion {

    public static void main(String[] argv) {
        Set<Element> elements = new HashSet<>();

        elements.add(new Element("Hello"));
        elements.add(new Element("Hello"));

        System.out.println(elements.size());
    }


    public static class Element {
        private final String val;

        Element(final String val) {
            this.val = val;
        }

        public String getVal() {
            return this.val;
        }
    }
}
