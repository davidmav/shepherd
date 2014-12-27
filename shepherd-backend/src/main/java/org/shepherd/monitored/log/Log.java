package org.shepherd.monitored.log;

import java.io.RandomAccessFile;

import org.shepherd.monitored.Monitored;

public interface Log extends Monitored {
	
	public boolean isLocal();
	
	public String getPath();
	
	public RandomAccessFile getFile();

}
