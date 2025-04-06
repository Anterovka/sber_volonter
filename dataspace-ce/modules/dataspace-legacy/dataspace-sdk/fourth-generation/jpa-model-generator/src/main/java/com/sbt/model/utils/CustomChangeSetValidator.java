package com.sbt.model.utils;

import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.model.utils.exception.ChangeSetAttributeIsMissingException;
import com.sbt.model.utils.exception.DataDefinitionChangesAreProhibitedException;
import com.sbt.model.utils.exception.DataManipulationChangesAreProhibitedException;
import com.sbt.model.utils.exception.RollbackTagIsMissingException;
import com.sbt.model.utils.exception.UnknownChangeException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomChangeSetValidator {

    private static final Logger LOGGER = Logger.getLogger(CustomChangeSetValidator.class.getName());

    private static String VALIDCHECKSUM_TAG = "validCheckSum";
    private static String COMMENT_TAG = "comment";


    private static String PRECONDITIONS_TAG = "preConditions";


    private static String CREATE_VIEW_CHANGE_TAG = "createView";
    private static String DROP_VIEW_CHANGE_TAG = "dropView";
    private static String CREATE_INDEX_CHANGE_TAG = "createIndex";
    private static String DROP_INDEX_CHANGE_TAG = "dropIndex";

    private static String INSERT_CHANGE_TAG = "insert";
    private static String UPDATE_CHANGE_TAG = "update";
    private static String DELETE_CHANGE_TAG = "delete";

    private static String SQL_CHANGE_TAG = "sql";


    private static String ROLLBACK_TAG = "rollback";

    private static String INSERT_SQL_REGEX = "INSERT\\s*INTO";
    private static String UPDATE_SQL_REGEX = "UPDATE.*SET";
    private static String DELETE_SQL_REGEX = "DELETE\\s*FROM";
    private static String MERGE_SQL_REGEX = "MERGE\\s*INTO";
    private static String TRUNCATE_SQL_REGEX = "TRUNCATE\\s*TABLE";

    private static String ALTER_TABLE_ADD_SQL_REGEX = "ALTER\\s*TABLE.*ADD";
    private static String ALTER_TABLE_MODIFY_ORACLE_SQL_REGEX = "ALTER\\s*TABLE.*MODIFY";
    private static String ALTER_TABLE_MODIFY_PG_SQL_REGEX = "ALTER\\s*TABLE.*ALTER\\s*COLUMN";
    private static String ALTER_TABLE_DROP_COLUMN_SQL_REGEX = "ALTER\\s*TABLE.*DROP\\s*COLUMN";
    private static String ALTER_TABLE_RENAME_COLUMN_SQL_REGEX = "ALTER\\s*TABLE.*RENAME\\s*COLUMN";
    private static String ALTER_TABLE_RENAME_TABLE_SQL_REGEX = "ALTER\\s*TABLE.*RENAME\\s*TO";

    private static String DROP_TABLE_SQL_REGEX = "DROP\\s*TABLE";


    //https://docs.liquibase.com/workflows/liquibase-community/liquibase-auto-rollback.html?Highlight=Auto%20rollback
    private static Set<String> AUTOROLLBACK_TAGS = new HashSet<>();
    private static Set<String> ALLOWED_CHANGES = new HashSet<>();

    private static Set<String> DML_REGEXES = new HashSet<>();
    private static Set<String> FORBIDDEN_DDL_REGEXES = new HashSet<>();

    private static Set<String> DATA_MANIPULATION_TAGS = new HashSet<>();

    private static final String DATA_MANIPULATION_WARNING = "Attention! Data is modified directly in the database. It requires re-initialization of the StandIn database and the replica of CAП!";
    private static final String DATA_MANIPULATION_ERROR = "Invalid changes in custom-changelog.xml, in changeSet with id=%s : data changes/deletions are not allowed!";
    private static final String DATA_DEFINITION_ERROR = "Invalid changes in custom-changelog.xml, in changeSet with id=%s : set modifications, field types and table deletions are prohibited!";


    static {
        AUTOROLLBACK_TAGS.add(CREATE_VIEW_CHANGE_TAG);
        AUTOROLLBACK_TAGS.add(CREATE_INDEX_CHANGE_TAG);

        ALLOWED_CHANGES.add(CREATE_VIEW_CHANGE_TAG);
        ALLOWED_CHANGES.add(DROP_VIEW_CHANGE_TAG);
        ALLOWED_CHANGES.add(CREATE_INDEX_CHANGE_TAG);
        ALLOWED_CHANGES.add(DROP_INDEX_CHANGE_TAG);
        ALLOWED_CHANGES.add(SQL_CHANGE_TAG);
        ALLOWED_CHANGES.add(INSERT_CHANGE_TAG);
        ALLOWED_CHANGES.add(UPDATE_CHANGE_TAG);
        ALLOWED_CHANGES.add(DELETE_CHANGE_TAG);

        DML_REGEXES.add(INSERT_SQL_REGEX);
        DML_REGEXES.add(UPDATE_SQL_REGEX);
        DML_REGEXES.add(DELETE_SQL_REGEX);
        DML_REGEXES.add(MERGE_SQL_REGEX);
        DML_REGEXES.add(TRUNCATE_SQL_REGEX);

        DATA_MANIPULATION_TAGS.add(INSERT_CHANGE_TAG);
        DATA_MANIPULATION_TAGS.add(UPDATE_CHANGE_TAG);
        DATA_MANIPULATION_TAGS.add(DELETE_CHANGE_TAG);

        FORBIDDEN_DDL_REGEXES.add(ALTER_TABLE_ADD_SQL_REGEX);
        FORBIDDEN_DDL_REGEXES.add(ALTER_TABLE_MODIFY_ORACLE_SQL_REGEX);
        FORBIDDEN_DDL_REGEXES.add(ALTER_TABLE_MODIFY_PG_SQL_REGEX);
        FORBIDDEN_DDL_REGEXES.add(ALTER_TABLE_DROP_COLUMN_SQL_REGEX);
        FORBIDDEN_DDL_REGEXES.add(ALTER_TABLE_RENAME_COLUMN_SQL_REGEX);
        FORBIDDEN_DDL_REGEXES.add(ALTER_TABLE_RENAME_TABLE_SQL_REGEX);
        FORBIDDEN_DDL_REGEXES.add(DROP_TABLE_SQL_REGEX);

    }

    private final PluginParameters pluginParameters;
    private final Node changeSetNode;


    public CustomChangeSetValidator(PluginParameters pluginParameters, Node changeSetNode) {
        this.pluginParameters = pluginParameters;
        this.changeSetNode = changeSetNode;
    }

    public void checkingValidateOfChangeSet() {

        boolean containsRollbackTag = containsTag(changeSetNode, ROLLBACK_TAG);
        String idChangeSet = getIdChangeSet(changeSetNode);

        NodeList childNodes = changeSetNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if ((childNode instanceof Element)) {
                String nameTag = childNode.getNodeName();
                if (nameTag.equalsIgnoreCase(PRECONDITIONS_TAG)) {
                    checkPrecondition(childNode);
                } else if (ALLOWED_CHANGES.contains(nameTag)) {
                    if (!AUTOROLLBACK_TAGS.contains(nameTag) && !containsRollbackTag) {
                        throw new RollbackTagIsMissingException(String.format("The <rollback> tag is missing in the change set with id=%s in custom-changelog.xml!", idChangeSet));
                    }

                    //todo: suggested moving the check of SQL and data manipulation tags before checking for rollback, need to think about it
                    if (nameTag.equalsIgnoreCase(SQL_CHANGE_TAG)) {
                        checkSqlChange(childNode);
                    }

                    if (DATA_MANIPULATION_TAGS.contains(nameTag)) {
                        checkDataManipulationChange(childNode);
                    }

                } else if (nameTag.equalsIgnoreCase(ROLLBACK_TAG)) {
                    checkRollback(childNode);
                } else {
                    throw new UnknownChangeException(String.format("Change in custom-changelog.xml, in changeSet with id=%s cannot be processed (tag <unknown> is unknown to custom-changelog validator)!", idChangeSet, nameTag));
                }
            }
        }
    }

    private void checkDataManipulationChange(Node childNode) {
        boolean enableCustomDML = pluginParameters.isEnableCustomDML();
        String idChangeSet = getIdChangeSet(changeSetNode);
        if (enableCustomDML) {
            LOGGER.warning(DATA_MANIPULATION_WARNING);
        } else {
            throw new DataManipulationChangesAreProhibitedException(String.format(DATA_MANIPULATION_ERROR, idChangeSet));
        }
        //possibly some more checks
    }

    private void checkSqlChange(Node childNode) {
        //проверка raw sql
        boolean enableCustomDML = pluginParameters.isEnableCustomDML();
        String idChangeSet = getIdChangeSet(changeSetNode);

        Node firstChild = childNode.getFirstChild();
        if (firstChild instanceof Text) {
            String data = ((Text) firstChild).getData().toUpperCase(Locale.ENGLISH);

            //todo: to add a check for служебные tables in DML
            boolean changeSetContainsDML = expressionContainsCommand(DML_REGEXES, data);
            if (changeSetContainsDML) {
                if (enableCustomDML) {
                    LOGGER.warning(DATA_MANIPULATION_WARNING);
                } else {
                    throw new DataManipulationChangesAreProhibitedException(String.format(DATA_MANIPULATION_ERROR, idChangeSet));
                }
            }

            boolean changeSetContainsForbiddenDDL = expressionContainsCommand(FORBIDDEN_DDL_REGEXES, data);
            if (changeSetContainsForbiddenDDL) {
                throw new DataDefinitionChangesAreProhibitedException(String.format(DATA_DEFINITION_ERROR, idChangeSet));
            }
        }
    }

    private boolean expressionContainsCommand(Set<String> commands, String data) {
        return commands.stream().anyMatch(sqlRegex -> {
            Pattern pattern = Pattern.compile(sqlRegex);
            Matcher matcher = pattern.matcher(data);
            return matcher.find();
        });
    }

    private void checkRollback(Node childNode) {
        //check rollback?
    }

    private void checkPrecondition(Node childNode) {
        // check precondition?
    }

    private boolean containsTag(Node changeSetNode, String nameTag) {
        NodeList childNodes = changeSetNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if ((childNode instanceof Element)) {
                String nameChildTag = childNode.getNodeName();
                if (nameTag.equalsIgnoreCase(nameChildTag)) return true;
            }
        }
        return false;
    }

    private String getIdChangeSet(Node changeSetNode) {
        int lengthAttrib = changeSetNode.getAttributes().getLength();
        for (int j = 0; j < lengthAttrib; j++) {
            Node node = changeSetNode.getAttributes().item(j);
            String nodeName = node.getNodeName();
            String nodeValue = node.getNodeValue();
            if (nodeName.equalsIgnoreCase("id")) {
                return nodeValue;
            }
        }
        throw new ChangeSetAttributeIsMissingException("The id is missing in the <changeSet> tag in custom-changelog.xml:\n" + ChangelogUtils.nodeToString(changeSetNode));
    }

}
