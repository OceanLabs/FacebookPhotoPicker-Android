package ly.kite.photopicker.common;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.HashSet;

import ly.kite.facebookphotopicker.R;

/*****************************************************
 *
 * A multi choice mode listener for the grid view.
 *
 *****************************************************/
public class GridCheckController implements GridView.MultiChoiceModeListener, AdapterView.OnItemClickListener
  {
  private Activity        mActivity;
  private PagingGridView  mPagingGridView;

  private IListener       mListener;

  private HashSet<Photo>  mSelectedPhotoSet;


  public GridCheckController( Activity activity, PagingGridView pagingGridView )
    {
    mActivity         = activity;
    mPagingGridView   = pagingGridView;

    mSelectedPhotoSet = new HashSet<>();

    mPagingGridView.setMultiChoiceModeListener( this );
    mPagingGridView.setOnItemClickListener( this );
    }


  public GridCheckController( Activity activity, PagingGridView pagingGridView, IListener listener )
    {
    this( activity, pagingGridView );

    setListener( listener );
    }


  public void setListener( IListener listener )
    {
    mListener = listener;
    }


  public boolean onCreateActionMode( ActionMode mode, Menu menu )
    {
    MenuInflater inflater = mActivity.getMenuInflater();
    inflater.inflate( R.menu.photo_selection_menu, menu );

    int selectCount = mPagingGridView.getCheckedItemCount();
    mode.setTitle( "" + selectCount );

    return true;
    }


  public boolean onPrepareActionMode( ActionMode mode, Menu menu )
    {
    return true;
    }


  public boolean onActionItemClicked( ActionMode mode, MenuItem item )
    {
    if ( item.getItemId() == R.id.item_done )
      {
      if ( mListener != null )
        {
        Photo[] photoArray = new Photo[ mSelectedPhotoSet.size() ];

        mSelectedPhotoSet.toArray( photoArray );

        mListener.mcmlOnAction( photoArray );
        }

      }

    return true;
    }


  public void onDestroyActionMode( ActionMode mode )
    {
    }


  public void onItemCheckedStateChanged( ActionMode mode, int position, long id, boolean checked )
    {
    int selectCount = mPagingGridView.getCheckedItemCount();
    mode.setTitle( "" + selectCount );
    PhotoAdaptor adaptor = (PhotoAdaptor) mPagingGridView.getOriginalAdapter();
    adaptor.notifyDataSetChanged();

    Photo photo = (Photo) adaptor.getItem( position );

    if ( checked )
      {
      mSelectedPhotoSet.add( photo );
      }
    else
      {
      mSelectedPhotoSet.remove( photo );
      }
    }


  @Override
  public void onItemClick( AdapterView<?> adapterView, View view, int position, long id )
    {
    boolean checked = mPagingGridView.isItemChecked( position );

    mPagingGridView.setItemChecked( position, ! checked );
    }


  public interface IListener
    {
    public void mcmlOnAction( Photo[] photoArray );
    }
  }
