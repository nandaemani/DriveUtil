/*
 * Copyright 2011 Nanda Emani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.company.app.client;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;


public class DriveUtil {

	private static JSONObject currFile;

	private static String INSTALL_SCOPE = "https://www.googleapis.com/auth/drive.install";
	private static String FILE_SCOPE = "https://www.googleapis.com/auth/drive.file";
	private static String DRIVE_SCOPE = "https://www.googleapis.com/auth/drive.readonly";
	private static String USER_INFO_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	private static String OPENID_SCOPE = "openid";

	public static native void authorize(String type, String title, String fileId, String folderId, String fileData) /*-{
		$wnd.gapi.load('auth:client,picker,drive-share', function() {
		      var clientId = '12345667-a4cde138kivdb67jk82abcd9vaks3gea';
		      var scopes = ['https://www.googleapis.com/auth/drive.file'];
			
			  var handleAuthResult = function(authResult) {
			    if (authResult && !authResult.error) {
			      if (type == 'create'){
			      	@com.company.app.client.DriveUtil::createFile(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(title, fileData, folderId);
			      }else if (type == 'update') {
			      	@com.company.app.client.DriveUtil::updateFile(Ljava/lang/String;)(fileData);
			      }else if (type == 'load') {
			      	@com.company.app.client.DriveUtil::loadFile(Ljava/lang/String;)(fileId);
			      }else if (type == 'share') {
			      	var share = new $wnd.gapi.drive.share.ShareClient(clientId);
			      	share.setItemIds([fileId]);
					share.showSettingsDialog();
			      }else if (type == 'open') {
			      	var view = new $wnd.google.picker.View($wnd.google.picker.ViewId.DOCS).setQuery('*.sl');
					view.setMimeTypes('text/plain');
					var pickerCallback = function(data) {
				      if (data[$wnd.google.picker.Response.ACTION] == $wnd.google.picker.Action.PICKED) {
				        var doc = data[$wnd.google.picker.Response.DOCUMENTS][0];
				        @com.company.app.client.DriveUtil::openFile(Ljava/lang/String;)(doc.id);
				      }
				    };
				    
					var picker = new $wnd.google.picker.PickerBuilder().
					                 addView(view).
					                 enableFeature($wnd.google.picker.Feature.NAV_HIDDEN).
					                 setOAuthToken(authResult.access_token).
					                 setTitle('Choose file').
					                 setAppId(clientId).
					                 setCallback(pickerCallback).
					                 build();
					picker.setVisible(true);
			      }
			    } else {
			      authorizeWithPopup();
			    }
			  };
			
			  var authorizeWithPopup = function() {
			     $wnd.gapi.auth.authorize({
			      client_id: clientId,
			      scope: scopes,
			      immediate: false
			    }, handleAuthResult);
			  };
			
			  $wnd.gapi.auth.authorize({
			    client_id: clientId,
			    scope: scopes,
			    immediate: true
			  }, handleAuthResult);
		  });
	}-*/;

	private static native void createFile(String title, String fileData, String folderId)/*-{
		var boundary = '-------314159265358979323846';
	    var delimiter = "\r\n--" + boundary + "\r\n";
	    var close_delim = "\r\n--" + boundary + "--";
	
	      var contentType = "text/plain";
	      if (folderId != null)
	          var metadata = {
	    	    'title': title,
	    	    'parents': [{'id':folderId}],
	    	    'mimeType': contentType
	    	  };
	      else
	          var metadata = {
	  	        'title': title,
	  	        'mimeType': contentType
	  	      };
	
	      var multipartRequestBody =
	          delimiter +
	          'Content-Type: application/json\r\n\r\n' +
	          JSON.stringify(metadata) +
	          delimiter + 'Content-Type: ' + contentType + '\r\n' + '\r\n' +
	          fileData +
	          close_delim;
	
	      var accessToken = $wnd.gapi.auth.getToken().access_token;
	      var request = $wnd.gapi.client.request({
	          'path': '/upload/drive/v2/files',
	          'method': 'POST',
	          'params': {'uploadType': 'multipart'},
	          'headers': {
	            'Content-Type': 'multipart/mixed; boundary="' + boundary + '"',
	            'Authorization': 'Bearer ' + accessToken
	          },
	          'body': multipartRequestBody});
	
          var callback = function(file) {
	           if (!file.error) {
	           	@com.company.app.client.Message::showSuccessMsg(Ljava/lang/String;)('new file created');
	           	@com.company.app.client.DriveUtil::openFile(Ljava/lang/String;)(file.id);
	           }
	           else
	            @com.company.app.client.Message::showErrorMsg(Ljava/lang/String;)(file.error.message);
	      };	      
	      request.execute(callback);
	}-*/;

	private static native void updateFile(String fileData) /*-{
		var boundary = '-------314159265358979323846';
	    var delimiter = "\r\n--" + boundary + "\r\n";
	    var close_delim = "\r\n--" + boundary + "--";
	
	   if(currFile.mimeType){
	      var multipartRequestBody =
	    	  delimiter +  'Content-Type: application/json\r\n\r\n' +
	          JSON.stringify(currFile) +
	          delimiter + 'Content-Type: ' + currFile.mimeType + '\r\n' + '\r\n' +
	          fileData +
	          close_delim;
	      
	      var accessToken = $wnd.gapi.auth.getToken().access_token;
	      var request = $wnd.gapi.client.request({
	          'path': '/upload/drive/v2/files/' + currFile.id,
	          'method': 'PUT',
	          'params': {'uploadType': 'multipart'},
	          'headers': {
	            'Content-Type': 'multipart/mixed; boundary="' + boundary + '"',
	            'Authorization': 'Bearer ' + accessToken
	          },
	          'body': multipartRequestBody});
	
	      var callback = function(file) {
	           if (!file.error) {	
	        	@com.company.app.client.Message::showSuccessMsg(Ljava/lang/String;)('saved');
	           }
	           else
	        	@com.company.app.client.Message::showErrorMsg(Ljava/lang/String;)(file.error.message);
	      };
	      request.execute(callback);
	   }
	}-*/;

	private static native void loadFile(String fileId)/*-{
		
		var callback = function(file) {
           if (!file.error) {	
        	if (file.downloadUrl){
        		  currFile = file;
			      var accessToken = $wnd.gapi.auth.getToken().access_token;
			      var xhr = new XMLHttpRequest();
			      xhr.open('GET', file.downloadUrl);
			      xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
			      xhr.onload = function() {
			    	  @com.company.app.client.JsonUtil::loadJSON(Ljava/lang/String;)(xhr.responseText);
			    	  $doc.title = file.title;
			    	  @com.company.app.client.Logger::log(Ljava/lang/String;)(file.title);
			      };
			      xhr.onerror = function(e) {
					  if (e.error){
					   @com.company.app.client.Message::showErrorMsg(Ljava/lang/String;)(e.error);
					  }
					  else
					   @com.company.app.client.Message::showErrorMsg(Ljava/lang/String;)('Error');
			      };
			      xhr.send();
			    } else {
			      if (file.error.message){
			       @com.company.app.client.Message::showErrorMsg(Ljava/lang/String;)(file.error.message);
			      }else
			       @com.company.app.client.Message::showErrorMsg(Ljava/lang/String;)('Error');
			    }
        	}
        	else
	        	@com.company.app.client.Message::showErrorMsg(Ljava/lang/String;)(file.error.message);
        	
        };
		
		$wnd.gapi.client.load('drive', 'v2', function() {
	      $wnd.gapi.client.drive.files.get({
	        'fileId' : fileId
	      }).execute(callback);
	    });
		
	}-*/;



	private static void openFile(String fileId){
		Window.Location.replace(StaticVar.APP_BASE_URL+"/?fileId="+fileId);
	}
}
