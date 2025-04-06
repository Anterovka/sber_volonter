package ru.sbertech.dataspace.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dataspace.app")
class AppProperties {
    var pathConfigDirectory = "/home/user/models"
    var pathConfigModelFilePattern: String = "_active.txt"
    var pathConfigModelDir: String = "/..data/"
    var removeTemplateForModelPath: String = "/..data"
    var modelPropertiesFileSuffix: String = "properties"
    var modelPropertiesFileName: String = "context-child"
    var modelContextFullFileNameProperties: String = "$modelPropertiesFileName.$modelPropertiesFileSuffix"
    var maxTriesPropertyFileFinding = 3
    var pauseSecondsPropertyFileFinding = 1

    var pauseSecondBeforeContextOff = 20
    var shutdownRetryCount = 3
    var shutdownRetryPauseSecond = 1

    var filesystemK8sEnabled = false
    var filesystemLinuxEnabled = true

    var pdmZipped = false
    var defaultModelId = "1"
    var singleMode = false
    var singleOptionPrefix = "single"
}
