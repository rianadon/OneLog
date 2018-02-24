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

    public BackgroundProcess(String volumeName) {
        this.volumeName = volumeName;
    }

    public static void main(String[] args) {
        BackgroundProcess proc = new BackgroundProcess("USB DISK");
        proc.mainLoop();
    }

    public native void mainLoop();

    private void handleDevice(UsbDevice device) {
        System.out.println("USB Device plugged in: " + device);
        if (device.volumeName.equals(volumeName)) {
            System.out.println("It's a match!");
        }
    }
 }
