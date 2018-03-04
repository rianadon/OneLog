package onelog;


public class UsbDevice {

	String path;
	String volumeName;
	long serialNumber;
	String fileSystemName;
	long maxComponentLen;

	public String toString() {
		return "UsbDevice[" +
			"path=" + path + ", " +
			"volumeName=" + volumeName + ", " +
			"serialNumber=" + serialNumber + ", " +
			"fileSystemName=" + fileSystemName + ", " +
			"maxComponentLen=" + maxComponentLen +
			"]";
	}
}
