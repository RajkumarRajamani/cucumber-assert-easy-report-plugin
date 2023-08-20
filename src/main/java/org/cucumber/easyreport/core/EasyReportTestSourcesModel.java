//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.cucumber.easyreport.core;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.Examples;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.FeatureChild;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.RuleChild;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Source;
import io.cucumber.messages.types.SourceMediaType;
import io.cucumber.messages.types.Step;
import io.cucumber.messages.types.TableRow;
import io.cucumber.plugin.event.TestSourceRead;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

final class EasyReportTestSourcesModel {
    private final Map<URI, TestSourceRead> pathToReadEventMap = new HashMap();
    private final Map<URI, GherkinDocument> pathToAstMap = new HashMap();
    private final Map<URI, Map<Long, AstNode>> pathToNodeMap = new HashMap();

    EasyReportTestSourcesModel() {
    }

    static Scenario getScenarioDefinition(AstNode astNode) {
        AstNode candidate;
        for(candidate = astNode; candidate != null && !(candidate.node instanceof Scenario); candidate = candidate.parent) {
        }

        return candidate == null ? null : (Scenario)candidate.node;
    }

    static boolean isBackgroundStep(AstNode astNode) {
        return astNode.parent.node instanceof Background;
    }

    static String calculateId(AstNode astNode) {
        Object node = astNode.node;
        if (node instanceof Scenario) {
            return calculateId(astNode.parent) + ";" + convertToId(((Scenario)node).getName());
        } else if (node instanceof ExamplesRowWrapperNode) {
            return calculateId(astNode.parent) + ";" + (((ExamplesRowWrapperNode)node).bodyRowIndex + 2);
        } else if (node instanceof TableRow) {
            return calculateId(astNode.parent) + ";" + 1;
        } else if (node instanceof Examples) {
            return calculateId(astNode.parent) + ";" + convertToId(((Examples)node).getName());
        } else {
            return node instanceof Feature ? convertToId(((Feature)node).getName()) : "";
        }
    }

    static String convertToId(String name) {
        return name.replaceAll("[\\s'_,!]", "-").toLowerCase();
    }

    static URI relativize(URI uri) {
        if (!"file".equals(uri.getScheme())) {
            return uri;
        } else if (!uri.isAbsolute()) {
            return uri;
        } else {
            try {
                URI root = (new File("")).toURI();
                URI relative = root.relativize(uri);
                return new URI("file", relative.getSchemeSpecificPart(), relative.getFragment());
            } catch (URISyntaxException var3) {
                throw new IllegalArgumentException(var3.getMessage(), var3);
            }
        }
    }

    void addTestSourceReadEvent(URI path, TestSourceRead event) {
        this.pathToReadEventMap.put(path, event);
    }

    Feature getFeature(URI path) {
        if (!this.pathToAstMap.containsKey(path)) {
            this.parseGherkinSource(path);
        }

        return this.pathToAstMap.containsKey(path) ? (Feature)((GherkinDocument)this.pathToAstMap.get(path)).getFeature().orElse((Feature) null) : null;
    }

    private void parseGherkinSource(URI path) {
        if (this.pathToReadEventMap.containsKey(path)) {
            String source = ((TestSourceRead)this.pathToReadEventMap.get(path)).getSource();
            GherkinParser parser = GherkinParser.builder().build();
            Stream<Envelope> envelopes = parser.parse(Envelope.of(new Source(path.toString(), source, SourceMediaType.TEXT_X_CUCUMBER_GHERKIN_PLAIN)));
            GherkinDocument gherkinDocument = (GherkinDocument)envelopes.map(Envelope::getGherkinDocument).filter(Optional::isPresent).map(Optional::get).findFirst().orElse((GherkinDocument) null);
            this.pathToAstMap.put(path, gherkinDocument);
            Map<Long, AstNode> nodeMap = new HashMap();
            Feature feature = (Feature)gherkinDocument.getFeature().get();
            AstNode currentParent = new AstNode(feature, (AstNode)null);
            Iterator var9 = feature.getChildren().iterator();

            while(var9.hasNext()) {
                FeatureChild child = (FeatureChild)var9.next();
                this.processFeatureDefinition(nodeMap, child, currentParent);
            }

            this.pathToNodeMap.put(path, nodeMap);
        }
    }

