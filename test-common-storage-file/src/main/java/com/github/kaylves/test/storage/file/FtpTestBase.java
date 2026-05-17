package com.github.kaylves.test.storage.file;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class FtpTestBase implements AutoCloseable {

    protected String ftpHost = "localhost";
    protected int ftpPort = 21;
    protected String ftpUsername = "test";
    protected String ftpPassword = "test";

    protected FTPClient createFtpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ftpHost, ftpPort);
        if (!ftpClient.login(ftpUsername, ftpPassword)) {
            ftpClient.disconnect();
            throw new IOException("FTP login failed for user: " + ftpUsername);
        }
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }

    protected void disconnectFtp(FTPClient ftpClient) {
        if (ftpClient == null) {
            return;
        }
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (IOException ignored) {
        }
        try {
            ftpClient.disconnect();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() {
    }
}
