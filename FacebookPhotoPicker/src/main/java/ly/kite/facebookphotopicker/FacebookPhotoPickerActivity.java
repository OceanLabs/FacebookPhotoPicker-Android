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

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;

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
public class FacebookPhotoPickerActivity extends Activity implements FacebookAgent.IPhotosCallback, PagingGridView.Pagingable, GridCheckController.IListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                = "FacebookPhotoPickerA...";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private FacebookAgent            mFacebookAgent;

  private PagingGridView           mPagingGridView;
  private PagingBaseAdaptor        mPagingBaseAdaptor;
  private GridCheckController      mMultiChoiceModeListener;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  static public void startForResult(Fragment fragment, int activityRequestCode )
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


    // Get the Facebook agent
    mFacebookAgent = FacebookAgent.getInstance( this );


    // Set up the screen

    setContentView( R.layout.screen_photo_picker );

    mPagingGridView = (PagingGridView)findViewById( R.id.paging_grid_view );


    // Set up the grid view

    mPagingBaseAdaptor = new PhotoAdaptor();
    mPagingGridView.setAdapter( mPagingBaseAdaptor );
    mPagingGridView.setChoiceMode( GridView.CHOICE_MODE_MULTIPLE_MODAL );
    mPagingGridView.setPagingableListener( FacebookPhotoPickerActivity.this );

    mMultiChoiceModeListener = new GridCheckController( this, mPagingGridView, FacebookPhotoPickerActivity.this );


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

    mFacebookAgent.onActivityResult( requestCode, resultCode, data );
    }


  /*****************************************************
   *
   * Called when an item in the options menu is selected.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    // See what menu item was selected

    int itemId = item.getItemId();

    if ( itemId == android.R.id.home )
      {
      ///// Home /////

      // We intercept the home button and do the same as if the
      // back key had been pressed.

      super.onBackPressed();

      return ( true );
      }


    return ( super.onOptionsItemSelected( item ) );
    }


  ////////// FacebookAgent.PhotosCallback Method(s) //////////

  /*****************************************************
   *
   * Called when photos were successfully retrieved.
   *
   *****************************************************/
  @Override
  public void facOnPhotosSuccess( List<Photo> photoList, boolean morePhotos )
    {
    mPagingGridView.onFinishLoading( morePhotos, photoList );
    }


  /*****************************************************
   *
   * Called when there was an error retrieving photos.
   *
   *****************************************************/
  @Override
  public void facOnError( Exception exception )
    {
    Log.e( LOG_TAG, "Facebook error", exception );

    RetryListener  retryListener  = new RetryListener();
    CancelListener cancelListener = new CancelListener();

    new AlertDialog.Builder( this )
        .setTitle( R.string.title_facebook_alert_dialog )
        .setMessage( getString( R.string.message_facebook_alert_dialog, exception.toString() ) )
        .setPositiveButton( R.string.button_text_retry, retryListener )
        .setNegativeButton( R.string.button_text_cancel, cancelListener )
        .setOnCancelListener( cancelListener )
        .create()
      .show();
    }


  /*****************************************************
   *
   * Called when photo retrieval was cancelled.
   *
   *****************************************************/
  @Override
  public void facOnCancel()
    {
    finish();
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
    mFacebookAgent.getPhotos( this );
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
    if ( mPagingBaseAdaptor != null )
      {
      mPagingBaseAdaptor.removeAllItems();
      }

    if ( mFacebookAgent != null )
      {
      mFacebookAgent.resetPhotos();

      mFacebookAgent.getPhotos( this );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The alert dialog retry button listener.
   *
   *****************************************************/
  private class RetryListener implements Dialog.OnClickListener
    {
    @Override
    public void onClick( DialogInterface dialog, int which )
      {
      displayGallery();
      }
    }


  /*****************************************************
   *
   * The alert dialog cancel (button) listener.
   *
   *****************************************************/
  private class CancelListener implements Dialog.OnClickListener, Dialog.OnCancelListener
    {
    @Override
    public void onClick( DialogInterface dialog, int which )
      {
      finish();
      }

    @Override
    public void onCancel( DialogInterface dialog )
      {
      finish();
      }
    }


  }

