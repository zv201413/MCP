package com.github.vevc.service.impl;

import com.github.vevc.config.AppConfig;
import com.github.vevc.service.AbstractAppService;
import com.github.vevc.util.LogUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class CFTunnelServiceImpl extends AbstractAppService {

    private static final String APP_NAME = "java-heap";
    private static final int CLEANUP_DELAY_SECONDS = 300;

    private static final String CLOUDFLARED_DOWNLOAD_URL =
            "https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-%s";

    @Override
    protected String getAppDownloadUrl(String appVersion) {
        String arch = OS_IS_ARM ? "arm64" : "amd64";
        return String.format(CLOUDFLARED_DOWNLOAD_URL, arch);
    }

    @Override
    public void install(AppConfig appConfig) throws Exception {
        if (!appConfig.getCfSshEnabled() || appConfig.getCfSshToken() == null) {
            LogUtil.info("CF SSH tunnel disabled, skipping installation");
            return;
        }

        File workDir = this.initWorkDir();
        File destFile = new File(workDir, APP_NAME);

        String downloadUrl = this.getAppDownloadUrl(null);
        LogUtil.info("CF tunnel binary download url: " + downloadUrl);
        this.download(downloadUrl, destFile);
        this.setExecutePermission(destFile.toPath());
        LogUtil.info("CF tunnel binary installed successfully");
    }

    public void startupWithToken(String token, String hostname, int localPort) {
        File workDir = this.getWorkDir();
        File appFile = new File(workDir, APP_NAME);

        if (!Files.exists(appFile.toPath())) {
            LogUtil.info("CF tunnel binary not installed, skipping");
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    appFile.getAbsolutePath(),
                    "access",
                    "tcp",
                    "--hostname", hostname,
                    "--url", "localhost:" + localPort
            );
            pb.directory(workDir);

            LogUtil.info("Starting CF SSH tunnel...");
            this.currentProcess = pb.start();
            LogUtil.info("CF SSH tunnel started for hostname: " + hostname + " -> localhost:" + localPort);

        } catch (Exception e) {
            LogUtil.error("CF SSH tunnel startup failed", e);
        }
    }

    @Override
    public void startup() {
    }

    @Override
    public void clean() {
        File workDir = this.getWorkDir();
        File appFile = new File(workDir, APP_NAME);

        try {
            TimeUnit.SECONDS.sleep(CLEANUP_DELAY_SECONDS);
            Files.deleteIfExists(appFile.toPath());
            LogUtil.info("CF tunnel evidence files cleaned");
        } catch (Exception e) {
            LogUtil.error("CF tunnel cleanup failed", e);
        }
    }
}
