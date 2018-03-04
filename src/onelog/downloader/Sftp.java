package onelog.downloader;

import com.jcraft.jsch.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sftp
 */
public class Sftp extends Downloader {

    private String host;
    private int port;
    private List<String> files;
    private String username;
    private String password;

	@Override
	public void download(String root) throws JSchException, SftpException {
        JSch jsch = new JSch();
        Session session = null;
        session = jsch.getSession(username, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        for (String file : files) {
            sftpChannel.get(file, Paths.get(root, new File(file).getName()).toString());
        }
        sftpChannel.exit();
        session.disconnect();
    }

    public List<String> exports(String root) {
        return files.stream()
                    .map((file) -> Paths.get(root, new File(file).getName()).toString())
                    .collect(Collectors.toList());
    }

}
