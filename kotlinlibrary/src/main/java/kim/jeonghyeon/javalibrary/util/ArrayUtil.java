package kim.jeonghyeon.javalibrary.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;


@SuppressWarnings({"WeakerAccess", "unused"})
public class ArrayUtil {

    /**
     * Ignore null or empty value
     *
     * @param delimiter delimiter
     * @param array     objects
     * @param <T>       any object to join
     * @return joined string
     */
    @SafeVarargs
    public static <T> String join(@Nullable String delimiter, @NotNull T... array) {
        if (isEmpty(array)) {
            return "";
        }

        if (delimiter == null) {
            delimiter = "";
        }

        StringBuilder b = new StringBuilder();
        for (T v : array) {
            if (v == null || StringUtil.isEmpty(String.valueOf(v))) continue;

            if (b.length() != 0) {
                b.append(delimiter);
            }
            b.append(v);
        }

        return b.toString();
    }

    public static String join(@Nullable String delimiter, @NotNull int[] array) {
        if (isEmpty(array)) {
            return "";
        }

        if (delimiter == null) {
            delimiter = "";
        }

        StringBuilder b = new StringBuilder();
        for (int v : array) {
            if (b.length() != 0) {
                b.append(delimiter);
            }
            b.append(v);
        }

        return b.toString();
    }


    public static boolean isEmpty(@Nullable int[] array) {
        return array == null || array.length == 0;
    }


    public static boolean isEmpty(@Nullable Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isEmpty(@Nullable T[] array) {
        return array == null || array.length == 0;
    }

    @Nullable
    public static <T> T get(@Nullable T[] array, int idx) {
        if (array == null) {
            return null;
        }

        if (0 <= idx && idx < array.length) {
            return array[idx];
        }
        return null;
    }

    public static <T> boolean contains(T[] array, T item) {
        return indexOf(array, item) != -1;
    }

    @SuppressWarnings("WeakerAccess")
    public static <T> int indexOf(@Nullable T[] array, @Nullable T item) {
        if (array == null || item == null) {
            return -1;
        }

        for (int i = 0; i < array.length; i++) {
            T t = array[i];
            if (item.equals(t)) {
                return i;
            }
        }

        return -1;
    }

    public static int indexOf(@Nullable int[] array, int item) {
        if (array == null) {
            return -1;
        }

        for (int i = 0; i < array.length; i++) {
            int t = array[i];
            if (item == t) {
                return i;
            }
        }

        return -1;
    }

    public static <T> int indexOf(@Nullable List<T> list, @Nullable T item) {
        if (list == null || item == null) {
            return -1;
        }

        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            if (item.equals(t)) {
                return i;
            }
        }

        return -1;
    }


    public static int[] toArray(int... items) {
        return items;
    }

    public static boolean[] toArray(boolean... items) {
        return items;
    }

    public static String[] toArray(String... items) {
        return items;
    }

    public static float[] toArray(float... items) {
        return items;
    }
}
