package com.example.myschedule.utils;

public class StringUtils {

    public static String capitalizeFirstLetter(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }

        char firstLetter = word.charAt(0);
        char capitalizeLetter = Character.toUpperCase(firstLetter);

        return capitalizeLetter + word.substring(1);
    }

    public static String formatLessonCount(int count) {
        int lastDigit = count % 10;
        if (count >= 11 && count <= 19) {
            return count + " пар";
        }
        if (lastDigit == 1) {
            return count + " пара";
        } else if (lastDigit >= 2 && lastDigit <= 4) {
            return count + " пары";
        } else {
            return count + " пар";
        }
    }
}
