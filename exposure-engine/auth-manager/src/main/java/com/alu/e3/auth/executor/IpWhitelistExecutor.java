/**
 * Copyright © 2012 Alcatel-Lucent.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * Licensed to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.alu.e3.auth.executor;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;

import com.alu.e3.auth.access.IAuthDataAccess;
import com.alu.e3.auth.model.AuthType;
import com.alu.e3.common.camel.AuthReport;
import com.alu.e3.common.logging.Category;
import com.alu.e3.common.logging.CategoryLogger;
import com.alu.e3.common.logging.CategoryLoggerFactory;
import com.alu.e3.common.tools.CanonicalizedIpAddress;
import com.alu.e3.gateway.common.camel.exception.GatewayExceptionCode;

public class IpWhitelistExecutor implements IAuthExecutor {

	private static final CategoryLogger logger = CategoryLoggerFactory.getLogger(IpWhitelistExecutor.class, Category.AUTH);

	private String apiId;
	private IAuthDataAccess dataAccess;


	public IpWhitelistExecutor(String apiId, IAuthDataAccess dataAccess) {
		this.apiId = apiId;
		this.dataAccess = dataAccess;
	}

	@Override
	public AuthReport checkAllowed(Exchange exchange) {
		
		AuthReport authReport = new AuthReport();
		
		logger.debug("Hit the IpWhitelistExecutor.isAllowed");
		
		// magic Jetty stuff
		HttpServletRequest request = (HttpServletRequest) exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST);
		
		if(request != null) {
			CanonicalizedIpAddress ip = new CanonicalizedIpAddress(request.getRemoteAddr());
			authReport = dataAccess.checkAllowed(ip, apiId);
		} else {
			authReport.setBadRequest(true);
		}
			
		return authReport;
	}

	@Override
	public AuthType getType() {
		return AuthType.IP_WHITE_LIST;
	}

	@Override
	public GatewayExceptionCode getErrorCode() {
		return GatewayExceptionCode.AUTHORIZATION;
	}
}