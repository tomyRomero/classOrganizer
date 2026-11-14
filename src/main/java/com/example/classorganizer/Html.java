package com.example.classorganizer;

/** Makes typed text safe to put inside a page. Public because the JSP pages call it too. */
public final class Html {

    private Html() {
    }

    /** Escapes text so the browser shows it as text and never runs it as HTML or script. */
    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            switch (c) {
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '&' -> out.append("&amp;");
                case '"' -> out.append("&quot;");
                case '\'' -> out.append("&#39;");
                default -> out.append(c);
            }
        }
        return out.toString();
    }
}
