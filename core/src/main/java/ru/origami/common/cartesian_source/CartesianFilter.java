package ru.origami.common.cartesian_source;

/*
    Пример реализации:

    public static class MyFilter implements CartesianFilter {
        @Override public boolean test(Object[] args) {
            int number = (int) args[0];
            String letter = (String) args[1];
            boolean flag = (boolean) args[2];

            return number == 3 && letter.equals("A") && flag; // исключить
        }
    }
 */
public interface CartesianFilter {

    boolean test(Object[] args); // args — значения параметров (уже без Named)
}
