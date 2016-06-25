/*
 * Copyright 2013-2014 Andrea De Cesare
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nkdroid.dressify;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class ClothesApplication extends Application {
	private static Context context;
	//private BrowserDirectory currentDirectory;
    public int currentPage = -1;
	private String lastSearch;


	@Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("MusicPlayer", "Low memory condition!");
     //   imagesCache.clearCache();
    }

    public static Context getContext() {
        return context;
    }
    
    /*public BrowserDirectory getCurrentDirectory() {
		return currentDirectory;
	}*/
    
    /* Moves to a new directory */
	/*public void gotoDirectory(File directory) {
		currentDirectory = new BrowserDirectory(directory);
	}*/
	
	public void setLastSearch(String lastSearch) {
		this.lastSearch = lastSearch;
	}
	
	public String getLastSearch() {
		return lastSearch;
	}

    @Override
    public void onTerminate(){
        super.onTerminate();
    }
}
