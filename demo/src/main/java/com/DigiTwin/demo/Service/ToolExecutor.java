
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

            // TOOL 1: Inzoomen op een locatie
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

            // TOOL 2: Een gebouw plaatsen
            else if ("place_building".equals(name)) {

                Object lat = arguments.get("latitude");
                Object lon = arguments.get("longitude");
                Object width = arguments.get("width");
                Object depth = arguments.get("depth");
                Object height = arguments.get("height");
                //Optioneel
                Object rotation = arguments.get("rotation");

                if (!(lat instanceof Number)
                        || !(lon instanceof Number)
                        || !(width instanceof Number)
                        || !(depth instanceof Number)
                        || !(height instanceof Number)) {
                    continue;
                }

                // Rotation is optioneel, standaard 0 graden.
                double buildingRotation = rotation instanceof Number
                        ? ((Number) rotation).doubleValue()
                        : 0.0;

                double latitude = ((Number) lat).doubleValue();
                double longitude = ((Number) lon).doubleValue();
                double buildingWidth = ((Number) width).doubleValue();
                double buildingDepth = ((Number) depth).doubleValue();
                double buildingHeight = ((Number) height).doubleValue();

                // Valideer geografische coördinaten
                if (latitude < -90 || latitude > 90) continue;
                if (longitude < -180 || longitude > 180) continue;

                // Valideer gebouwafmetingen
                if (buildingWidth <= 0 || buildingWidth > 200) continue;
                if (buildingDepth <= 0 || buildingDepth > 200) continue;
                if (buildingHeight <= 0 || buildingHeight > 200) continue;

                if (buildingRotation < 0 || buildingRotation >= 360) continue;

                actions.add(Map.of(
                    "type", "place_building",
                    "parameters", Map.of(
                        "latitude", latitude,
                        "longitude", longitude,
                        "width", buildingWidth,
                        "depth", buildingDepth,
                        "height", buildingHeight,
                        "rotation", buildingRotation
                    )
                ));
            }
        }

        return actions;
    }
}
