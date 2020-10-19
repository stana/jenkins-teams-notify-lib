def call(String webhookGuid, String buildStatus, Map kwargs) {
    //
    // required - webhookGuid and buildStatus
    // optional - kwargs.title, kwargs.message, kwargs.color, kwargs.summary, kwargs.envName
    //
    def env_name = kwargs.envName ?: env.ENV_NAME
    if (env_name) {
        env_name = env_name.toUpperCase()
    }
    def title = kwargs.title ?: env.JOB_NAME
    if (env_name) {
        // prefix title with env name
        title = "${env_name} ${title}"
    }

    def message = kwargs.message
    def color = kwargs.color
    def summary = kwargs.summary

    // some default colors if not provided
    def good_color = "#6264A7"
    def bad_color = "#CC4A31"
    //def resp

    if (buildStatus == "STARTED") {
        if (!message) { message = "${env_name} job ${env.JOB_NAME}, build ${env.BUILD_NUMBER} started" }
        if (!summary) { summary = message }
        if (!color) { color = good_color }
        //resp = sendNotification webhookGuid, title, color, message, summary
        sendNotification webhookGuid, title, color, message, summary
    }
    else if (buildStatus == "SUCCESS") {
        if (!message) { message = "${env_name} job ${env.JOB_NAME}, build ${env.BUILD_NUMBER} successful" }
        if (!summary) { summary = message }
        if (!color) { color = good_color }
        //resp = sendNotification webhookGuid, title, color, message, summary 
        sendNotification webhookGuid, title, color, message, summary 
    }
    else if (buildStatus == "FAILURE" ) {
        if (!message) { message = "${env_name} job ${env.JOB_NAME}, build ${env.BUILD_NUMBER} failure" }
        if (!summary) { summary = message }
        if (!color) { color = bad_color }
        //resp = sendNotification webhookGuid, title, color, message, summary 
        sendNotification webhookGuid, title, color, message, summary 
    }
    else if (buildStatus == "UNSTABLE" ) {
        if (!message) { message = "${env_name} job ${env.JOB_NAME}, build ${env.BUILD_NUMBER} unstable" }
        if (!summary) { summary = message }
        if (!color) { color = bad_color }
        //resp = sendNotification webhookGuid, title, color, message, summary 
        sendNotification webhookGuid, title, color, message, summary 
    }
    else {
        if (!message) { message = "${env_name} job ${env.JOB_NAME}, build ${env.BUILD_NUMBER} result uknown" }
        if (!summary) { summary = message }
        if (!color) { color = bad_color }
        //resp = sendNotification webhookGuid, title, color, message, summary 
        sendNotification webhookGuid, title, color, message, summary 
    }
    //return null
}
