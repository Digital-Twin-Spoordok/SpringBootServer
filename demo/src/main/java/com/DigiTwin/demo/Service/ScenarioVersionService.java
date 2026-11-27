
package com.DigiTwin.demo.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.DigiTwin.demo.Model.CreateScenarioVersionRequest;
import com.DigiTwin.demo.Model.Scenario;
import com.DigiTwin.demo.Model.ScenarioObject;
import com.DigiTwin.demo.Model.ScenarioVersion;
import com.DigiTwin.demo.Repository.ScenarioRepository;
import com.DigiTwin.demo.Repository.ScenarioVersionRepository;

@Service
public class ScenarioVersionService {

    private final ScenarioVersionRepository versionRepository;
    private final ScenarioRepository scenarioRepository;

    public ScenarioVersionService(
            ScenarioVersionRepository versionRepository,
            ScenarioRepository scenarioRepository) {

        this.versionRepository = versionRepository;
        this.scenarioRepository = scenarioRepository;
    }

    // Eerste versie aanmaken bij een nieuw scenario
    public ScenarioVersion createInitialVersion(
            Scenario scenario,
            String createdBy) {

        ScenarioVersion version = new ScenarioVersion();

        version.setScenarioId(scenario.getId());
        version.setVersionNumber(1);
        version.setName("Eerste versie");
        version.setDescription("Automatisch aangemaakte eerste versie");
        version.setCreatedAt(Instant.now());
        version.setCreatedBy(createdBy);
        version.setObjects(new ArrayList<>());

        ScenarioVersion savedVersion =
                versionRepository.save(version);

        scenario.setActiveVersionId(savedVersion.getId());
        scenario.setUpdatedAt(Instant.now());

        scenarioRepository.save(scenario);

        return savedVersion;
    }

    // Alle versies ophalen
    public List<ScenarioVersion> getVersions(String scenarioId) {

        return versionRepository
                .findByScenarioIdOrderByVersionNumberDesc(scenarioId);
    }

    // Actieve versie ophalen
    public ScenarioVersion getActiveVersion(Scenario scenario) {

        if (scenario.getActiveVersionId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Dit scenario heeft nog geen actieve versie.");
        }

        return getVersion(
                scenario.getId(),
                scenario.getActiveVersionId());
    }

