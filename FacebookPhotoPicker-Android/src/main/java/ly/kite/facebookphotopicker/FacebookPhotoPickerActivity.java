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

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;


///// Class Declaration /////

/*****************************************************
 *
 * This activity is the Facebook photo picker.
 *
 *****************************************************/
public class FacebookPhotoPickerActivity extends Activity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                = "FacebookPhotoPickerActivity";

  static private final String  PERMISSION_USER_PHOTOS = "user_photos";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private AccessToken      mAccessToken;

  private CallbackManager  mCallbackManager;


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


    // See if we already have an access token

    mAccessToken = AccessToken.getCurrentAccessToken();

    if ( mAccessToken != null )
      {
      displayGallery();
      }
    else
      {
      LoginManager loginManager = LoginManager.getInstance();

      mCallbackManager = CallbackManager.Factory.create();

      loginManager.registerCallback( mCallbackManager, new LoginResultCallback() );

      loginManager.logInWithReadPermissions( this, Arrays.asList( PERMISSION_USER_PHOTOS ) );
      }

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


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Displays the gallery.
   *
   *****************************************************/
  private void displayGallery()
    {
    // TODO
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
      mAccessToken = loginResult.getAccessToken();

      displayGallery();
      }


    /*****************************************************
     *
     * Called when login is cancelled.
     *
     *****************************************************/
    @Override
    public void onCancel()
      {
      finish();
      }


    /*****************************************************
     *
     * Called when login fails with an error.
     *
     *****************************************************/
    @Override
    public void onError( FacebookException error )
      {
      // TODO: Display error dialog
      }
    }


  }

