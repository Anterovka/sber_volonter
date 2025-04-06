package com.sbt.model.utils;

import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.utils.exception.PdmDoesNotMatchesPdmBuildException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class PdmMatcher {
    private static final Logger LOGGER = Logger.getLogger(PdmMatcher.class.getName());

    private static Map<String, String> keyTags = new HashMap<>();
    private static Set<String> ignoredAttributes = new HashSet<>();
    private static Set<String> ignoredTags = new HashSet<>();

    private Map<String, Tag[]> allTags = new HashMap<>();

    private enum Pdms {
        FIRST, SECOND
    }

    static {
        keyTags.put("import", "type");
        keyTags.put("class", "name");
        keyTags.put("index", "index-name");
        keyTags.put("property", "name");
        keyTags.put("system-locks-classes", "class");
        keyTags.put("embedded-list", "name");
        keyTags.put("status-classes", "class");
        keyTags.put("stakeholder", "name");
        keyTags.put("status", "code");
        keyTags.put("history-version", "name");
        keyTags.put("enum", "name");
        keyTags.put("value", "name");
        keyTags.put("statuses", "class");
        keyTags.put("to", "status");

        ignoredAttributes.add("version");

        ignoredTags.add("source-models");
    }

    // anotherPdm - pdm-build.xml
    public boolean pdmMatchesAnotherPdm(File pdm, File anotherPdm) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(pdm);
            doc.normalizeDocument();
            Document anotherDoc = db.parse(anotherPdm);
            anotherDoc.normalizeDocument();

            String startTag = "PdmModel";

            NodeList nodeList = doc.getElementsByTagName(startTag);
            NodeList anotherNodeList = anotherDoc.getElementsByTagName(startTag);

            fillAllTags(nodeList, Pdms.FIRST);
            fillAllTags(anotherNodeList, Pdms.SECOND);

            return pdmMatch();

        } catch (ParserConfigurationException | SAXException
                 | IOException e) {
            throw new GeneralSdkException(e.getMessage());
        }
    }

    private boolean pdmMatch() {
        Set<Map.Entry<String, Tag[]>> entries = allTags.entrySet();
        entries.forEach(entry -> {
            Tag[] tags = entry.getValue();
            Tag pdmTag = tags[0];
            Tag pdmBuildTag = tags[1];
            if (pdmTag == null) {
                throw new PdmDoesNotMatchesPdmBuildException(String.format("The tag is not found in pdm.xml: [%s]", entry.getKey()));
            } else if (pdmBuildTag == null) {
                throw new PdmDoesNotMatchesPdmBuildException(String.format("The tag is not found in pdm-build.xml: [%s]", entry.getKey()));
            }
            attributesMatch(pdmTag, pdmBuildTag);
        });
        return true;
    }

    private boolean attributesMatch(Tag tag, Tag anotherTag) {
        Map<String, String> attributes = tag.getAttributeValues();
        Map<String, String> anotherAttributes = anotherTag.getAttributeValues();
        if (attributes.size() != anotherAttributes.size()) {
            throw new PdmDoesNotMatchesPdmBuildException(String.format("The number of attributes in the tag does not match: [%s]", tag.getKey()));
        } else {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                if (ignoredAttributes.contains(key)) {
                    continue;
                }
                String value = entry.getValue();
                String anotherValue = anotherAttributes.get(key);
                if (anotherValue == null || !anotherValue.equals(value)) {
                    throw new PdmDoesNotMatchesPdmBuildException(String.format("The attribute value [%s] of the tag [%s] does not match", key, tag.getKey()));
                }
            }
            return true;
        }
    }

    private List<Tag> fillAllTags(NodeList nodeList, Pdms pdms) {
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            String nodeName = nodeList.item(i).getNodeName();
            Tag tag = new Tag();
            tag.setNameTag(nodeList.item(i).getNodeName());
            tag.setKey(nodeName);

            if (allTags.containsKey(nodeName)) {
                Tag[] values = allTags.get(nodeName);
                if (pdms.equals(Pdms.FIRST)) {
                    values[0] = tag;
                } else {
                    values[1] = tag;
                }
                allTags.put(nodeName, values);
            } else {
                allTags.put(nodeName, pdms.equals(Pdms.FIRST) ? new Tag[]{tag, null} : new Tag[]{null, tag});
            }

            tag.setParentTag(null);
            readNodes(nodeList.item(i).getChildNodes(), tag, pdms);
            tags.add(tag);
        }
        return tags;
    }


    private void readNodes(NodeList nodeList, Tag parentTag, Pdms pdms) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {

                String nodeName = node.getNodeName();
                if (ignoredTags.contains(nodeName)) {
                    continue;
                }

                Tag tag = new Tag();
                tag.setNameTag(nodeName);
                tag.setParentTag(parentTag);
                tag.setText(node.getTextContent());

                parentTag.addChild(tag);

                String keyAttribute = keyTags.get(nodeName);
                String keyAttributeValue = "";

                if (node.hasAttributes()) {
                    NamedNodeMap attributes = node.getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attribute = attributes.item(j);
                        String attributeName = attribute.getNodeName();
                        String attributeValue = attribute.getNodeValue();
                        tag.addAttributeValue(attributeName, attributeValue);

                        if (keyAttribute != null && keyAttribute.equals(attributeName)) {
                            keyAttributeValue = attributeValue;
                        }
                    }
                }

//create a unique key for each tag - we will use it for searching
                StringBuilder sbKeyTag = new StringBuilder();
                sbKeyTag.append(StringUtils.isNoneBlank(parentTag.getKey()) ? parentTag.getKey() + '/' : "")
                    .append(nodeName)
                    .append(StringUtils.isNoneBlank(keyAttributeValue) ? String.format("/{%s}", keyAttributeValue) : "");

                tag.setKey(sbKeyTag.toString());

                if (allTags.containsKey(sbKeyTag.toString())) {
                    Tag[] values = allTags.get(sbKeyTag.toString());
                    if (pdms.equals(Pdms.FIRST)) {
                        values[0] = tag;
                    } else {
                        values[1] = tag;
                    }
                    allTags.put(sbKeyTag.toString(), values);
                } else {
                    allTags.put(sbKeyTag.toString(), pdms.equals(Pdms.FIRST) ? new Tag[]{tag, null} : new Tag[]{null, tag});
                }

                if (node.hasChildNodes()) {
                    readNodes(node.getChildNodes(), tag, pdms);
                }
            }
        }
    }

    class Tag {
        private String key;
        private String nameTag;
        private String text;
        private Map<String, String> attributeValues = new HashMap<>();
        private Tag parentTag;
        private List<Tag> childTags = new LinkedList<>();


        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getNameTag() {
            return nameTag;
        }

        public void setNameTag(String nameTag) {
            this.nameTag = nameTag;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Map<String, String> getAttributeValues() {
            return attributeValues;
        }

        public void setAttributeValues(Map<String, String> attributeValues) {
            this.attributeValues = attributeValues;
        }

        public void addAttributeValue(String name, String value) {
            this.attributeValues.put(name, value);
        }

        public Tag getParentTag() {
            return parentTag;
        }

        public void setParentTag(Tag parentTag) {
            this.parentTag = parentTag;
        }

        public List<Tag> getChildTags() {
            return childTags;
        }

        public void setChildTags(List<Tag> childTags) {
            this.childTags = childTags;
        }

        public void addChild(Tag child) {
            this.childTags.add(child);
        }

    }

}
