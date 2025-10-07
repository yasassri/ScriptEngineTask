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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.artifact.ArtifactManager;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.buildqueue.manager.AgentManager;
import com.atlassian.bamboo.configuration.SystemInfo;
import com.atlassian.bamboo.deployments.results.service.DeploymentResultService;
import com.atlassian.bamboo.labels.LabelManager;
import com.atlassian.bamboo.logger.ErrorUpdateHandler;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.bamboo.v2.build.agent.capability.AgentContext;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityConfigurationManager;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityDefaultsHelper;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.atlassian.sal.api.transaction.TransactionTemplate;

abstract public class ScriptEngineCore
{
	private static final Logger log = LoggerFactory.getLogger(ScriptEngineCore.class);
	// The following variables/managers are NOT safe to run on remote agents
	// These likely will only function on an agent that is executing on the
	// local
	// server.
	protected PlanManager planManager = null;
	public PlanManager getPlanManager()
	{
		return planManager;
	}
	public void setPlanManager(PlanManager planManager)
	{
		this.planManager = planManager;
	}

	protected LabelManager labelManager = null;
	public LabelManager getLabelManager()
	{
		return labelManager;
	}
	public void setLabelManager(LabelManager labelManager)
	{
		this.labelManager = labelManager;
	}

	protected ResultsSummaryManager resultsSummaryManager = null;
	public ResultsSummaryManager getResultsSummaryManager()
	{
		return resultsSummaryManager;
	}
	public void setResultsSummaryManager(ResultsSummaryManager resultsSummaryManager)
	{
		this.resultsSummaryManager = resultsSummaryManager;
	}

	protected TransactionTemplate transactionTemplate = null;
	public TransactionTemplate getTransactionTemplate()
	{
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate)
	{
		this.transactionTemplate = transactionTemplate;
	}

	protected DeploymentResultService deploymentResultService = null;
	public DeploymentResultService getDeploymentResultService()
	{
		return deploymentResultService;
	}
	public void setDeploymentResultService(DeploymentResultService deploymentResultService)
	{
		this.deploymentResultService = deploymentResultService;
	}

	protected AgentManager agentManager = null;
	public AgentManager getAgentManager()
	{
		return agentManager;
	}
	public void setAgentManager(AgentManager agentManager)
	{
		this.agentManager = agentManager;
	}

	// The following variables/managers ARE safe to run on remote agents and can
	// be used on
	// any build task.
	protected TestCollationService testCollationService = null;
	public TestCollationService getTestCollationService()
	{
		return testCollationService;
	}
	public void setTestCollationService(TestCollationService testCollationService)
	{
		this.testCollationService = testCollationService;
	}

	protected ProcessService processService = null;
	public ProcessService getProcessService()
	{
		return processService;
	}
	public void setProcessService(ProcessService processService)
	{
		this.processService = processService;
	}

	protected ArtifactManager artifactManager = null;
	public ArtifactManager getArtifactManager()
	{
		return artifactManager;
	}
	public void setArtifactManager(ArtifactManager artifactManager)
	{
		this.artifactManager = artifactManager;
	}

	protected CustomVariableContext customVariableContext = null;
	public CustomVariableContext getCustomVariableContext()
	{
		return customVariableContext;
	}
	public void setCustomVariableContext(CustomVariableContext customVariableContext)
	{
		this.customVariableContext = customVariableContext;
	}

	protected ErrorUpdateHandler errorUpdateHandler = null;
	public ErrorUpdateHandler getErrorUpdateHandler()
	{
		return errorUpdateHandler;
	}
	public void setErrorUpdateHandler(ErrorUpdateHandler errorUpdateHandler)
	{
		this.errorUpdateHandler = errorUpdateHandler;
	}

	protected AgentContext agentContext = null;
	public AgentContext getAgentContext()
	{
		return agentContext;
	}
	public void setAgentContext(AgentContext agentContext)
	{
		this.agentContext = agentContext;
	}

	protected CapabilityConfigurationManager capabilityConfigurationManager = null;
	public CapabilityConfigurationManager getCapabilityConfigurationManager()
	{
		return capabilityConfigurationManager;
	}
	public void setCapabilityConfigurationManager(CapabilityConfigurationManager capabilityConfigurationManager)
	{
		this.capabilityConfigurationManager = capabilityConfigurationManager;
	}

