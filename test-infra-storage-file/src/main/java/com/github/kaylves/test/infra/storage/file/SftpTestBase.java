package com.github.kaylves.test.infra.storage.file;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SftpTestBase implements AutoCloseable {

    protected String sftpHost = "localhost";
    protected int sftpPort = 22;
    protected String sftpUsername = "test";
    protected String sftpPassword = "test";

    protected ChannelSftp createSftpChannel() throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(sftpPassword);
        session.connect();
        try {
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            return channel;
        } catch (Exception e) {
            session.disconnect();
            throw e;
        }
    }

    protected void disconnectSftp(ChannelSftp channel) {
        if (channel == null) {
            return;
        }
        Session session = null;
        try {
            session = channel.getSession();
        } catch (Exception ignored) {
        }
        try {
            channel.disconnect();
        } catch (Exception ignored) {
        }
        if (session != null) {
            try {
                session.disconnect();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void close() {
    }
}
