package my.sample.elasticsearch.service;

import java.util.List;

public interface ESService {
    void execute();
    List<String> handlePlugin(String command, String target);
    List<String> showPlugin();
}
