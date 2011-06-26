package com.durianberry.bbcommons.task;

public interface Task {

	void execute();
	
	void setProgressListener(final TaskProgressListener progressListener) ;

}

