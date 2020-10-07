package net.openid.conformance.condition.as;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.runner.TestDispatcher;
import net.openid.conformance.testmodule.Environment;
import net.openid.conformance.testmodule.OIDFJSON;

public class SetTokenEndpointAuthMethodsSupportedToSelfSignedTlsClientAuthOnly extends AbstractCondition {

	@Override
	@PreEnvironment(required = "server")
	@PostEnvironment(required = "server")
	public Environment evaluate(Environment env) {

		JsonArray data = new JsonArray();
		data.add("self_signed_tls_client_auth");

		JsonObject server = env.getObject("server");
		server.add("token_endpoint_auth_methods_supported", data);
		env.putObject("server", server);

		log("Changed token_endpoint_auth_methods_supported to self_signed_tls_client_auth only in server configuration",
			args("server_configuration", server));

		return env;
	}
}
