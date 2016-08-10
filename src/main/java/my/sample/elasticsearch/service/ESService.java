package my.sample.elasticsearch.service;

import java.util.List;
import java.util.Map;

public interface ESService {
    List<String> handleLocalPlugin(String command, String target);
    List<String> getPluginInfo();
    Map<String, String> getNodeSetting();
}
