package com.durianberry.bbcommons.task;

public interface TaskProgressListener {
		
		void taskUpdate(Object obj);
		
		void taskComplete(Object obj);
}
