/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jenkins.plugins.elanceodesk.workplace.notifier;

import hudson.util.FormValidation;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class Endpoint {

    public static final Integer DEFAULT_TIMEOUT = 30000;

    private String url;

	private boolean startNotification;

	private boolean notifySuccess;

	private boolean notifyAborted;

	private boolean notifyNotBuilt;

	private boolean notifyUnstable;

	private boolean notifyFailure;

	private boolean notifyBackToNormal;

	private int timeout;
	
	@DataBoundConstructor
	public Endpoint(String url, boolean startNotification, boolean notifySuccess, boolean notifyAborted,
			boolean notifyNotBuilt, boolean notifyUnstable, boolean notifyFailure, boolean notifyBackToNormal, int timeout) {
		this.url = url;
		this.startNotification = startNotification;
		this.notifySuccess = notifySuccess;
		this.notifyBackToNormal = notifyBackToNormal;
		this.notifyFailure = notifyFailure;
		this.notifyUnstable = notifyUnstable;
		this.notifyNotBuilt = notifyNotBuilt;
		this.notifyAborted = notifyAborted;
		this.timeout = timeout;
	}
	
	
	public String getUrl() {
		return url;
	}

	public boolean isNotifySuccess() {
		return notifySuccess;
	}

	public boolean isStartNotification() {
		return startNotification;
	}

	public boolean isNotifyAborted() {
		return notifyAborted;
	}

	public boolean isNotifyNotBuilt() {
		return notifyNotBuilt;
	}

	public boolean isNotifyUnstable() {
		return notifyUnstable;
	}

	public boolean isNotifyFailure() {
		return notifyFailure;
	}

	public boolean isNotifyBackToNormal() {
		return notifyBackToNormal;
	}
	
	public int getTimeout() {
		return timeout;
	}

    public FormValidation doCheckURL(@QueryParameter(value = "url", fixEmpty = true) String url) {
        if (url.equals("111"))
            return FormValidation.ok();
        else
            return FormValidation.error("There's a problem here");
    }

    @Override
    public String toString() {
        return url;
    }
}