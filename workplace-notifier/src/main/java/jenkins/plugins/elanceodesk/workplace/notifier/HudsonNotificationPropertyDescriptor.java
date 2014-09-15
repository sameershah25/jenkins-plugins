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

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobPropertyDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Extension
public final class HudsonNotificationPropertyDescriptor extends JobPropertyDescriptor {

    public HudsonNotificationPropertyDescriptor() {
        super(HudsonNotificationProperty.class);
        load();
    }

    private List<Endpoint> endpoints = new ArrayList<Endpoint>();

    public boolean isEnabled() {
        return !endpoints.isEmpty();
    }

    public List<Endpoint> getTargets() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = new ArrayList<Endpoint>( endpoints );
    }

    @Override
    public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends Job> jobType) {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Hudson Job Notification";
    }

    public int getDefaultTimeout(){
        return Endpoint.DEFAULT_TIMEOUT;
    }

    @Override
    public HudsonNotificationProperty newInstance(StaplerRequest req, JSONObject formData) throws FormException {

        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        if (formData != null && !formData.isNullObject()) {
            JSON endpointsData = (JSON) formData.get("endpoints");
            if (endpointsData != null && !endpointsData.isEmpty()) {
                if (endpointsData.isArray()) {
                    JSONArray endpointsArrayData = (JSONArray) endpointsData;
                    endpoints.addAll(req.bindJSONToList(Endpoint.class, endpointsArrayData));
                } else {
                    JSONObject endpointsObjectData = (JSONObject) endpointsData;
                    endpoints.add(req.bindJSON(Endpoint.class, endpointsObjectData));
                }
            }
        }
        HudsonNotificationProperty notificationProperty = new HudsonNotificationProperty(endpoints);
        return notificationProperty;
    }

    public FormValidation doCheckUrl(@QueryParameter(value = "url", fixEmpty = true) String url) {
    	try {
			new URL(url);
		} catch (MalformedURLException e) {
			return FormValidation.error(e.getMessage());
		}
		return FormValidation.ok();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) {
        save();
        return true;
    }

}