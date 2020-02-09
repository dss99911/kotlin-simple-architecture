package kim.jeonghyeon.javasample.annotation.iface;

/**
 * No need to declare enum type if it is just simple int.
 * same way to declare static field. but it is more readable.
 * but if doesn't mention, doesn't know if the Int type has this fields
 */
public @interface EnumByAnnotationInterface {
    int UNKNOWN = 0;
    int UPDATE_NOT_AVAILABLE = 1;
    int UPDATE_AVAILABLE = 2;
    int DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS = 3;
}
