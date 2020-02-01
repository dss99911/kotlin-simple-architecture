package kim.jeonghyeon.javasample.lombok;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

// 'toBuilder' attribute allows a model object to make a builder
// with the current field values as default values.
@Builder(toBuilder = true)
@Getter
// (Optional) Overrides equals and hashCode methods with the specified field.
// It will be helpful when this object is inserted into a collection.
@EqualsAndHashCode(of = "name")
// Overrides toString method.
@ToString
public class User {
    private final String name;
    private final int score;
}
