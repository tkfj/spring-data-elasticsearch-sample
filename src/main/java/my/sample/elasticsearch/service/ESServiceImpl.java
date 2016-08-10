package my.sample.elasticsearch.service;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.plugins.PluginInfo;
import org.elasticsearch.plugins.PluginManagerCliParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ESServiceImpl implements ESService {

    @Autowired
    Client client;

    @Override
    public List<String> handleLocalPlugin(String command, String target) {

        Map<String, String> nodeSetting = getNodeSetting();

        boolean isLocal = Boolean.valueOf(nodeSetting.get("node.local"));

        if (isLocal) {
            String[] args = {command, target};

            // data ディレクトリと同じディレクトリに plugins ディレクトリを作成
            System.setProperty("es.path.home", nodeSetting.get("path.home"));
            new PluginManagerCliParser().execute(args);
        } else {
            List<String> list = new ArrayList<>();
            list.add("Node is not local");
            return list;
        }

        return getPluginInfo();
    }

    @Override
    public List<String> getPluginInfo() {

        NodesInfoResponse nodesInfoResponse = client.admin().cluster().prepareNodesInfo().setPlugins(true).get();
        List<String> plugins = new ArrayList<>();
        for (NodeInfo nodeInfo : nodesInfoResponse.getNodes()) {

            plugins.addAll(
                nodeInfo.getPlugins().getPluginInfos()
                    .stream()
                    .map(PluginInfo::toString)
                    .collect(Collectors.toList()));
        }

        return plugins;
    }

    @Override
    public Map<String, String> getNodeSetting() {
        return client.settings().getAsMap();
    }

}
