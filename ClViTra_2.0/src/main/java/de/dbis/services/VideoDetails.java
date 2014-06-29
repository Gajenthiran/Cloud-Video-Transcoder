package de.dbis.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import de.dbis.util.CORS;

/**
 * Returns video information and status.
 */
@Path("/videoDetail/{user}/{videoId}")
@Component
public class VideoDetails {
	
	@Context UriInfo uriInfo;
	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}
	
	/**
	 * Returns the Video URL, Thumbnail URL, Video Name, and Video status for the given video ID uploaded by the logged in User. 
	 * @param username 
	 * @param videoId
	 * @return javax.ws.rs.core.Response JSON formatted string
	 * @throws JSONException
	 */
	@GET
	@Produces("application/json")
	public Response Details(@PathParam("user") String username, @PathParam("videoId") String videoId) throws JSONException{
		   String Name, URI, Thumbnail, Status;
		   //String output = "<form action=\"upload\" method=\"post\" enctype=\"multipart/form-data\"> "
		   	//	+ "<p> Select a file : <input type=\"file\" name=\"file\" size=\"45\" /> </p> <input type=\"submit\" value=\"Upload It\" /> </form> ";
		   //new Java2MySql();
		   
		   int UserId = Java2MySql.getUserId(username);
		   
		   String[] Details = Java2MySql.getVideoDetails(UserId, videoId);
		   
		   //System.out.println(myList.size());
		   JSONObject j =  new JSONObject();
		   JSONObject j_final = new JSONObject();
		   
		   //j.put("videolist", output);

		   //if (!myList.isEmpty()) {
		   
		   if(!"Not Found".equals(Details[0])){
		   
			   Name = Details[0];
			   URI = Details[1];
			   Thumbnail = Details[2];
			   Status = Details[3];
			   j.put("Video_Name", Name);
			   j.put("Video_URL", URI);
			   j.put("Thumbnail_URL", Thumbnail);
			   j.put("Status", Status);
			   
		   }
		   else{
			   j.put("Status", Details[0]);
		   }
		   //j_final.put("Videos", j);
		   Response.ResponseBuilder r = Response.ok(j.toString());
		   return CORS.makeCORS(r, _corsHeaders);
	 }
}