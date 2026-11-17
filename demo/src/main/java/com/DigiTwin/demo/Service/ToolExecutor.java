
package com.DigiTwin.demo.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ToolExecutor {

    public List<Map<String, Object>> processToolCalls(
            Map<String, Object> aiResponse) {

        List<Map<String, Object>> actions = new ArrayList<>();

        if (aiResponse == null) {
            return actions;
        }

        Object messageValue = aiResponse.get("message");

        if (!(messageValue instanceof Map)) {
            return actions;
        }

        Map<?, ?> message = (Map<?, ?>) messageValue;

        Object callsValue = message.get("tool_calls");

        if (!(callsValue instanceof List)) {
            return actions;
        }

        List<?> toolCalls = (List<?>) callsValue;

        for (Object callValue : toolCalls) {

            if (!(callValue instanceof Map)) continue;

            Map<?, ?> call = (Map<?, ?>) callValue;

            Object functionValue = call.get("function");

            if (!(functionValue instanceof Map)) continue;

            Map<?, ?> function = (Map<?, ?>) functionValue;

            String name = String.valueOf(function.get("name"));

            Object argumentsValue = function.get("arguments");

            if (!(argumentsValue instanceof Map)) continue;

            Map<?, ?> arguments = (Map<?, ?>) argumentsValue;

            if ("zoom_to_location".equals(name)) {

                Object lat = arguments.get("latitude");
                Object lon = arguments.get("longitude");
                Object height = arguments.get("height");

                if (!(lat instanceof Number)
                        || !(lon instanceof Number)
                        || !(height instanceof Number)) {
                    continue;
                }

                double latitude = ((Number) lat).doubleValue();
                double longitude = ((Number) lon).doubleValue();
                double cameraHeight = ((Number) height).doubleValue();

                // Valideer de parameters
                if (latitude < -90 || latitude > 90) continue;
                if (longitude < -180 || longitude > 180) continue;
                if (cameraHeight < 1 || cameraHeight > 10000000) continue;

                actions.add(Map.of(
                    "type", "zoom_to_location",
                    "parameters", Map.of(
                        "latitude", latitude,
                        "longitude", longitude,
                        "height", cameraHeight
                    )
                ));
            }
        }

        return actions;
    }
}
