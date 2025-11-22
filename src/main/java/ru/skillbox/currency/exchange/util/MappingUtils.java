package ru.skillbox.currency.exchange.util;

public class MappingUtils {

    // Метод, который MapStruct будет использовать для преобразования
    public static Double stringToDouble(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // Заменяем запятую на точку, чтобы Java мог распарсить число
        String sanitizedValue = value.replace(',', '.');
        try {
            return Double.parseDouble(sanitizedValue);
        } catch (NumberFormatException e) {
            // В идеале здесь нужно добавить log.error()
            return null;
        }
    }
}