
package com.DigiTwin.demo.Tools;

import java.util.List;
import java.util.Map;

public class ToolDefinitions {

    public static List<Map<String, Object>> getTools() {

        return List.of(
            // Tool 1: Een gebouw plaatsen
            Map.of(
                "type", "function",
                "function", Map.of(
                    "name", "place_building",
                    "description",
                    "Plaats één gebouw op de opgegeven geografische locatie in CesiumJS. " +
                    "Gebruik latitude als breedtegraad en longitude als lengtegraad. " +
                    "Latitude en longitude moeten decimale graden zijn. " +
                    "Voor Spoordok moeten gebouwen binnen de opgegeven Spoordok-polygon worden geplaatst. " +
                    "Gebruik de polygon-hoekpunten niet als gebouwlocaties. " +
                    "Bij meerdere gebouwen moet ieder gebouw een eigen locatie krijgen. " +
                    "Plaats gebouwen niet op dezelfde of vrijwel dezelfde coördinaten. " +
                    "De breedte, diepte en hoogte zijn in meters. " +
                    "Rotation is de rotatie in graden.",

                    "parameters", Map.of(
                        "type", "object",

                        "properties", Map.of(
                            "latitude", Map.of(
                                "type", "number",
                                "description", "Breedtegraad van de locatie (-90 tot 90)"
                            ),

                            "longitude", Map.of(
                                "type", "number",
                                "description", "Lengtegraad van de locatie (-180 tot 180)"
                            ),

                            "width", Map.of(
                                "type", "number",
                                "description", "Breedte van het gebouw in meters"
                            ),

                            "depth", Map.of(
                                "type", "number",
                                "description", "Diepte van het gebouw in meters"
                            ),

                            "height", Map.of(
                                "type", "number",
                                "description", "Hoogte van het gebouw in meters"
                            ),
                            "rotation", Map.of(
                                "type", "number",
                                "description", "Rotatie van een gebouw"
                            )
                        ),

                        "required", List.of(
                            "latitude",
                            "longitude",
                            "width",
                            "depth",
                            "height"
                        )
                    )
                )
            )
        );
    }
}
