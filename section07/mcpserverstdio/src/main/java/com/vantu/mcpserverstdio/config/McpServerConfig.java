package com.vantu.mcpserverstdio.config;

import com.vantu.mcpserverstdio.tool.HelpDeskTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpServerConfig  {
    @Bean
    List<ToolCallback> toolCallbacks(HelpDeskTools helpDeskTools){
        // Quét các method trong HelpDeskTools, biến chúng thành ToolCallback (để AI có thể gọi)
        return List.of(ToolCallbacks.from(helpDeskTools));
    }
}
