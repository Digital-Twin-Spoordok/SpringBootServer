package com.DigiTwin.demo.Tools;

import java.util.List;
import java.util.Map;

public class ToolDefinitions {

    public static List<Map<String, Object>> getTools() {

        return List.of(
            Map.of(
                "type", "function",
                "function", Map.of(
                    "name", "zoom_to_location",
                    "description",
                    "Zoom de CesiumJS kaart naar opgegeven geografische coördinaten.",

                    "parameters", Map.of(
                        "type", "object",

                        "properties", Map.of(
                            "latitude", Map.of(
                                "type", "number",
                                "description", "Breedtegraad (-90 tot 90)"
                            ),

                            "longitude", Map.of(
                                "type", "number",
                                "description", "Lengtegraad (-180 tot 180)"
                            ),

                            "height", Map.of(
                                "type", "number",
                                "description", "Camera hoogte in meters"
                            )
                        ),

                        "required", List.of(
                            "latitude",
                            "longitude",
                            "height"
                        )
                    )
                )
            )
        );
    }
}