    private void processFeatureDefinition(Map<Long, AstNode> nodeMap, FeatureChild child, AstNode currentParent) {
        child.getBackground().ifPresent((background) -> {
            this.processBackgroundDefinition(nodeMap, background, currentParent);
        });
        child.getScenario().ifPresent((scenario) -> {
            this.processScenarioDefinition(nodeMap, scenario, currentParent);
        });
        child.getRule().ifPresent((rule) -> {
            AstNode childNode = new AstNode(rule, currentParent);
            nodeMap.put(rule.getLocation().getLine(), childNode);
            rule.getChildren().forEach((ruleChild) -> {
                this.processRuleDefinition(nodeMap, ruleChild, childNode);
            });
        });
    }

    private void processBackgroundDefinition(Map<Long, AstNode> nodeMap, Background background, AstNode currentParent) {
        AstNode childNode = new AstNode(background, currentParent);
        nodeMap.put(background.getLocation().getLine(), childNode);
        Iterator var5 = background.getSteps().iterator();

        while(var5.hasNext()) {
            Step step = (Step)var5.next();
            nodeMap.put(step.getLocation().getLine(), new AstNode(step, childNode));
        }

    }

    private void processScenarioDefinition(Map<Long, AstNode> nodeMap, Scenario child, AstNode currentParent) {
        AstNode childNode = new AstNode(child, currentParent);
        nodeMap.put(child.getLocation().getLine(), childNode);
        Iterator var5 = child.getSteps().iterator();

        while(var5.hasNext()) {
            Step step = (Step)var5.next();
            nodeMap.put(step.getLocation().getLine(), new AstNode(step, childNode));
        }

        if (!child.getExamples().isEmpty()) {
            this.processScenarioOutlineExamples(nodeMap, child, childNode);
        }

    }

    private void processRuleDefinition(Map<Long, AstNode> nodeMap, RuleChild child, AstNode currentParent) {
        child.getBackground().ifPresent((background) -> {
            this.processBackgroundDefinition(nodeMap, background, currentParent);
        });
        child.getScenario().ifPresent((scenario) -> {
            this.processScenarioDefinition(nodeMap, scenario, currentParent);
        });
    }

    private void processScenarioOutlineExamples(Map<Long, AstNode> nodeMap, Scenario scenarioOutline, AstNode parent) {
        Iterator var4 = scenarioOutline.getExamples().iterator();

        while(var4.hasNext()) {
            Examples examples = (Examples)var4.next();
            AstNode examplesNode = new AstNode(examples, parent);
            TableRow headerRow = (TableRow)examples.getTableHeader().get();
            AstNode headerNode = new AstNode(headerRow, examplesNode);
            nodeMap.put(headerRow.getLocation().getLine(), headerNode);

            for(int i = 0; i < examples.getTableBody().size(); ++i) {
                TableRow examplesRow = (TableRow)examples.getTableBody().get(i);
                Object rowNode = new ExamplesRowWrapperNode(examplesRow, i);
                AstNode expandedScenarioNode = new AstNode(rowNode, examplesNode);
                nodeMap.put(examplesRow.getLocation().getLine(), expandedScenarioNode);
            }
        }

    }

    AstNode getAstNode(URI path, int line) {
        if (!this.pathToNodeMap.containsKey(path)) {
            this.parseGherkinSource(path);
        }

        return this.pathToNodeMap.containsKey(path) ? (AstNode)((Map)this.pathToNodeMap.get(path)).get((long)line) : null;
    }

    boolean hasBackground(URI path, int line) {
        if (!this.pathToNodeMap.containsKey(path)) {
            this.parseGherkinSource(path);
        }

        if (this.pathToNodeMap.containsKey(path)) {
            AstNode astNode = (AstNode)((Map)this.pathToNodeMap.get(path)).get((long)line);
            return getBackgroundForTestCase(astNode).isPresent();
        } else {
            return false;
        }
    }

    static Optional<Background> getBackgroundForTestCase(AstNode astNode) {
        Feature feature = getFeatureForTestCase(astNode);
        return feature.getChildren().stream().map(FeatureChild::getBackground).filter(Optional::isPresent).map(Optional::get).findFirst();
    }

    private static Feature getFeatureForTestCase(AstNode astNode) {
        while(astNode.parent != null) {
            astNode = astNode.parent;
        }

        return (Feature)astNode.node;
    }

    static class AstNode {
        final Object node;
        final AstNode parent;

        AstNode(Object node, AstNode parent) {
            this.node = node;
            this.parent = parent;
        }
    }

    static class ExamplesRowWrapperNode {
        final int bodyRowIndex;

        ExamplesRowWrapperNode(Object examplesRow, int bodyRowIndex) {
            this.bodyRowIndex = bodyRowIndex;
        }
    }
}
