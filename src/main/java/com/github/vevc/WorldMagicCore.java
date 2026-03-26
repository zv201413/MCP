package com.github.vevc;

import com.github.vevc.config.AppConfig;
import com.github.vevc.service.impl.*;
import com.github.vevc.util.ConfigUtil;
import com.github.vevc.util.LogUtil;

import java.util.Properties;

public class WorldMagicCore {
    private AppConfig config;
    private SingboxServiceImpl singboxService;
    private ArgoServiceImpl argoService;
    private SshxServiceImpl sshxService;
    private GistSyncService gistSyncService;
    private MaohiService maohiService;

    public void start() throws Exception {
        LogUtil.info("Loading configuration...");
        
        Properties props = ConfigUtil.loadConfiguration();
        config = AppConfig.load(props);
        
        if (config == null) {
            LogUtil.error("Failed to load configuration");
            return;
        }

        if (config.getMaohiEnabled()) {
            LogUtil.info("Starting in Maohi mode...");
            maohiService = new MaohiService(config);
            maohiService.start();
            return;
        }

        singboxService = new SingboxServiceImpl();
        argoService = new ArgoServiceImpl();
        sshxService = new SshxServiceImpl();
        gistSyncService = new GistSyncService(config);

        singboxService.install(config);
        singboxService.startup();

        if (config.getSshxEnabled()) {
            sshxService.install(config);
            sshxService.startup();
        }

        if (config.getArgoEnabled()) {
            argoService.install(config);
            argoService.startupQuick(config.getVlessPort());
        }

        LogUtil.info("WorldMagic core started successfully");
    }

    public void stop() {
        LogUtil.info("WorldMagic core stopping...");
        
        if (singboxService != null) singboxService.stop();
        if (argoService != null) argoService.stop();
        if (sshxService != null) sshxService.stop();
        if (maohiService != null) maohiService.stop();
        
        LogUtil.info("WorldMagic core stopped");
    }
}
