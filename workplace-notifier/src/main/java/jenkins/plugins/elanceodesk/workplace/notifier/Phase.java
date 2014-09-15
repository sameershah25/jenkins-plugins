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

import hudson.EnvVars;
import hudson.model.ParameterValue;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.Job;
import hudson.model.ParametersAction;
import hudson.model.Run;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jenkins.model.Jenkins;
import jenkins.plugins.elanceodesk.workplace.notifier.model.BuildState;
import jenkins.plugins.elanceodesk.workplace.notifier.model.Changeset;
import jenkins.plugins.elanceodesk.workplace.notifier.model.JobState;
import jenkins.plugins.elanceodesk.workplace.notifier.model.ScmState;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



@SuppressWarnings({ "unchecked", "rawtypes" })
public enum Phase {
    STARTED, COMPLETED, FINALIZED;
    
    private final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    
    private ExecutorService executorService = Executors.newCachedThreadPool();
    
    @SuppressWarnings( "CastToConcreteClass" )
    public void handle(AbstractBuild build, TaskListener listener) {

        HudsonNotificationProperty property = (HudsonNotificationProperty) build.getParent().getProperty(HudsonNotificationProperty.class);
        if ( property == null ){ return; }

        for ( Endpoint target : property.getEndpoints()) {
            if ( isRun( target, build )) {
                listener.getLogger().println( String.format( "Notifying endpoint '%s'", target ));

                try {
                	listener.getLogger().println( String.format( "Notifying endpoint '%s'", target ));

                    try {
                    	JobState jobState = buildJobState(build.getParent(), build, listener);
                    	HttpWorker worker = new HttpWorker(target.getUrl(), gson.toJson(jobState), 30000, listener.getLogger());
                    	executorService.submit(worker);
                    } catch (Throwable error) {
                        error.printStackTrace( listener.error( String.format( "Failed to notify endpoint '%s'", target )));
                        listener.getLogger().println( String.format( "Failed to notify endpoint '%s' - %s: %s",
                                                                     target, error.getClass().getName(), error.getMessage()));
                    }

                } catch (Throwable error) {
                    error.printStackTrace( listener.error( String.format( "Failed to notify endpoint '%s'", target )));
                    listener.getLogger().println( String.format( "Failed to notify endpoint '%s' - %s: %s",
                                                                 target, error.getClass().getName(), error.getMessage()));
                }
            }
        }
    }

    /*
    * Determines if the endpoint specified should be notified at the current job phase.
    */
   private boolean isRun( Endpoint endpoint, AbstractBuild build ) {
   	if(this.equals(STARTED) && endpoint.isStartNotification()) {
   		return true;
   	} else if(this.equals(COMPLETED)) {
   		Result result = build.getResult();
           Run<?, ?> previousBuild = (AbstractBuild<?, ?>) build.getPreviousBuild();
           Result previousResult = (previousBuild != null) ? previousBuild.getResult() : Result.SUCCESS;
           return ((result == Result.ABORTED && endpoint.isNotifyAborted())
                   || (result == Result.FAILURE && endpoint.isNotifyFailure())
                   || (result == Result.NOT_BUILT && endpoint.isNotifyNotBuilt())
                   || (result == Result.SUCCESS && previousResult == Result.FAILURE && endpoint.isNotifyBackToNormal())
                   || (result == Result.SUCCESS && endpoint.isNotifySuccess())
                   || (result == Result.UNSTABLE && endpoint.isNotifyUnstable()));
       
           
   	} else {
   		return false;
   	}
   }

    private JobState buildJobState(Job job, AbstractBuild run, TaskListener listener)
        throws IOException, InterruptedException
    {

        Jenkins            jenkins      = Jenkins.getInstance();
        String             rootUrl      = jenkins.getRootUrl();
        JobState           jobState     = new JobState();
        BuildState         buildState   = new BuildState();
        ScmState           scmState     = new ScmState();
        Result             result       = run.getResult();
        ParametersAction   paramsAction = run.getAction(ParametersAction.class);
        EnvVars            environment  = run.getEnvironment( listener );

        jobState.setName( job.getName());
        jobState.setUrl( job.getUrl());
        jobState.setBuild( buildState );

        buildState.setNumber( run.number );
        buildState.setUrl( run.getUrl());
        buildState.setPhase( this );
        buildState.setScm( scmState );

        if ( result != null ) {
            buildState.setStatus(result.toString());
        }

        if ( rootUrl != null ) {
            buildState.setFullUrl(rootUrl + run.getUrl());
        }

        buildState.updateArtifacts( job, run );

        if ( paramsAction != null ) {
            EnvVars env = new EnvVars();
            for (ParameterValue value : paramsAction.getParameters()){
                if ( ! value.isSensitive()) {
                    value.buildEnvironment( run, env );
                }
            }
            buildState.setParameters(env);
        }

        if ( environment.get( "GIT_URL" ) != null ) {
            scmState.setUrl( environment.get( "GIT_URL" ));
        }

        if ( environment.get( "GIT_BRANCH" ) != null ) {
            scmState.setBranch( environment.get( "GIT_BRANCH" ));
        }

        if ( environment.get( "GIT_COMMIT" ) != null ) {
            scmState.setCommit( environment.get( "GIT_COMMIT" ));
        }
        
        if(this.equals(STARTED)) {
	        ChangeLogSet changeLogSet = run.getChangeSet();
	        List<Changeset> changesets = new ArrayList<Changeset>();
	        for (Object o : changeLogSet.getItems()) {
	        	
	            Entry entry = (Entry) o;
	            listener.getLogger().println("Entry " + o);
	            Changeset changeset = new Changeset(entry.getAuthor().getDisplayName(), entry.getAffectedFiles());
	            changesets.add(changeset);
	        }
	        buildState.setChangeSet(changesets);
        }     
        return jobState;

    }
}