package com.sourpower.resources;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class ShutdownResource extends ServerResource {
	@Post("json")
	public Representation shutdown(JsonRepresentation entity) {
		System.exit(0);
		return entity;
	}
}
