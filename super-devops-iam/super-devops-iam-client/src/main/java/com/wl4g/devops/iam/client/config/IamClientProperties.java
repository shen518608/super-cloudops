/*
 * Copyright 2017 ~ 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.iam.client.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.iam.client.config.IamClientProperties.ClientParamProperties;

@ConfigurationProperties(prefix = "spring.cloud.devops.iam.client")
public class IamClientProperties extends AbstractIamProperties<ClientParamProperties> implements InitializingBean {
	private static final long serialVersionUID = -8848998112902613969L;

	/**
	 * Default IAM server URI.
	 */
	final public static String DEFAULT_VIEW_SERV_URI = DEFAULT_VIEW_BASE_URI + "/login.html";

	/**
	 * IAM server base URI (public network)
	 */
	private String serverUri = "http://localhost:14040/iam-server";

	/**
	 * Application name. e.g. http://host:port/{serviceName}/shiro-cas
	 */
	private String serviceName;

	/**
	 * IAM server login page URI
	 */
	private String loginUri = "http://localhost:14040/iam-server" + DEFAULT_VIEW_SERV_URI;

	/**
	 * This success(index) page URI
	 */
	private String successUri = "http://localhost:8080/index";

	/**
	 * IAM server unauthorized(403) page URI
	 */
	private String unauthorizedUri = "http://localhost:14040/iam-server" + DEFAULT_VIEW_403_URI;

	/**
	 * Re-login callback URL, whether to use the previously remembered URL
	 */
	private boolean useRememberRedirect = false;

	/**
	 * Secondary authenticator provider name.
	 */
	private String secondAuthenticatorProvider = "wechat";

	/**
	 * Filter chains.
	 */
	private Map<String, String> filterChain = new HashMap<>();

	/**
	 * IAM client parameters configuration.
	 */
	private ClientParamProperties param = new ClientParamProperties();

	public String getServerUri() {
		return serverUri;
	}

	public void setServerUri(String baseUri) {
		this.serverUri = baseUri;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public String getLoginUri() {
		return loginUri;
	}

	public void setLoginUri(String loginUri) {
		this.loginUri = WebUtils2.cleanURI(loginUri);
	}

	@Override
	public String getSuccessUri() {
		return successUri;
	}

	public void setSuccessUri(String successUri) {
		this.successUri = successUri;
	}

	@Override
	public String getUnauthorizedUri() {
		return unauthorizedUri;
	}

	public void setUnauthorizedUri(String unauthorizedUri) {
		this.unauthorizedUri = unauthorizedUri;
	}

	public boolean isUseRememberRedirect() {
		return useRememberRedirect;
	}

	public void setUseRememberRedirect(boolean useRememberRedirect) {
		this.useRememberRedirect = useRememberRedirect;
	}

	public String getSecondAuthenticatorProvider() {
		return secondAuthenticatorProvider;
	}

	public void setSecondAuthenticatorProvider(String secondAuthcProvider) {
		this.secondAuthenticatorProvider = secondAuthcProvider;
	}

	public Map<String, String> getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(Map<String, String> filterChain) {
		this.filterChain = filterChain;
	}

	@Override
	public ClientParamProperties getParam() {
		return param;
	}

	@Override
	public void setParam(ClientParamProperties param) {
		this.param = param;
	}

	@Override
	protected void applyDefaultIfNecessary() {
		if (isBlank(getServiceName())) {
			setServiceName(environment.getProperty("spring.application.name"));
		}
	}

	@Override
	protected void validation() {
		super.validation();
		Assert.notNull(getServerUri(), "'baseUri' must be empty.");
		Assert.notNull(getServiceName(), "'serviceName' must be empty.");
		Assert.notNull(getFilterChain(), "'filterChain' must be empty.");
	}

	/**
	 * IAM client parameters configuration properties
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月29日
	 * @since
	 */
	public static class ClientParamProperties extends ParamProperties {
		private static final long serialVersionUID = 3258460473777285504L;

	}

}