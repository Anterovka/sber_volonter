package com.sbt.model.utils;

public class ChangelogGeneratorChangelogCorrector {
    private static final String CLOSING_ANGLE_BRACKET = ">";

    private static final String SET_COLUMN_REMARKS = "<setColumnRemarks";
    private static final String SET_TABLE_REMARKS = "<setTableRemarks";
    private static final String MODIFY_DATA_TYPE = "<modifyDataType";
    private static final String PRECONDITIONS = "<preConditions";

    private static final String COLUMN_NAME = "columnName=\"";
    private static final String TABLE_NAME = "tableName=\"";


    public String rebuildChangelogText(String changelogText, int indexChangeSetOpen, int indexChangeSetClosed){

        while(true) {
            String changeSetText = changelogText.substring(indexChangeSetOpen, indexChangeSetClosed);

            int offset = 0;

            if (changeSetText.contains(SET_COLUMN_REMARKS)
                    || changeSetText.contains(SET_TABLE_REMARKS)
                    || changeSetText.contains(MODIFY_DATA_TYPE)) {

                String newChangeSetText = changeChangeSetText(changeSetText);
                offset = newChangeSetText.length() - changeSetText.length();
                changelogText = changelogText.replace(changeSetText, newChangeSetText);
            }
            indexChangeSetOpen = changelogText.indexOf("<changeSet", indexChangeSetClosed + offset);
            if (indexChangeSetOpen > 0) {
                indexChangeSetClosed = changelogText.indexOf("</changeSet", indexChangeSetOpen);
            } else {
                break;
            }
        }
        return changelogText;
    }

    private String changeChangeSetText(String changeSetText) {
        if (changeSetText.contains(PRECONDITIONS)) {
            return changeSetText;
        }

        if (changeSetText.contains(SET_COLUMN_REMARKS)) {
            //here offset = 0, as we want to get the entire string of the change tag
            String changeText = getIncludeSubstring(changeSetText, SET_COLUMN_REMARKS, CLOSING_ANGLE_BRACKET);
            // here we need the contents of the attribute already
            String columnName = getSubstring(changeText, COLUMN_NAME, "\"", COLUMN_NAME.length());
            // here we already need the contents of the attribute
            String tableName = getSubstring(changeText, TABLE_NAME, "\"", TABLE_NAME.length());

            String preconditionForColumnRemark = getPreconditionForColumnRemark(tableName, columnName);

            int indexChangeOpen = changeSetText.indexOf(SET_COLUMN_REMARKS);

            StringBuilder sbChangeSetText = new StringBuilder(changeSetText);
            sbChangeSetText.insert(indexChangeOpen, preconditionForColumnRemark);

            changeSetText = sbChangeSetText.toString();

            return changeSetText;

        } else if (changeSetText.contains(SET_TABLE_REMARKS)) {
            String changeText = getIncludeSubstring(changeSetText, SET_TABLE_REMARKS, CLOSING_ANGLE_BRACKET);
            String tableName = getSubstring(changeText, TABLE_NAME, "\"", TABLE_NAME.length());

            String preconditionForTableRemark = getPreconditionForTableRemark(tableName);

            int indexChangeOpen = changeSetText.indexOf(SET_TABLE_REMARKS);

            StringBuilder sbChangeSetText = new StringBuilder(changeSetText);
            sbChangeSetText.insert(indexChangeOpen, preconditionForTableRemark);

            changeSetText = sbChangeSetText.toString();

            return changeSetText;

        } else if (changeSetText.contains(MODIFY_DATA_TYPE)) {

            String changeText = getIncludeSubstring(changeSetText, MODIFY_DATA_TYPE, CLOSING_ANGLE_BRACKET);
            String columnName = getSubstring(changeText, COLUMN_NAME, "\"", COLUMN_NAME.length());
            String tableName = getSubstring(changeText, TABLE_NAME, "\"", TABLE_NAME.length());

            String preconditionForModifyDataType = getPreconditionForModifyDataType(tableName, columnName);

            int indexChangeOpen = changeSetText.indexOf(MODIFY_DATA_TYPE);

            StringBuilder sbChangeSetText = new StringBuilder(changeSetText);
            sbChangeSetText.insert(indexChangeOpen, preconditionForModifyDataType);

            changeSetText = sbChangeSetText.toString();

            return changeSetText;

        }
        return changeSetText;
    }

    private String getIncludeSubstring(String changeSetText, String sequenceOpen, String sequenceClosed) {
        return getSubstring(changeSetText, sequenceOpen, sequenceClosed, 0);
    }

    private String getSubstring(String str, String sequenceOpen, String sequenceClosed, int offset) {
        int indexOpen = str.indexOf(sequenceOpen) + offset;
        int indexClosed = str.indexOf(sequenceClosed, indexOpen);
        return str.substring(indexOpen, indexClosed);
    }


    private String getPreconditionForTableRemark(String tableName) {
        return String.format("<preConditions onFail=\"MARK_RAN\">\n" +
                "            <tableExists tableName=\"%s\"/>\n" +
                "        </preConditions>\n\t\t", tableName);
    }

    private String getPreconditionForColumnRemark(String tableName, String columnName) {
        return String.format("<preConditions onFail=\"MARK_RAN\">\n" +
                "            <columnExists tableName=\"%s\" columnName=\"%s\"/>\n" +
                "        </preConditions>\n\t\t", tableName, columnName);
    }

    private String getPreconditionForModifyDataType(String tableName, String columnName) {
        return String.format("<preConditions onFail=\"MARK_RAN\">\n" +
                "            <columnExists tableName=\"%s\" columnName=\"%s\"/>\n" +
                "        </preConditions>\n\t\t", tableName, columnName);
    }
}
