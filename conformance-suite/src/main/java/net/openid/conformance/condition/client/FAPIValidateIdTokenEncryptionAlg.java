package net.openid.conformance.condition.client;

import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class FAPIValidateIdTokenEncryptionAlg extends AbstractCondition {

	@Override
	@PreEnvironment(required = "id_token")
	public Environment evaluate(Environment env) {

		String alg = env.getString("id_token", "jwe_header.alg");

		if (alg.equals("RSA1_5")) {
			throw error("id_token encrypted with RSA1_5, which is not permitted by FAPI-RW", args("alg", alg));
		}

		logSuccess("id_token was encrypted with a permitted algorithm", args("alg", alg));
		return env;

	}

}
