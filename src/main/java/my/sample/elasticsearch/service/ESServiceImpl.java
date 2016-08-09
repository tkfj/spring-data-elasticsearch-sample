package my.sample.elasticsearch.service;

import my.sample.elasticsearch.util.JsonGenerator;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.PluginInfo;
import org.elasticsearch.plugins.PluginManagerCliParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ESServiceImpl implements ESService {

    @Autowired
    Client client;

    @Override
    public void execute() {

        prepareIndex(client);

    }

    @Override
    public List<String> handlePlugin(String command, String target) {

        String[] args = {command, target};
        new PluginManagerCliParser().execute(args);

        return showPlugin();
    }

    @Override
    public List<String> showPlugin() {

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


    private void prepareIndex(Client client) {

        IndicesAdminClient adminClient = client.admin().indices();

        // index が既に存在していると作成した際に IndexAlreadyExistsException がスローされるため、削除
        if (adminClient.prepareExists("nested").get().isExists()) {
            adminClient.prepareDelete("nested").get();
        }

        if (adminClient.prepareExists("twitter").get().isExists()) {
            adminClient.prepareDelete("twitter").get();
        }

        adminClient.prepareCreate("nested")
            .setSettings(Settings.builder()
                .put("index.number_of_shards", 1)     // default 5
                .put("index.number_of_replicas", 0))  // default 1 このままだと Cluster Health が  Yellow になる
            .addMapping("parent", "{\n" +
                "    \"parent\": {\n" +
                "      \"properties\": {\n" +
                "        \"children\": {\n" +
                "          \"type\": \"nested\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }")
            .get();
        adminClient.prepareCreate("twitter")
            .setSettings(Settings.builder()
                .put("index.number_of_shards", 1)     // default 5
                .put("index.number_of_replicas", 0))  // default 1 このままだと Cluster Health が  Yellow になる
            .get();

        BulkRequestBuilder bulkRequest = client.prepareBulk();
        try {
            bulkRequest.add(new IndexRequest("twitter", "tweet", "1").source(JsonGenerator.generateJsonString()));
            bulkRequest.add(new IndexRequest("twitter", "tweet", "2").source(JsonGenerator.generateJsonStringByHelper()));
            bulkRequest.add(new IndexRequest("twitter", "tweet", "3").source(JsonGenerator.generateJsonMap()));
            bulkRequest.add(new IndexRequest("twitter", "tweet", "4").source(JsonGenerator.generateJsonArray()));
            bulkRequest.add(new IndexRequest("nested", "parent", "parent-uuid-1").source(JsonGenerator.generateNestedJsonArray(1)));
            bulkRequest.add(new IndexRequest("nested", "parent", "parent-uuid-2").source(JsonGenerator.generateNestedJsonArray(2)));
            bulkRequest.add(new IndexRequest("nested", "parent", "parent-uuid-3").source(JsonGenerator.generateNestedJsonArray(3)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        BulkResponse bulkResponse = bulkRequest.get();

        // 確認
        if (bulkResponse.hasFailures()) {
            // process failures by iterating through each bulk response item
            for (BulkItemResponse response : bulkResponse.getItems()) {
                if (response.isFailed()) {
                    System.out.println(response.getFailure().getMessage());
                }
            }
        }

        // Refresh
        adminClient.prepareRefresh().get();
    }

}
