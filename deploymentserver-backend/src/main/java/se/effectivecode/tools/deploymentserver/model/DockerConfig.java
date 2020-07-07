package se.effectivecode.tools.deploymentserver.model;

import lombok.Builder;
import lombok.Data;
import se.effectivecode.tools.deploymentserver.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class DockerConfig {
    public static final String SPACE = " ";
    private static final String EXPOSED_PORTS = "exposed";
    private static final String BIND_PORTS = "bind";
    public static final String COLON = ":";
    private String portString;
    private String containerName;
    private String volumeLinux;
    private String volumeWindows;
    private String network;
    private String hostname;
    private String endline;
    private String dockerfileLocation;
    private boolean restart;

    //TODO Eventually there is a need to handle all different kinds of port
    // definition. Like specific ip nummer and/or UDP/TCP. Default now
    // is only TCP bindings.
    public List<String> getSplitPorts() {
        List<String> portCombos = new ArrayList<>();
        for (String portCombo : portString.split(" ")) {
            if (StringUtil.isNotEmpty(portCombo) && portCombo.contains(COLON)) {
                if (portCombo.indexOf(COLON) > 0) {
                    portCombos.add(portCombo);
                }
            }
        }
        return portCombos;
    }

    public String volume() {
        String os = System.getProperty("os.name");
        String out = "";
        if (os.contains("Windows")) {
            if (StringUtil.isNotEmpty(volumeWindows)) {
                out = volumeWindows;
            }
        } else {
            if (StringUtil.isNotEmpty(volumeLinux)) {
                out = volumeLinux;
            }
        }
        return out;
    }

    public Set<String> getExposedPorts() {
        Set<String> exposedPorts = new HashSet<>();
        for (String portCombo : getSplitPorts()) {
            int splitIndex = portCombo.indexOf(DockerConfig.COLON);
            String containerPort = portCombo.substring(splitIndex + 1) + "/tcp";
            exposedPorts.add(containerPort);
        }
        return exposedPorts;
    }
}
