/* Copyright 2015 TruSolve, LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.trusolve.atlassian.bamboo.plugins.scriptengine;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.bamboo.agent.AgentType;
import com.atlassian.bamboo.build.CustomBuildProcessorServer;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.agent.BuildAgent;


public class ScriptEngineBuildProcessorServer
	extends ScriptEngineCore
	implements CustomBuildProcessorServer
{
	private static final Logger log = LoggerFactory.getLogger(ScriptEngineBuildProcessorServer.class);
	
	private BuildContext buildContext = null;

	@SuppressWarnings("deprecation")
	@Override
	public BuildContext call() throws InterruptedException, Exception
	{
		log.debug("Entering build processor server.");
		
		// TODO: This needs to be fixed properly after checking usecases
		// Add null checks to fail safely
		if (resultsSummaryManager == null || agentManager == null || buildLoggerManager == null) {
			log.error("ResultsSummaryManager or AgentManager or BuildLoggerManager is null. Skipping script execution.");
			return buildContext;
		}
		
		ResultsSummary resultsSummary = resultsSummaryManager.getResultsSummary(buildContext.getPlanResultKey());
		BuildAgent buildAgent = agentManager.getAgent(resultsSummary.getBuildAgentId());
		BuildLogger buildLogger = buildLoggerManager.getLogger(buildContext.getPlanResultKey());
		if( buildAgent != null && ! AgentType.LOCAL.equals(buildAgent.getType()))
		{
			for(TaskDefinition taskDefinition : buildContext.getBuildDefinition().getTaskDefinitions() )
			{
				if( taskDefinition.isEnabled() && ScriptEngineConstants.PLUGIN_KEY.equals(taskDefinition.getPluginKey()))
				{
					configurationInit(taskDefinition.getConfiguration());
					
					if( scriptRunOnServer != null && "true".equalsIgnoreCase(scriptRunOnServer) )
					{
						SimpleScriptContext ssc = new SimpleScriptContext();
						ssc.setAttribute("buildContext", buildContext, ScriptContext.ENGINE_SCOPE);
						ssc.setAttribute("buildLogger", buildLogger, ScriptContext.ENGINE_SCOPE);
	
						try
						{
							if ("FILE".equals(scriptLocation))
							{
								this.executeScript(scriptFile, scriptLanguage, ssc, true);
							}
							else
							{
								this.executeScript(scriptBody, scriptLanguage, ssc, false);
							}
						}
						catch (Exception e)
						{
							buildContext.getCurrentResult().setBuildState(BuildState.FAILED);
							buildLogger.addErrorLogEntry("Script Execution Failed.",e);
						}
					}
				}
			}
		}
		return buildContext;
	}

	@Override
	public void init(BuildContext buildContext)
	{
		this.buildContext = buildContext;
	}

}
