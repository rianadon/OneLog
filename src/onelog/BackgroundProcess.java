package onelog;

import java.util.function.Consumer;
import onelog.gui.UserNotificationApp;

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
        this.handler = handler;
    }

    public static void main(String[] args) {
        UserNotificationApp.doLaunch();
        BackgroundProcess proc = new BackgroundProcess("PATRIOT", (s) -> {
            UserNotificationApp.createWindow();
        });
        proc.mainLoop();
    }

    public native void mainLoop();

    private void handleDevice(UsbDevice device) {
        System.out.println("USB Device plugged in: " + device);
        if (device.volumeName.equals(volumeName)) {
            System.out.println("It's a match!");
            try {
                handler.accept(device);
            } catch (Exception e) {
                // If we throw an error here C will get confused, so instead print it
                e.printStackTrace();
            }
        }
    }
 }
