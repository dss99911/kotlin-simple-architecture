package kim.jeonghyeon.javalibrary.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"WeakerAccess", "unused"})
public class StringUtil {
    public static boolean containsIgnoreCase(@Nullable String container, @Nullable String checkString) {
        if (container == null || checkString == null) return false;

        String containerLower = container.toLowerCase();
        String checkLower = checkString.toLowerCase();
        return containerLower.contains(checkLower);
    }

    public static boolean startWithIgnoreCase(@Nullable String allString, @Nullable String startString) {
        if (allString == null || startString == null) return false;

        String allLower = allString.toLowerCase();
        String startLower = startString.toLowerCase();
        return allLower.startsWith(startLower);
    }


    public static boolean isEmpty(@Nullable CharSequence cs) {
        return ((cs == null) || (cs.length() == 0));
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return (!(isEmpty(cs)));
    }

    @NotNull
    public static StringBuilder makeStringBuilder(@Nullable StringBuilder sb,
                                                  @NotNull Object... strings) {
        if (sb == null)
            sb = new StringBuilder();
        for (Object s : strings) {
            if (s == null) {
                continue;
            }
            sb.append(s);
        }
        return sb;
    }

    public static String makeString(Object... strings) {
        return makeStringBuilder(new StringBuilder(), strings).toString();
    }
}