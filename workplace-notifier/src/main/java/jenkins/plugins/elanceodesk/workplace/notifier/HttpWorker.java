package jenkins.plugins.elanceodesk.workplace.notifier;

import hudson.ProxyConfiguration;

import java.io.PrintStream;

import jenkins.model.Jenkins;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class HttpWorker implements Runnable {

	private PrintStream logger;

	private String url;

	private String data;

	private int timeout;

	public HttpWorker(String url, String data, int timeout, PrintStream logger) {
		this.url = url;
		this.data = data;
		this.timeout = timeout;
		this.logger = logger;
	}

	public void run() {
		RequestEntity requestEntity;
		try {
			logger.println(String.format("Posting data to webhook - %s", url));
			requestEntity = new StringRequestEntity(data,"application/json", "UTF-8");
			HttpClient client = getHttpClient();
			client.getParams().setConnectionManagerTimeout(timeout);;
	        PostMethod post = new PostMethod(url);
	        post.setRequestEntity(requestEntity);
	        int responseCode = client.executeMethod(post);
	        if(responseCode != HttpStatus.SC_OK) {
	        	String response = post.getResponseBodyAsString();
	        	logger.println(String.format("Posting data to - %s may have failed. Webhook responded with status code - %s", url, responseCode));
	        	logger.println(String.format("Message from webhook - %s", response));
	        	
	        } else {
	        	logger.println(String.format("Posting data to webhook - %s completed ", url));
	        }
		} catch (Exception e) {
			logger.println(String.format("Failed to post data to webhook - %s", url));
			e.printStackTrace(logger);
		}
		
	}

	private HttpClient getHttpClient() {
		HttpClient client = new HttpClient();
		if (Jenkins.getInstance() != null) {
			ProxyConfiguration proxy = Jenkins.getInstance().proxy;
			if (proxy != null) {
				client.getHostConfiguration().setProxy(proxy.name, proxy.port);
				String username = proxy.getUserName();
				String password = proxy.getPassword();
				// Consider it to be passed if username specified. Sufficient?
				if (username != null && !"".equals(username.trim())) {
					logger.println("Using proxy authentication (user=" + username + ")");
					// http://hc.apache.org/httpclient-3.x/authentication.html#Proxy_Authentication
					// and
					// http://svn.apache.org/viewvc/httpcomponents/oac.hc3x/trunk/src/examples/BasicAuthenticationExample.java?view=markup
					client.getState().setProxyCredentials(AuthScope.ANY,
							new UsernamePasswordCredentials(username, password));
				}
			}
		}
		return client;
	}
}
