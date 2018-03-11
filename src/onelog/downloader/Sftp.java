package onelog.downloader;

import com.jcraft.jsch.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sftp
 */
public class Sftp extends Downloader {

    private static java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(Sftp.class.getName());

    private String host;
    private Integer port;
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
        for (String src : files) {
            String dst = Paths.get(root, new File(src).getName()).toString();
            logger.fine("Downloading " + src + " to " + dst);
            sftpChannel.get(src, dst);
        }
        sftpChannel.exit();
        session.disconnect();
    }

    @Override
    public List<Path> exports(String root) {
        return files.stream().map(f ->
            Paths.get(root, new File(f).getName())
        ).collect(Collectors.toList());
    }

}
