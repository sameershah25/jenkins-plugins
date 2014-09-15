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

import hudson.model.BuildListener;
import hudson.model.JobProperty;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;


public class HudsonNotificationProperty extends
        JobProperty<AbstractProject<?, ?>> {

    public final List<Endpoint> endpoints;

    @DataBoundConstructor
    public HudsonNotificationProperty(List<Endpoint> endpoints) {
        this.endpoints = new ArrayList<Endpoint>( endpoints );
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    @SuppressWarnings( "CastToConcreteClass" )
    @Override
    public HudsonNotificationPropertyDescriptor getDescriptor() {
        return (HudsonNotificationPropertyDescriptor) super.getDescriptor();
    }
    
    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        Phase.STARTED.handle(build, listener);
        return super.prebuild(build, listener);
    }
}