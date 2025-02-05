package ar.com.elbaden.utils;

public final class Strings {

    private Strings() {}

    public static String convertToHTML(String value) {
        String htmlFormat = "<HTML>";
        htmlFormat += value.replaceAll("(\r\n|\n)", "<br>");
        return htmlFormat + "</HTML>";
    }

}
