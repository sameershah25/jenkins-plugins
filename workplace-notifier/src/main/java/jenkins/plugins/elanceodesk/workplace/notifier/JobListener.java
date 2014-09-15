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
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.listeners.RunListener;

@Extension
@SuppressWarnings("rawtypes")
public class JobListener extends RunListener<AbstractBuild> {

    public JobListener() {
        super(AbstractBuild.class);
    }

    @Override
    public void onStarted(AbstractBuild r, TaskListener listener) {
        Phase.STARTED.handle(r, listener);
    }

    @Override
    public void onCompleted(AbstractBuild r, TaskListener listener) {
        Phase.COMPLETED.handle(r, listener);
    }

    @Override
    public void onFinalized(AbstractBuild r) {
        Phase.FINALIZED.handle(r, TaskListener.NULL);
    }

}