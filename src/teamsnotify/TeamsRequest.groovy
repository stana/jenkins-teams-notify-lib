package teamsnotify

import java.net.URLEncoder
//import groovy.json.JsonSlurper
import hudson.AbortException

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.GeneralSecurityException;

import java.net.URL;
import java.net.URLConnection;
import java.net.Proxy;
import java.net.InetSocketAddress;

class TeamsRequest implements Serializable {
    def env
    def script
    def baseUrl
    def httpProxy

    TeamsRequest(script, env, teamsGuid) {
        this.script = script
        this.env = env
        // Teams webhook format -  
        //     https://outlook.office.com/webhook/{guid}/IncomingWebhook/{webhook_guid}
        if (!teamsGuid) {
            teamsGuid = env.CHAT_ID
        }
        assert teamsGuid: "teamsGuid not set"
        this.baseUrl = "https://outlook.office.com/webhook/${teamsGuid}"
        this.httpProxy = env.http_proxy ?: env.HTTP_PROXY
    }

    private def getWebhookUrl(String webhookGuid) {
        if (!webhookGuid) {
            // channel/webhook id
            webhookGuid = env.CHATROOM
        }
        return  "${this.baseUrl}/IncomingWebhook/${webhookGuid}"
    }

    //private Map parseJson(String txt) {
    private def parseJson(String txt) {
        this.script.println("parseJson start...")
        // JsonSlurper returns non-serializable LazyMap. Convert it to HashMap
        // see http://stackoverflow.com/a/38899227/400222
        final slurper = new groovy.json.JsonSlurper()
        //return new HashMap<>(slurper.parseText(txt))
        def result = slurper.parseText(txt)
        this.script.println("parseJson end...")
        return result
    }

    private def getProxy() {
        def proxy
        def proxy_url
        if (this.httpProxy) {
            this.script.println("Setting proxy obj from ${this.httpProxy}")
            proxy_url = new URL(httpProxy)
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy_url.getHost(), proxy_url.getPort()));
        }
        return proxy
    }

    private def sendRequest(String url, String payload) {
        //def resp_content
        //def resp_map

        this.script.println("Sending request to - ${url}")
        this.script.println("Request message - ${payload}")
        // get proxy obj if needed
        def proxy = this.getProxy()
        def conn
        if (proxy) {
            conn = new URL(url).openConnection(proxy)
        }
        else {
            conn = new URL(url).openConnection()
        }
        conn.setConnectTimeout(6000) //6sec
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Accept", "application/json")

        // POST request
        conn.setRequestMethod("POST")
        conn.setDoOutput(true)
        conn.getOutputStream().write(payload.getBytes("UTF-8"))

        def resp_code = conn.getResponseCode()
        if (resp_code.equals(200)) {
            //resp_content = conn.getInputStream().getText()
            //def resp_content_pretty = groovy.json.JsonOutput.prettyPrint(resp_content)
            //this.script.println("Response - ${resp_content_pretty}")
            //resp_map = parseJson(resp_content)
            this.script.println("Request sent successfully")
        }
        else {
            this.script.println("Http Response Code - ${resp_code}")
        }
        //return resp_map
    }

    private def sendNotification(String webhookGuid, String title, String themeColor, String text, String summary) {
        def payload = '{"title":"'+title+'", "themeColor":"'+themeColor+'", "text":"'+text+'", "summary":"'+summary+'"}'
        def webhook_url = this.getWebhookUrl(webhookGuid)
        //def res = this.sendRequest(webhook_url, payload)
        //return res
        this.sendRequest(webhook_url, payload)
    }
}
