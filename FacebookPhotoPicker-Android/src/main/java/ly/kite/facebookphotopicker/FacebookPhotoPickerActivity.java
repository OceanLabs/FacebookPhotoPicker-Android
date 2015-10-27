/*****************************************************
 *
 * FacebookPhotoPickerActivity.java
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

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

import ly.kite.photopicker.common.GridCheckController;
import ly.kite.photopicker.common.PagingBaseAdaptor;
import ly.kite.photopicker.common.PagingGridView;
import ly.kite.photopicker.common.Photo;
import ly.kite.photopicker.common.PhotoAdaptor;


///// Class Declaration /////

/*****************************************************
 *
 * This activity is the Facebook photo picker.
 *
 *****************************************************/
public class FacebookPhotoPickerActivity extends Activity implements PagingGridView.Pagingable, GridCheckController.IListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                = "FacebookPhotoPickerA...";

  static private final String  PERMISSION_USER_PHOTOS = "user_photos";

  static private final String  GRAPH_PATH_MY_PHOTOS   = "/me/photos";

  static private final String  PARAMETER_NAME_TYPE    = "type";
  static private final String  PARAMETER_VALUE_TYPE   = "uploaded";

  static private final String  PARAMETER_NAME_FIELDS  = "fields";
  static private final String  PARAMETER_VALUE_FIELDS = "id,link,picture";

  static private final String  JSON_NAME_DATA         = "data";
  static private final String  JSON_NAME_ID           = "id";
  static private final String  JSON_NAME_LINK         = "link";
  static private final String  JSON_NAME_PICTURE      = "picture";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private CallbackManager          mCallbackManager;

  private boolean                  mDisplayGalleryOnNewAccessToken;

  private PagingGridView           mPagingGridView;
  private PagingBaseAdaptor        mPagingBaseAdaptor;
  private GridCheckController mMultiChoiceModeListener;

  private GraphRequest             mNextPageGraphRequest;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  static public void startForResult( Fragment fragment, int activityRequestCode )
    {
    Intent intent = new Intent( fragment.getActivity(), FacebookPhotoPickerActivity.class );

    fragment.startActivityForResult( intent, activityRequestCode );
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


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Initialise the Facebook SDK

    FacebookSdk.sdkInitialize( getApplicationContext() );

    mCallbackManager = CallbackManager.Factory.create();


    setContentView( R.layout.screen_photo_picker );

    mPagingGridView = (PagingGridView)findViewById( R.id.paging_grid_view );


    // Set up the grid view

    mPagingBaseAdaptor = new PhotoAdaptor();
    mPagingGridView.setAdapter( mPagingBaseAdaptor );
    mPagingGridView.setChoiceMode( GridView.CHOICE_MODE_MULTIPLE_MODAL );
    mPagingGridView.setPagingableListener( FacebookPhotoPickerActivity.this );

    mMultiChoiceModeListener = new GridCheckController( FacebookPhotoPickerActivity.this, mPagingGridView, FacebookPhotoPickerActivity.this );


    displayGallery();
    }


  /*****************************************************
   *
   * Called when an activity returns a result.
   *
   *****************************************************/
  @Override
  protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
    {
    super.onActivityResult( requestCode, resultCode, data );

    if ( mCallbackManager != null ) mCallbackManager.onActivityResult( requestCode, resultCode, data );
    }


  ////////// PagingGridView.Pagingable Method(s) //////////

  /*****************************************************
   *
   * Called when the done action is clicked.
   *
   *****************************************************/
  @Override
  public void onLoadMoreItems()
    {
    if ( mNextPageGraphRequest != null )
      {
      mNextPageGraphRequest.setCallback( new GraphRequestCallback() );

      mNextPageGraphRequest.executeAsync();

      mNextPageGraphRequest = null;
      }
    }


  ////////// MultiChoiceModeListener.IListener Method(s) //////////

  /*****************************************************
   *
   * Called when the done action is clicked.
   *
   *****************************************************/
  @Override
  public void mcmlOnAction( Photo[] photoArray )
    {
    // Set the result and exit

    Intent resultIntent = new Intent();

    resultIntent.putExtra( FacebookPhotoPicker.EXTRA_SELECTED_PHOTOS, photoArray );

    setResult( Activity.RESULT_OK, resultIntent );

    finish();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Displays the gallery.
   *
   *****************************************************/
  private void displayGallery()
    {
    // If we don't have an access token - make a log-in request.

    AccessToken accessToken = AccessToken.getCurrentAccessToken();

    if ( accessToken == null || accessToken.getUserId() == null )
      {
      LoginManager loginManager = LoginManager.getInstance();

      loginManager.registerCallback( mCallbackManager, new LoginResultCallback() );

      mDisplayGalleryOnNewAccessToken = true;

      loginManager.logInWithReadPermissions( this, Arrays.asList( PERMISSION_USER_PHOTOS ) );

      return;
      }

    Log.d( LOG_TAG, "Current access token = " + accessToken.getToken() );


    // If the access token has expired - refresh it

    if ( accessToken.isExpired() )
      {
      Log.i( LOG_TAG, "Access token has expired - refreshing" );

      mDisplayGalleryOnNewAccessToken = true;

      AccessToken.refreshCurrentAccessTokenAsync();

      return;
      }


    // Create and execute a graph request for the user's photos

    Bundle parameters = new Bundle();

    parameters.putString( PARAMETER_NAME_TYPE,   PARAMETER_VALUE_TYPE );
    parameters.putString( PARAMETER_NAME_FIELDS, PARAMETER_VALUE_FIELDS );

    GraphRequest request = new GraphRequest(
            AccessToken.getCurrentAccessToken(),
            GRAPH_PATH_MY_PHOTOS,
            parameters,
            HttpMethod.GET,
            new GraphRequestCallback());

    request.executeAsync();
    }


  /*****************************************************
   *
   * Processes a new access token.
   *
   *****************************************************/
  private void newAccessToken( AccessToken accessToken )
    {
    Log.d( LOG_TAG, "newAcceessToken( accessToken ):\n" + stringFrom( accessToken ) );

    if ( mDisplayGalleryOnNewAccessToken )
      {
      mDisplayGalleryOnNewAccessToken = false;

      displayGallery();
      }
    }


  ////////// Inner Class(es) //////////

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
      Log.d( LOG_TAG, "onSuccess( loginResult = " + loginResult.toString() + " )" );

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
      Log.d( LOG_TAG, "onCancel()" );

      AccessToken.setCurrentAccessToken( null );

      finish();
      }


    /*****************************************************
     *
     * Called when login fails with an error.
     *
     *****************************************************/
    @Override
    public void onError( FacebookException facebookException )
      {
      Log.d( LOG_TAG, "onError( facebookException = " + facebookException + ")", facebookException );

      // TODO: Display error dialog
      }
    }


  /*****************************************************
   *
   * A graph request callback.
   *
   *****************************************************/
  private class GraphRequestCallback implements GraphRequest.Callback
    {
    @Override
    public void onCompleted( GraphResponse graphResponse )
      {
      Log.d( LOG_TAG, "Graph response: " + graphResponse );


      // Check for error

      FacebookRequestError error = graphResponse.getError();

      if ( error != null )
        {
        Log.e( LOG_TAG, "Received Facebook server error: " + error.toString() );

        switch ( error.getCategory() )
          {
          case LOGIN_RECOVERABLE:

            Log.e( LOG_TAG, "Attempting to resolve LOGIN_RECOVERABLE error" );

            mDisplayGalleryOnNewAccessToken = true;

            LoginManager.getInstance().resolveError( FacebookPhotoPickerActivity.this, graphResponse );

            return;

          case TRANSIENT:

            displayGallery();

            return;

          case OTHER:

            return;
          }

        // TODO: Display error dialog

        return;
        }


      // Check for data

      JSONObject responseJSONObject = graphResponse.getJSONObject();

      if ( responseJSONObject != null )
        {
        Log.d( LOG_TAG, "Response object: " + responseJSONObject.toString() );

        JSONArray dataJSONArray = responseJSONObject.optJSONArray( JSON_NAME_DATA );

        if ( dataJSONArray != null )
          {
          ArrayList<Photo> photoArrayList = new ArrayList<>( dataJSONArray.length() );

          for ( int photoIndex = 0; photoIndex < dataJSONArray.length(); photoIndex ++ )
            {
            try
              {
              JSONObject photoJSONObject = dataJSONArray.getJSONObject( photoIndex );

              String id      = photoJSONObject.getString( JSON_NAME_ID );
              String link    = photoJSONObject.getString( JSON_NAME_LINK );
              String picture = photoJSONObject.getString( JSON_NAME_PICTURE );

              Log.d( LOG_TAG, "-- Photo --" );
              Log.d( LOG_TAG, "Id      : " + id );
              Log.d( LOG_TAG, "Link    : " + link );
              Log.d( LOG_TAG, "Picture : " + picture );

              Photo photo = new Photo( new URL( picture), new URL( link ) );

              photoArrayList.add( photo );
              }
            catch ( JSONException je )
              {
              Log.e( LOG_TAG, "Unable to extract photo data from JSON: " + responseJSONObject.toString(), je );
              }
            catch ( MalformedURLException mue )
              {
              Log.e( LOG_TAG, "Invalid URL in JSON: " + responseJSONObject.toString(), mue );
              }
            }

          mNextPageGraphRequest = graphResponse.getRequestForPagedResults( GraphResponse.PagingDirection.NEXT );

          mPagingGridView.onFinishLoading( mNextPageGraphRequest != null, photoArrayList );
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
    }



  }

