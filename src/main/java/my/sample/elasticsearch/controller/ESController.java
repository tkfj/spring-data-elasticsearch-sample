package my.sample.elasticsearch.controller;

import my.sample.elasticsearch.service.ESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "es")
public class ESController {

    @Autowired
    ESService esService;

    @PostMapping(value = "plugin")
    public List<String> handleLocalPlugin(
        @RequestParam String command,
        @RequestParam String target) {

        return esService.handleLocalPlugin(command, target);
    }

    @GetMapping(value = "plugin")
    public List<String> showPlugin() {
        return esService.getPluginInfo();
    }

    @GetMapping(value = "setting")
    public Map<String, String> showSetting() {
        return esService.getNodeSetting();
    }
}
