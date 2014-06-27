package de.dbis.services;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.*;
import com.nimbusds.openid.connect.sdk.*;

import de.dbis.util.CORS;


/**
 * OpenID Connect login start page.
*/
@Path("/openIDauth")
@Component
public class OIDCLoginStart extends HttpServlet {

	
	@Context UriInfo uriInfo;
	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}

	private URL composeAuthzRequestURL()
		throws Exception {

		// Set the requested response_type (code, token and / or 
		// id_token):
		// Use CODE for authorisation code flow
		// Use TOKEN for implicit flow
		ResponseType rt = new ResponseType("code");

		// Set the requested scope of access
		Scope scope = new Scope("openid", "email", "profile");

		// Identify the client app by its registered ID
		ClientID clientID = new ClientID("clvitra");

		// Set the redirect URL after successful OIDC login / 
		// authorisation. This URL is typically registered in advance 
		// with the OIDC server
		//URI redirectURI = new URI("http://137.226.58.27:9080/ClViTra_2.0/FileUpload.html");
		URI redirectURI = new URI("http://137.226.58.27:9080/ClViTra_2.0/FileUpload.html");
		//URI redirectURI = new URI("http://127.0.0.1:8080/oidc-test-client/in");
		
		// Generate random state value. It's used to link the
		// authorisation response back to the original request, also to
		// prevent replay attacks
		State state = new State();

		// Generate random nonce value.
		Nonce nonce = new Nonce();

		// Create the actual OIDC authorisation request object
		AuthenticationRequest authRequest = new AuthenticationRequest(redirectURI, rt, scope, clientID, redirectURI, state, nonce);


		// Get the resulting URL query string with the authorisation
		// request encoded into it
		String queryString = authRequest.toQueryString();


		// Set the base URL of the OIDC server authorisation endpoint
		URL authzEndpointURL = new URL("http://137.226.58.15:9085/openid-connect-server-webapp/authorize");


		// Construct and output the final OIDC authorisation URL for
		// redirect
		URL authzURL = new URL(authzEndpointURL + "?" + queryString);

		return authzURL;
	}


	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response doGet() {

		URL authzURL;

		try {
			authzURL = composeAuthzRequestURL();

		} catch (Exception e) {

			System.out.println("<p>Couldn't compose OIDC authorisation request URL: " + e.getMessage() + "</p>");
			Response.ResponseBuilder r = Response.noContent();
			return CORS.makeCORS(r, _corsHeaders);
		}
		Response.ResponseBuilder r = Response.ok(authzURL.toString());
		return CORS.makeCORS(r, _corsHeaders);
		
	}
	
}