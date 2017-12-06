package com.sourpower.DesignPatterns;

import org.restlet.representation.Representation;

public interface Observer {
	public Representation observerUpdate(int userId, String activityType, int score);
}
