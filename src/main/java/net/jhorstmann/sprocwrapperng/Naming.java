package net.jhorstmann.sprocwrapperng;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Naming {
    ;


    public static String underscoreToCamel(String str) {
        Pattern pattern = Pattern.compile("(?:^|_)([a-z])");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String letter = matcher.group(1);
            matcher.appendReplacement(sb, matcher.start() == 0 ? letter : letter.toUpperCase(Locale.ROOT));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


}
