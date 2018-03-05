package onelog;

import java.util.function.Consumer;

/**
 * Example jni operations. Ex passing primitives, arrays, calling methods etc..
 *
 * Note, you have to add the following to the JVM vm arguments:
 * -Djava.library.path=jni
 *
 */
public class BackgroundProcess {
    static {
       System.loadLibrary("OneLog");
    }

    private String volumeName;
    private Consumer<UsbDevice> handler;

    public BackgroundProcess(String volumeName, Consumer<UsbDevice> handler) {
        this.volumeName = volumeName;
    }

    public static void main(String[] args) {
        UserNotification.doLaunch();
        BackgroundProcess proc = new BackgroundProcess("PATRIOT", (s) -> {
            try {
                UserNotification.go();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        proc.mainLoop();
    }

    public native void mainLoop();

    private void handleDevice(UsbDevice device) {
        System.out.println("USB Device plugged in: " + device);
        if (device.volumeName.equals(volumeName)) {
            System.out.println("It's a match!");
            handler.accept(device);
        }
    }
 }
