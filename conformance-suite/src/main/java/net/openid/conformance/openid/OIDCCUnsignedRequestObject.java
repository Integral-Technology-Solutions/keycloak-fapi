package net.openid.conformance.openid;

import net.openid.conformance.testmodule.PublishTestModule;

// Corresponds to https://www.heenan.me.uk/~joseph/oidcc_test_desc-phase1.html#OP_request_Unsigned
@PublishTestModule(
	testName = "oidcc-unsigned-request-object",
	displayName = "OIDCC: unsigned request object",
	summary = "This test sends a unsigned request object (by value) to the authorization endpoint. The server must either accept the request and process the authentication correctly,  or return a request_not_supported error as per OIDCC-3.1.2.6. Note that the python suite allowed implementations to completely ignore the request object - this was not compliant with the spec, and in this test either the object must be processed or request_not_supported must be returned.",
	profile = "OIDCC"
)
public class OIDCCUnsignedRequestObject extends AbstractOIDCCRequestObjectServerTest {

}
