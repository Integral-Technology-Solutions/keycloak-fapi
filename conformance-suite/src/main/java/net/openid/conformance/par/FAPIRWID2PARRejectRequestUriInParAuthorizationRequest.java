package net.openid.conformance.par;

import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.AddBadRequestUriToAuthorizationRequest;
import net.openid.conformance.condition.client.CallPAREndpoint;
import net.openid.conformance.condition.client.EnsurePARInvalidRequestObjectError;
import net.openid.conformance.fapi.AbstractFAPIRWID2ServerTestModule;
import net.openid.conformance.sequence.ConditionSequence;
import net.openid.conformance.testmodule.PublishTestModule;
import net.openid.conformance.variant.FAPIAuthRequestMethod;
import net.openid.conformance.variant.VariantNotApplicable;

//PAR-2.1 : The request_uri authorization request parameter MUST NOT be provided in this case
@PublishTestModule(
	testName = "fapi-rw-id2-par-authorization-request-containing-request_uri",
	displayName = "PAR : authorization request must not contain request_uri parameter",
	summary = "This test sends a random request_uri parameter in authorization request object and expects authorization server to return an error",
	profile = "FAPI-RW-ID2",
	configurationFields = {
		"server.discoveryUrl",
		"client.client_id",
		"client.scope",
		"client.jwks",
		"mtls.key",
		"mtls.cert",
		"mtls.ca",
		"client2.client_id",
		"client2.scope",
		"client2.jwks",
		"mtls2.key",
		"mtls2.cert",
		"mtls2.ca",
		"resource.resourceUrl"
	}
)
@VariantNotApplicable(parameter = FAPIAuthRequestMethod.class, values = {
	"by_value"
})
public class FAPIRWID2PARRejectRequestUriInParAuthorizationRequest extends AbstractFAPIRWID2ServerTestModule {

	@Override
	protected ConditionSequence makeCreateAuthorizationRequestObjectSteps() {
		return super.makeCreateAuthorizationRequestObjectSteps().
			butFirst(condition(AddBadRequestUriToAuthorizationRequest.class).requirement("PAR-2"));
	}

	@Override
	protected void performParAuthorizationRequestFlow() {

		callAndStopOnFailure(CallPAREndpoint.class);

		// this might be too strict, the spec mentions this error but doesn't require servers to use it
		// the only firm requirement is for the http status code to indicate failure
		callAndContinueOnFailure(EnsurePARInvalidRequestObjectError.class, Condition.ConditionResult.FAILURE, "PAR-2.1");

		fireTestFinished();
	}
}
