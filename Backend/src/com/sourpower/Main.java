package com.sourpower;

import java.util.Arrays;
import java.util.HashSet;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;

import com.sourpower.resources.ActivityResource;
import com.sourpower.resources.AuthenticationResource;
import com.sourpower.resources.AvatarResource;
import com.sourpower.resources.FriendRequestResource;
import com.sourpower.resources.FriendResource;
import com.sourpower.resources.ShutdownResource;
import com.sourpower.resources.UserResource;
import com.sourpower.resources.UserSearchResource;
import com.sourpower.resources.ScoreResource;

public class Main extends Application {
	public static void main(String[] args) throws Exception {
		final Component component = new Component();
        // tell the interface to listen to http:12345
        component.getServers().add(Protocol.HTTP, 12345);
        // create the application, giving it the component's context
        // technically, its child context, which is a protected version of its context
        Main server = new Main(component.getContext().createChildContext());
        // attach the application to the interface
        component.getDefaultHost().attach(server);
        // go to town
        component.start();
	}
	
	// just your everyday chaining constructor
    public Main(Context context) {
        super(context);
        CorsService corsService = new CorsService();         
        corsService.setAllowedOrigins(new HashSet<String>(Arrays.asList("*")));
        corsService.setAllowedCredentials(true);
        getServices().add(corsService);
    }

    /** add hooks to your services - this will get called by the component when
     * it attaches the application to the component (I think... or somewhere in there
     * it magically gets called... or something...)
     */
    public Restlet createInboundRoot() {
        // create a router to route the incoming queries
        Router router = new Router(getContext().createChildContext());
        // attach your resource here
        router.attach("/user", UserResource.class);
        router.attach("/user/search", UserSearchResource.class);
        router.attach("/friend", FriendResource.class);
        router.attach("/friend/request", FriendRequestResource.class);
        router.attach("/activity", ActivityResource.class);
        router.attach("/user/authenticate", AuthenticationResource.class);
        router.attach("/control/shutdown", ShutdownResource.class);
        router.attach("/score", ScoreResource.class);
        router.attach("/avatar", AvatarResource.class);
        // return the router.
        return router;
    }
}
