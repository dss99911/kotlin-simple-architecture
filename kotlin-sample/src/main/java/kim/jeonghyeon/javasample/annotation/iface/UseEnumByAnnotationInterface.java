package kim.jeonghyeon.javasample.annotation.iface;

public class UseEnumByAnnotationInterface {
    int type = EnumByAnnotationInterface.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS;

    public void main(@EnumByAnnotationInterface int a) {
        if (a == 1) {
            System.out.println("asdf");
        }
        if (type == a) {
            System.out.println("it is unknown");
        }
    }

}
