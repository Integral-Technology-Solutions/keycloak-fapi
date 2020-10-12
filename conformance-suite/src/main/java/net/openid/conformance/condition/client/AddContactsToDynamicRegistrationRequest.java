package net.openid.conformance.condition.client;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class AddContactsToDynamicRegistrationRequest extends AbstractCondition {

	@Override
	@PreEnvironment(required = "dynamic_registration_request")
	@PostEnvironment(required = "dynamic_registration_request")
	public Environment evaluate(Environment env) {

		if (!env.containsObject("dynamic_registration_request")) {
			throw error("No dynamic registration request found");
		}

		JsonObject dynamicRegistrationRequest = env.getObject("dynamic_registration_request");

		JsonArray contacts = new JsonArray();
		contacts.add("certification@oidf.org");
		dynamicRegistrationRequest.add("contacts", contacts);

		env.putObject("dynamic_registration_request", dynamicRegistrationRequest);

		log("Added contacts array to dynamic registration request", args("dynamic_registration_request", dynamicRegistrationRequest));

		return env;
	}
}
