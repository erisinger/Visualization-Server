package websocket_data_server;

import java.util.*;
import java.io.*;

/*
	Create a new FileWatcher by passing in a file (not path) and
	millisecond duration between check events.  
	
	Call startWatching() on the
	new instance to begin monitoring, stopWatching() to end.
	
	See main() for example usage.
*/

public class FileWatcher{
	RandomAccessFile watchFile;
	Timer timer;
	long lastFileSize = 0;
	int refresh = 10;
	
	public FileWatcher(File f, int r){
		try {
			this.watchFile = new RandomAccessFile(f, "r");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.refresh = r;
		this.timer = new Timer();
	}
	
	public void startWatching(){
		timer.schedule(new FileWatcherThread(this), 0, this.refresh);
	}
	
	public void stopWatching(){
		timer.cancel();
	}
	
	public boolean hasRecentLines(){
		
		if (watchFile == null) {
			return false;
		}
		long fileSize = 0;
		try {
			fileSize = watchFile.length();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileSize > lastFileSize;
	}
	
	public ArrayList<String> getRecentLines(){
		ArrayList<String> recentLines = new ArrayList<>();
		
		if (!hasRecentLines()) { return recentLines; }
		
		String line;
		try {
			while ((line = watchFile.readLine()) != null) {
				recentLines.add(line);
			}
			
			//potential concurrency trouble...
			lastFileSize = watchFile.length();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return recentLines;
	}
	
	/*  timer thread  */
	private class FileWatcherThread extends TimerTask{
		FileWatcher watcher;
		public FileWatcherThread(FileWatcher w){
			this.watcher = w;
		}
		
		public void run(){

		}
	}
	
	public static void main(String[] args) {
		try {
			
			File watchFile = new File("/users/erikrisinger/development/accel_vals.txt");
			FileWatcher watcher = new FileWatcher(watchFile, 100);
			watcher.startWatching();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}