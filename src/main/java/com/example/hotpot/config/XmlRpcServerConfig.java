package com.example.hotpot.config;

import com.example.hotpot.rpc.HotpotRpcService;
import org.apache.xmlrpc.webserver.WebServer;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class XmlRpcServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(XmlRpcServerConfig.class);

    @PostConstruct
    public void startXmlRpcServer() {
        new Thread(() -> {
            try {
                int port = 8089;
                WebServer webServer = new WebServer(port);

                XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
                PropertyHandlerMapping phm = new PropertyHandlerMapping();

                // Expose HotpotRpcService with name "HotpotService"
                phm.addHandler("HotpotService", HotpotRpcService.class);
                xmlRpcServer.setHandlerMapping(phm);

                XmlRpcServerConfigImpl config = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
                config.setEnabledForExtensions(true);
                config.setContentLengthOptional(false);

                webServer.start();
                logger.info("XML-RPC server started on port {}", port);
            } catch (Exception e) {
                logger.error("Error starting XML-RPC server: {}", e.getMessage());
            }
        }, "XML-RPC-Server-Thread").start();
    }
}
