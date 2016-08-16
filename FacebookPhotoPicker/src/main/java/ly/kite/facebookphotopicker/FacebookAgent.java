/*****************************************************
 *
 * FacebookAgent.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.facebookphotopicker;


///// Import(s) /////

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import ly.kite.photopicker.common.Photo;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an agent for the Facebook APIs.
 *
 *****************************************************/
public class FacebookAgent
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  static private final String  LOG_TAG                        = "FacebookAgent";

  static private final boolean DEBUGGING_ENABLED              = false;

  static private final String  PERMISSION_USER_PHOTOS         = "user_photos";

  static private final String  GRAPH_PATH_MY_PHOTOS           = "/me/photos";

  static private final String  PARAMETER_NAME_TYPE            = "type";
  static private final String  PARAMETER_VALUE_TYPE           = "uploaded";

  static private final String  PARAMETER_NAME_FIELDS          = "fields";
  static private final String  PARAMETER_VALUE_FIELDS         = "id,link,picture,images";

  static private final String  JSON_NAME_DATA                 = "data";
  static private final String  JSON_NAME_ID                   = "id";
  static private final String  JSON_NAME_PICTURE              = "picture";
  static private final String  JSON_NAME_IMAGES               = "images";

  static private final String  JSON_NAME_WIDTH                = "width";
  static private final String  JSON_NAME_HEIGHT               = "height";
  static private final String  JSON_NAME_SOURCE               = "source";

  static private final String  HTTP_HEADER_NAME_AUTHORISATION   = "Authorization";
  static private final String  HTTP_AUTHORISATION_FORMAT_STRING = "Bearer %s";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Activity         mActivity;
  private CallbackManager  mCallbackManager;

  private GraphRequest     mNextPhotosPageGraphRequest;

  private ARequest         mPendingRequest;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of this agent.
   *
   *****************************************************/
  static public FacebookAgent getInstance( Activity activity )
    {
    // We don't cache the instance, because we don't want to hold
    // onto the activity. The activity we use always needs to be the
    // current one, otherwise subsequent re-log-ins can fail.

    return ( new FacebookAgent( activity ) );
    }


  /*****************************************************
   *
   * Logs out.
   *
   *****************************************************/
  static void logOut()
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> logOut()" );

    LoginManager.getInstance().logOut();

    if ( DEBUGGING_ENABLED )
      {
      Log.d( LOG_TAG, "  Access token = " + AccessToken.getCurrentAccessToken() );
      Log.d( LOG_TAG, "<-- logOut()" );
      }
    }


  /*****************************************************
   *
   * Returns a string representation of an access token.
   *
   *****************************************************/
  static private String stringFrom( AccessToken accessToken )
    {
    if ( accessToken == null ) return ( "<null>" );

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append( "Token          : " ).append( accessToken.getToken()         ).append( ( '\n' ) );
    stringBuilder.append( "Application Id : " ).append( accessToken.getApplicationId() ).append( ( '\n' ) );
    stringBuilder.append( "Expires        : " ).append( accessToken.getExpires()       ).append( ( '\n' ) );
    stringBuilder.append( "Last Refresh   : " ).append( accessToken.getLastRefresh()   ).append( ( '\n' ) );
    stringBuilder.append( "Source         : " ).append( accessToken.getSource()        ).append( ( '\n' ) );
    stringBuilder.append( "Permissions    : " ).append( accessToken.getPermissions()   ).append( ( '\n' ) );
    stringBuilder.append( "User Id        : " ).append( accessToken.getUserId()        ).append( ( '\n' ) );

    return ( stringBuilder.toString() );
    }


  ////////// Constructor(s) //////////

  private FacebookAgent( Context context )
    {
    }

  private FacebookAgent( Activity activity )
    {
    mActivity = activity;

    FacebookSdk.sdkInitialize( activity.getApplicationContext() );

    mCallbackManager = CallbackManager.Factory.create();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when an activity returns a result.
   *
   *****************************************************/
  void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    if ( mCallbackManager != null ) mCallbackManager.onActivityResult( requestCode, resultCode, data );
    }


  /*****************************************************
   *
   * Processes a new access token.
   *
   *****************************************************/
  private void newAccessToken( AccessToken accessToken )
    {
    if ( DEBUGGING_ENABLED )
      {
      Log.d( LOG_TAG, "--> newAccessToken( accessToken = " + stringFrom( accessToken ) + " )" );
      Log.d( LOG_TAG, "mPendingRequest = " + mPendingRequest );
      }

    if ( mPendingRequest != null  )
      {
      ARequest pendingRequest = mPendingRequest;

      mPendingRequest = null;

      pendingRequest.onExecute();
      }


    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- newAccessToken( accessToken )" );

    }


  /*****************************************************
   *
   * Loads an initial set of photos.
   *
   *****************************************************/
  private void executeRequest( ARequest request )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> executeRequest( request = " + request + " )" );

    // If we don't have an access token - make a log-in request.

    AccessToken accessToken = AccessToken.getCurrentAccessToken();

    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "accessToken = " + stringFrom( accessToken ) );

    if ( accessToken == null || accessToken.getUserId() == null )
      {
      LoginManager loginManager = LoginManager.getInstance();

      loginManager.registerCallback( mCallbackManager, new LoginResultCallback() );

      mPendingRequest = request;

      loginManager.logInWithReadPermissions( mActivity, Arrays.asList( PERMISSION_USER_PHOTOS ) );

      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- executeRequest( request )" );

      return;
      }


    // If the access token has expired - refresh it

    if ( accessToken.isExpired() )
      {
      Log.i( LOG_TAG, "Access token has expired - refreshing" );

      mPendingRequest = request;

      AccessToken.refreshCurrentAccessTokenAsync();

      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- executeRequest( request )" );

      return;
      }


    // We have a valid access token, so execute the request
    request.onExecute();

    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- executeRequest( request )" );
    }


  /*****************************************************
   *
   * Clears any next page request, so photos are retrieved
   * from the start.
   *
   *****************************************************/
  void resetPhotos()
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "resetPhotos()" );

    mNextPhotosPageGraphRequest = null;
    }


  /*****************************************************
   *
   * Loads the next available page of photos.
   *
   *****************************************************/
  void getPhotos( IPhotosCallback photosCallback )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "getPhotos( photosCallback )" );

    PhotosRequest photosRequest = new PhotosRequest( photosCallback );

    executeRequest( photosRequest );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A request.
   *
   *****************************************************/
  private abstract class ARequest<T extends ICallback>
    {
    T  mCallback;


    ARequest( T callback )
      {
      mCallback = callback;
      }


    abstract void onExecute();


    void onError( Exception exception )
      {
      if ( mCallback != null ) mCallback.facOnError( exception );
      }


    void onCancel()
      {
      if ( mCallback != null ) mCallback.facOnCancel();
      }
    }


  /*****************************************************
   *
   * A photos request.
   *
   *****************************************************/
  private class PhotosRequest extends ARequest<IPhotosCallback>
    {
    PhotosRequest( IPhotosCallback photosCallback )
      {
      super( photosCallback );
      }


    @Override
    public void onExecute()
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> PhotosRequest.onExecute()" );

      // If we already have a next page request ready - execute it now. Otherwise
      // start a brand new request.

      PhotosGraphRequestCallback photosGraphRequestCallback = new PhotosGraphRequestCallback( mCallback );

      if ( mNextPhotosPageGraphRequest != null )
        {
        mNextPhotosPageGraphRequest.setCallback( photosGraphRequestCallback );

        mNextPhotosPageGraphRequest.executeAsync();

        mNextPhotosPageGraphRequest = null;

        if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- PhotosRequest.onExecute()" );

        return;
        }


      Bundle parameters = new Bundle();

      parameters.putString( PARAMETER_NAME_TYPE,   PARAMETER_VALUE_TYPE );
      parameters.putString( PARAMETER_NAME_FIELDS, PARAMETER_VALUE_FIELDS );

      GraphRequest request = new GraphRequest(
              AccessToken.getCurrentAccessToken(),
              GRAPH_PATH_MY_PHOTOS,
              parameters,
              HttpMethod.GET,
              photosGraphRequestCallback );

      request.executeAsync();

      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- PhotosRequest.onExecute()" );
      }
    }


  /*****************************************************
   *
   * A callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void facOnError( Exception exception );
    public void facOnCancel();
    }


  /*****************************************************
   *
   * A photos callback interface.
   *
   *****************************************************/
  public interface IPhotosCallback extends ICallback
    {
    public void facOnPhotosSuccess( List<Photo> photoList, boolean morePhotos );
    }


  /*****************************************************
   *
   * A login result callback.
   *
   *****************************************************/
  private class LoginResultCallback implements FacebookCallback<LoginResult>
    {

    /*****************************************************
     *
     * Called when login succeeds.
     *
     *****************************************************/
    @Override
    public void onSuccess( LoginResult loginResult )
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onSuccess( loginResult = " + loginResult.toString() + " )" );

      newAccessToken( loginResult.getAccessToken() );
      }


    /*****************************************************
     *
     * Called when login is cancelled.
     *
     *****************************************************/
    @Override
    public void onCancel()
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onCancel()" );

      if ( mPendingRequest != null ) mPendingRequest.onCancel();
      }


    /*****************************************************
     *
     * Called when login fails with an error.
     *
     *****************************************************/
    @Override
    public void onError( FacebookException facebookException )
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onError( facebookException = " + facebookException + ")", facebookException );

      if ( mPendingRequest != null ) mPendingRequest.onError( facebookException );
      }
    }


  /*****************************************************
   *
   * A graph request callback for photos.
   *
   *****************************************************/
  private class PhotosGraphRequestCallback implements GraphRequest.Callback
    {
    private IPhotosCallback mPhotosCallback;


    PhotosGraphRequestCallback( IPhotosCallback photosCallback )
      {
      mPhotosCallback = photosCallback;
      }


    @Override
    public void onCompleted( GraphResponse graphResponse )
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "PhotosGraphRequestCallback.onCompleted( graphResponse = " + graphResponse + " )" );


      // Check for error

      FacebookRequestError error = graphResponse.getError();

      if ( error != null )
        {
        Log.e( LOG_TAG, "Received Facebook server error: " + error.toString() );

        switch ( error.getCategory() )
          {
          case LOGIN_RECOVERABLE:

            Log.e( LOG_TAG, "Attempting to resolve LOGIN_RECOVERABLE error" );

            mPendingRequest = new PhotosRequest( mPhotosCallback );

            LoginManager.getInstance().resolveError( mActivity, graphResponse );

            return;

          case TRANSIENT:

            getPhotos( mPhotosCallback );

            return;

          case OTHER:

            // Fall through
          }

        if ( mPhotosCallback != null ) mPhotosCallback.facOnError( error.getException() );

        return;
        }


      // Check for data

      JSONObject responseJSONObject = graphResponse.getJSONObject();

      if ( responseJSONObject != null )
        {
        Log.d( LOG_TAG, "Response object: " + responseJSONObject.toString() );

        // Returned image data is as follows:
        //
        //      {
        //        "data":
        //          [
        //            {
        //            "id":"127137810981327",
        //            "link":"https:\/\/www.facebook.com\/photo.php?fbid=127137810981327&set=a.127137917647983.1073741826.100010553266947&type=3",
        //            "picture":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/s130x130\/12189788_127137810981327_132541351271856743_n.jpg?oh=28cc43a422b5a6af600cf69383ead821&oe=57D436FB",
        //            "images":
        //              [
        //                {
        //                "height":2048,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/t31.0-8\/12240189_127137810981327_132541351271856743_o.jpg",
        //                "width":1536
        //                },
        //                {
        //                "height":1280,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/t31.0-8\/q86\/p960x960\/12240189_127137810981327_132541351271856743_o.jpg",
        //                "width":960
        //                },
        //                {
        //                "height":960,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-9\/12189788_127137810981327_132541351271856743_n.jpg?oh=70a79bd7db8038ba1bddb6571f44f204&oe=57D20748",
        //                "width":720
        //                },
        //                {
        //                "height":800,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/t31.0-0\/q81\/p600x600\/12240189_127137810981327_132541351271856743_o.jpg",
        //                "width":600
        //                },
        //                {
        //                "height":640,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/q81\/p480x480\/12189788_127137810981327_132541351271856743_n.jpg?oh=df73c06e98f6fdf144ed52032b7c284c&oe=57CE9AB1",
        //                "width":480
        //                },
        //                {
        //                "height":426,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p320x320\/12189788_127137810981327_132541351271856743_n.jpg?oh=a93025d1656980ef03778b6e65f8e3ee&oe=57DAEDDE",
        //                "width":320
        //                },
        //                {
        //                "height":540,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p180x540\/12189788_127137810981327_132541351271856743_n.jpg?oh=f727d706ac924e214bdd1e113546acb2&oe=57D86FA8",
        //                "width":405
        //                },
        //                {
        //                "height":173,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p130x130\/12189788_127137810981327_132541351271856743_n.jpg?oh=7e3705aa4673ef25aba315198bd81d7c&oe=57DF914E",
        //                "width":130
        //                },
        //                {
        //                "height":225,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p75x225\/12189788_127137810981327_132541351271856743_n.jpg?oh=fe6e37ebef0d7813a5e0b5f9c16490ce&oe=57DAD07C",
        //                "width":168
        //                }
        //              ]
        //            },
        //            ... <next photo> ...

        JSONArray dataJSONArray = responseJSONObject.optJSONArray( JSON_NAME_DATA );

        if ( dataJSONArray != null )
          {
          ArrayList<Photo> photoArrayList = new ArrayList<>( dataJSONArray.length() );

          for ( int photoIndex = 0; photoIndex < dataJSONArray.length(); photoIndex ++ )
            {
            try
              {
              JSONObject photoJSONObject = dataJSONArray.getJSONObject( photoIndex );

              Photo photo = photoFromJSON( photoJSONObject );

              if ( photo != null )
                {
                photoArrayList.add( photo );
                }
              }
            catch ( JSONException je )
              {
              Log.e( LOG_TAG, "Unable to extract photo data from JSON: " + responseJSONObject.toString(), je );
              }
            }

          mNextPhotosPageGraphRequest = graphResponse.getRequestForPagedResults( GraphResponse.PagingDirection.NEXT );

          if (mPhotosCallback != null ) mPhotosCallback.facOnPhotosSuccess( photoArrayList, mNextPhotosPageGraphRequest != null );
          }
        else
          {
          Log.e( LOG_TAG, "No data found in JSON response: " + responseJSONObject );
          }
        }
      else
        {
        Log.e( LOG_TAG, "No JSON found in graph response" );
        }

      }


    /*****************************************************
     *
     * Returns a photo from the supplied JSON.
     *
     *****************************************************/
    private Photo photoFromJSON( JSONObject photoJSONObject ) throws JSONException
      {
      String    id             = photoJSONObject.getString( JSON_NAME_ID );
      String    picture        = photoJSONObject.getString( JSON_NAME_PICTURE );
      JSONArray imageJSONArray = photoJSONObject.getJSONArray( JSON_NAME_IMAGES );  // "The different stored representations of the photo. Can vary in number based upon the size of the original photo."

      if ( DEBUGGING_ENABLED )
        {
        Log.d( LOG_TAG, "-- Photo --" );
        Log.d( LOG_TAG, "Id      : " + id );
        Log.d( LOG_TAG, "Picture : " + picture );  // "Link to the 100px wide representation of this photo"
        }


      try
        {
        // Create a new photo, and add the (thumbnail) picture
        Photo photo = new Photo( picture, 100 );


        // Add the remaining images

        int imageCount = imageJSONArray.length();

        for ( int imageIndex = 0; imageIndex < imageCount; imageIndex++ )
          {
          JSONObject imageJSONObject = imageJSONArray.getJSONObject( imageIndex );

          String imageSourceURLString = imageJSONObject.getString( JSON_NAME_SOURCE );
          int    width                = imageJSONObject.optInt( JSON_NAME_WIDTH, Photo.Image.UNKNOWN_DIMENSION );
          int    height               = imageJSONObject.optInt( JSON_NAME_HEIGHT, Photo.Image.UNKNOWN_DIMENSION );

          if ( imageSourceURLString != null )
            {
            photo.addImage( imageSourceURLString, width, height );
            }
          }

        return ( photo );
        }
      catch ( MalformedURLException mue )
        {
        Log.e( LOG_TAG, "Invalid URL in JSON: " + photoJSONObject.toString(), mue );
        }

      return ( null );
      }


    /*****************************************************
     *
     * Iterates through the images in a JSON array, and returns
     * the source of the largest one.
     *
     *****************************************************/
//    private String getLargestImageSource( JSONArray imageJSONArray ) throws JSONException
//      {
//      if ( imageJSONArray == null ) return ( null );
//
//      int imageCount = imageJSONArray.length();
//
//      int    largestImageWidth  = 0;
//      String largestImageSource = null;
//
//      for ( int imageIndex = 0; imageIndex < imageCount; imageIndex ++ )
//        {
//        JSONObject imageJSONObject = imageJSONArray.getJSONObject( imageIndex );
//        int        width           = imageJSONObject.getInt( JSON_NAME_WIDTH );
//
//        if ( width > largestImageWidth )
//          {
//          largestImageWidth    = width;
//          largestImageSource = imageJSONObject.getString( JSON_NAME_SOURCE );
//          }
//        }
//
//      return ( largestImageSource );
//      }

    }

  }