    // Specifieke versie ophalen
    public ScenarioVersion getVersion(
            String scenarioId,
            String versionId) {

        return versionRepository
                .findByScenarioIdAndId(scenarioId, versionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Scenarioversie niet gevonden."));
    }

    // Nieuwe versie opslaan
    public ScenarioVersion createVersion(
            Scenario scenario,
            CreateScenarioVersionRequest request,
            String createdBy) {

        if (scenario.getActiveVersionId() == null ||
                !scenario.getActiveVersionId().equals(
                        request.getExpectedActiveVersionId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Het scenario is ondertussen gewijzigd. "
                    + "Laad de nieuwste versie voordat je opslaat.");
        }

        if (request.getName() == null ||
                request.getName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Een versienaam is verplicht.");
        }

        ScenarioVersion latestVersion = versionRepository
                .findFirstByScenarioIdOrderByVersionNumberDesc(
                        scenario.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Geen bestaande scenarioversies gevonden."));

        ScenarioVersion newVersion = new ScenarioVersion();

        newVersion.setScenarioId(scenario.getId());
        newVersion.setVersionNumber(
                latestVersion.getVersionNumber() + 1);

        newVersion.setName(request.getName());
        newVersion.setDescription(request.getDescription());
        newVersion.setCreatedAt(Instant.now());
        newVersion.setCreatedBy(createdBy);

        newVersion.setObjects(
                request.getObjects() == null
                        ? new ArrayList<>()
                        : copyObjectsWithNewIds(request.getObjects()));

        ScenarioVersion savedVersion =
                versionRepository.save(newVersion);

        scenario.setActiveVersionId(savedVersion.getId());
        scenario.setUpdatedAt(Instant.now());

        scenarioRepository.save(scenario);

        return savedVersion;
    }

    // Een oude versie herstellen als nieuwe versie
    public ScenarioVersion restoreVersion(
            Scenario scenario,
            String versionId,
            String createdBy) {

        ScenarioVersion oldVersion =
                getVersion(scenario.getId(), versionId);

        return createVersionFromSnapshot(
                scenario,
                oldVersion,
                "Hersteld vanaf versie " + oldVersion.getVersionNumber(),
                "Hersteld vanaf versie " + oldVersion.getVersionNumber(),
                createdBy);
    }

    // Een bestaande versie dupliceren als nieuwe versie
    public ScenarioVersion duplicateVersion(
            Scenario scenario,
            String versionId,
            String createdBy) {

        ScenarioVersion sourceVersion =
                getVersion(scenario.getId(), versionId);

        return createVersionFromSnapshot(
                scenario,
                sourceVersion,
                sourceVersion.getName() + " (kopie)",
                "Duplicaat van versie " + sourceVersion.getVersionNumber(),
                createdBy);
    }

    // Gedeelde methode voor restore en duplicate
    private ScenarioVersion createVersionFromSnapshot(
            Scenario scenario,
            ScenarioVersion sourceVersion,
            String name,
            String description,
            String createdBy) {

        ScenarioVersion latestVersion = versionRepository
                .findFirstByScenarioIdOrderByVersionNumberDesc(
                        scenario.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Geen bestaande scenarioversies gevonden."));

        ScenarioVersion newVersion = new ScenarioVersion();

        newVersion.setScenarioId(scenario.getId());
        newVersion.setVersionNumber(
                latestVersion.getVersionNumber() + 1);

        newVersion.setName(name);
        newVersion.setDescription(description);
        newVersion.setCreatedAt(Instant.now());
        newVersion.setCreatedBy(createdBy);

        newVersion.setObjects(
                sourceVersion.getObjects() == null
                        ? new ArrayList<>()
                        : copyObjectsWithNewIds(sourceVersion.getObjects()));

        ScenarioVersion savedVersion =
                versionRepository.save(newVersion);

        scenario.setActiveVersionId(savedVersion.getId());
        scenario.setUpdatedAt(Instant.now());

        scenarioRepository.save(scenario);

        return savedVersion;
    }

    // Objecten kopiëren met nieuwe ID's
    private List<ScenarioObject> copyObjectsWithNewIds(
            List<ScenarioObject> objects) {

        List<ScenarioObject> copiedObjects = new ArrayList<>();

        for (ScenarioObject original : objects) {

            ScenarioObject copy = new ScenarioObject();

            copy.setId(UUID.randomUUID().toString());
            copy.setType(original.getType());
            copy.setName(original.getName());
            copy.setRotation(original.getRotation());

            if (original.getPosition() != null) {
                ScenarioObject.Position position =
                        new ScenarioObject.Position();

                position.setLatitude(
                        original.getPosition().getLatitude());
                position.setLongitude(
                        original.getPosition().getLongitude());
                position.setHeight(
                        original.getPosition().getHeight());

                copy.setPosition(position);
            }

            if (original.getDimensions() != null) {
                ScenarioObject.Dimensions dimensions =
                        new ScenarioObject.Dimensions();

                dimensions.setWidth(
                        original.getDimensions().getWidth());
                dimensions.setDepth(
                        original.getDimensions().getDepth());
                dimensions.setHeight(
                        original.getDimensions().getHeight());

                copy.setDimensions(dimensions);
            }

            if (original.getModel() != null) {
                ScenarioObject.ModelReference model =
                        new ScenarioObject.ModelReference();

                model.setType(original.getModel().getType());
                model.setAssetId(original.getModel().getAssetId());

                copy.setModel(model);
            }

            copy.setProperties(
                    original.getProperties() == null
                            ? new java.util.HashMap<>()
                            : new java.util.HashMap<>(
                                    original.getProperties()));

            copiedObjects.add(copy);
        }

        return copiedObjects;
    }

    // Naam en beschrijving aanpassen
    public ScenarioVersion updateVersion(
            String scenarioId,
            String versionId,
            String name,
            String description) {

        ScenarioVersion version =
                getVersion(scenarioId, versionId);

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Een versienaam is verplicht.");
        }

        version.setName(name.trim());
        version.setDescription(description);

        return versionRepository.save(version);
    }

    // Versie verwijderen
    public void deleteVersion(
            Scenario scenario,
            String versionId) {

        ScenarioVersion version =
                getVersion(scenario.getId(), versionId);

        if (version.getId().equals(scenario.getActiveVersionId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Je kunt de actieve versie niet verwijderen.");
        }

        List<ScenarioVersion> versions =
                getVersions(scenario.getId());

        if (versions.size() <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Je kunt de laatste versie niet verwijderen.");
        }

        versionRepository.delete(version);
    }
}
