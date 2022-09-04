package com.geega.bsc.id.client.netty.node;

import com.geega.bsc.id.common.address.ServerNode;
import com.geega.bsc.id.common.address.ServerNodeExtension;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jun.An3
 * @date 2022/07/11
 */
@Slf4j
public class ServerNodeInformation {

    private final CopyOnWriteArrayList<ServerNode> nodes = new CopyOnWriteArrayList<>();

    public List<ServerNode> getNodes() {
        return this.nodes;
    }

    public synchronized ServerNode updateServerNode(String ip, Integer port) {
        assert ip != null;
        assert port != null;
        ServerNode serverNode = ServerNode.builder()
                .ip(ip)
                .port(port)
                .extension(ServerNodeExtension.builder().clients(new HashSet<>()).build())
                .build();
        if (!nodes.contains(serverNode)) {
            nodes.add(serverNode);
        } else {
            for (ServerNode node : nodes) {
                if (node.equals(serverNode)) {
                    if (node.getExtension() == null || node.getExtension().getClients() == null) {
                        node.setExtension(ServerNodeExtension.builder().clients(new HashSet<>()).build());
                    }
                    serverNode = node;
                    break;
                }
            }
        }
        return serverNode;
    }

    public synchronized void removeServerNode(String ip, Integer port) {
        assert ip != null;
        assert port != null;
        final ServerNode serverNode = ServerNode.builder()
                .ip(ip)
                .port(port)
                .build();
        nodes.remove(serverNode);
    }

    public synchronized void addClientInfo(String ip, Integer port, String client) {
        assert ip != null;
        assert port != null;
        final ServerNode serverNode = updateServerNode(ip, port);
        serverNode.getExtension().getClients().add(client);
    }

    public synchronized void removeClientInfo(String ip, Integer port, String client) {
        assert ip != null;
        assert port != null;
        final ServerNode serverNode = updateServerNode(ip, port);
        serverNode.getExtension().getClients().remove(client);
    }

}