	protected CapabilityContext capabilityContext = null;
	public CapabilityContext getCapabilityContext()
	{
		return capabilityContext;
	}
	public void setCapabilityContext(CapabilityContext capabilityContext)
	{
		this.capabilityContext = capabilityContext;
	}

	protected CapabilityDefaultsHelper capabilityDefaultsHelper = null;
	public CapabilityDefaultsHelper getCapabilityDefaultsHelper()
	{
		return capabilityDefaultsHelper;
	}
	public void setCapabilityDefaultsHelper(CapabilityDefaultsHelper capabilityDefaultsHelper)
	{
		this.capabilityDefaultsHelper = capabilityDefaultsHelper;
	}

	protected SystemInfo systemInfo = null;
	public SystemInfo getSystemInfo()
	{
		return systemInfo;
	}
	public void setSystemInfo(SystemInfo systemInfo)
	{
		this.systemInfo = systemInfo;
	}

	protected BuildLoggerManager buildLoggerManager = null;
	public BuildLoggerManager getBuildLoggerManager()
	{
		return buildLoggerManager;
	}
	public void setBuildLoggerManager(BuildLoggerManager buildLoggerManager)
	{
		this.buildLoggerManager = buildLoggerManager;
	}

	protected String scriptRunOnServer = null;
	protected String scriptLanguage = null;
	protected String scriptBody = null;
	protected String scriptFile = null;
	protected String scriptLocation = null;

	protected void configurationInit(Map<String, String> config)
	{
		log.debug("Entering configuration");
		scriptRunOnServer = config.get(ScriptEngineConstants.SCRIPTENGINE_RUNONSERVER);
		scriptLanguage = config.get(ScriptEngineConstants.SCRIPTENGINE_SCRIPTTYPE);
		scriptBody = config.get(ScriptEngineConstants.SCRIPTENGINE_SCRIPTBODY);
		scriptFile = config.get(ScriptEngineConstants.SCRIPTENGINE_SCRIPTFILE);
		scriptLocation = config.get(ScriptEngineConstants.SCRIPTENGINE_SCRIPTLOCATION);

	}

	protected void executeScript(String script, String scriptLanguage, ScriptContext scriptContext, boolean isFile) throws FileNotFoundException, ScriptException
	{
		ScriptEngine engine = null;
		
		// Creating Nashorn engine directly first
		if ("js".equals(scriptLanguage) || "javascript".equals(scriptLanguage) || "nashorn".equals(scriptLanguage)) {
			try {
				NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
				engine = factory.getScriptEngine();
				log.info("Created Nashorn engine directly for script language: " + scriptLanguage);
			} catch (Exception e) {
				log.warn("Failed to create Nashorn engine directly: " + e.getMessage());
			}
		}
		
		// Fallback to ScriptEngineManager if script type is not a flavour of JS, ATM there is no support for other types
		if (engine == null) {
			ScriptEngineManager manager = new ScriptEngineManager(this.getClass().getClassLoader());
			engine = manager.getEngineByName(scriptLanguage);
			
			if (engine == null) {
				throw new ScriptException("Script engine '" + scriptLanguage + "' not found.");
			}
		}
		else
		{
			scriptContext.setAttribute("planManager", planManager, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("labelManager", labelManager, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("resultsSummaryManager", resultsSummaryManager, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("transactionTemplate", transactionTemplate, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("buildLoggerManager", buildLoggerManager, ScriptContext.ENGINE_SCOPE);

			scriptContext.setAttribute("testCollationService", this.testCollationService, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("processService", this.processService, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("artifactManager", this.artifactManager, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("customVariableContext", this.customVariableContext, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("errorUpdateHandler", this.errorUpdateHandler, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("agentContext", this.agentContext, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("capabilityConfigurationManager", this.capabilityConfigurationManager, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("capabilityContext", this.capabilityContext, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("capabilityDefaultsHelper", this.capabilityDefaultsHelper, ScriptContext.ENGINE_SCOPE);
			scriptContext.setAttribute("systemInfo", this.systemInfo, ScriptContext.ENGINE_SCOPE);

			engine.setContext(scriptContext);

			if (isFile)
			{
				log.info("Executing script from file: {}", script);
				engine.eval(new FileReader(script));
			}
			else
			{
				log.info("Executing inline script of {} characters", script.length());
				engine.eval(script);
			}
		}
	}
}
