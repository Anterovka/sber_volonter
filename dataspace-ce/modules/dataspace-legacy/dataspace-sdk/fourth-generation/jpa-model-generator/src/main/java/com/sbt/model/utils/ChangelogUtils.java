package com.sbt.model.utils;

import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.Helper;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.utils.exception.ChangeSetAlreadyExistsInCurrentCustomChangelogException;
import com.sbt.model.utils.exception.ChangeSetAlreadyExistsInPreviousChangelogException;
import com.sbt.model.utils.exception.ChangeSetAttributeIsMissingException;
import com.sbt.model.utils.exception.NodeToStringException;
import com.sbt.model.utils.exception.ParseChangelogException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChangelogUtils {

    public static final String AUTHOR_CUSTOM_CHANGESET = "custom.changes";

    private final PluginParameters pluginParameters;
    private final Map<String, Set<String>> mapPreviousChangeSetIds = new HashMap<>();
    private final List<String> changeSetIds = new ArrayList<>();

    public ChangelogUtils(PluginParameters pluginParameters) {
        this.pluginParameters = pluginParameters;
        fillMapPreviousChangeSetIds();
    }

    private void fillMapPreviousChangeSetIds() {
        File modelDir = pluginParameters.getModel();
        File previousModelDir = null;
        boolean localDeploy = pluginParameters.isLocalDeploy();
        if (localDeploy) {
            previousModelDir = new File(modelDir, JpaConstants.userLocalModelDir());
        } else {
            previousModelDir = new File(modelDir, JpaConstants.userModelDir());
        }
        File previousChangelogFile = Helper.getFile(previousModelDir, JpaConstants.CHANGELOG_FILENAME);
        if (previousChangelogFile.exists()) {
            NodeList previousChangeSets = getChangeSets(previousChangelogFile);
            if (previousChangeSets == null) {
                return;
            }
            for (int i = 0; i < previousChangeSets.getLength(); i++) {
                Node changeSetNode = previousChangeSets.item(i);
                String author = null;
                String id = null;
                int lengthAttrib = changeSetNode.getAttributes().getLength();
                for (int j = 0; j < lengthAttrib; j++) {
                    Node node = changeSetNode.getAttributes().item(j);
                    String nodeName = node.getNodeName();
                    String nodeValue = node.getNodeValue();
                    if (nodeName.equalsIgnoreCase("author")) {
                        author = nodeValue;
                    }
                    if (nodeName.equalsIgnoreCase("id")) {
                        id = nodeValue;
                    }
                    //todo: possibly add a constraint later on AUTHOR_CUSTOM_CHANGESET.equals(author)
                    mapPreviousChangeSetIds.computeIfAbsent(author, s -> new HashSet()).add(id);
                }
            }
        }

    }

    public String getCustomChangelogText() {
        File modelDir = pluginParameters.getModel();
        File customChangelogFile = Helper.getFile(modelDir, JpaConstants.CUSTOM_CHANGELOG_FILENAME);
        if (customChangelogFile.exists()) {
            StringBuilder sbCustomChangelogText = new StringBuilder();
            NodeList changeSets = getChangeSets(customChangelogFile);
            if (changeSets == null) {
                return null;
            }
            for (int i = 0; i < changeSets.getLength(); i++) {
                Node changeSetNode = changeSets.item(i);
                addChangeSet(changeSetNode, sbCustomChangelogText);
            }
            return sbCustomChangelogText.toString();
        }
        return null;
    }

    private NodeList getChangeSets(File changelogFile) {
        try {
            //todo: add XML schema validator
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            if (pluginParameters.isCustomChangelogCDATAValidation()) {
                factory.setCoalescing(true);
            }
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource xmlSource = new InputSource(new FileReader(changelogFile));
            Document doc = builder.parse(xmlSource);
            return doc.getElementsByTagName("changeSet");
        } catch (Exception exc) {
            throw new ParseChangelogException(exc.getMessage());
        }
    }

    public static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException exc) {
            throw new NodeToStringException(exc.getMessage());
        }
        return sw.toString();
    }

    private void addChangeSet(Node changeSetNode, StringBuilder sbCustomChangelogText) {
        new CustomChangeSetValidator(pluginParameters, changeSetNode).checkingValidateOfChangeSet();
        modifyChangeSet(changeSetNode);
        sbCustomChangelogText.append("\n\t");
        sbCustomChangelogText.append(nodeToString(changeSetNode));
    }

    private void modifyChangeSet(Node changeSetNode) {
        String author = null;
        String id = null;
        int lengthAttrib = changeSetNode.getAttributes().getLength();
        for (int j = 0; j < lengthAttrib; j++) {
            Node node = changeSetNode.getAttributes().item(j);
            String nodeName = node.getNodeName();
            String nodeValue = node.getNodeValue();
            if (nodeName.equals("author")) {
                author = AUTHOR_CUSTOM_CHANGESET;
                node.setNodeValue(author);
            }
            if (nodeName.equals("id")) {
                id = nodeValue;
                if (changeSetIds.contains(id)) {
                    throw new ChangeSetAlreadyExistsInCurrentCustomChangelogException(String.format("ChangeSet with id=%s already exists in custom-changelog.xml!", id));
                }
                changeSetIds.add(id);
            }
        }
        checkIds(author, id);
    }

    private void checkIds(String author, String id) {
        if (author == null) {
            throw new ChangeSetAttributeIsMissingException("The author attribute is missing in the <changeSet> tag in custom-changelog.xml!");
        }
        if (id == null) {
            throw new ChangeSetAttributeIsMissingException("The id is missing in the <changeSet> tag in custom-changelog.xml!");
        }
        //todo: possibly add a constraint later on AUTHOR_CUSTOM_CHANGESET.equals(author)
        if (mapPreviousChangeSetIds.get(author) != null && mapPreviousChangeSetIds.get(author).contains(id)) {
            throw new ChangeSetAlreadyExistsInPreviousChangelogException(String.format("ChangeSet with author=%s and id=%s already exists in changelog.xml!", author, id));
        }
    }

}
