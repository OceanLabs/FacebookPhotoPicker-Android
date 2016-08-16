package ly.kite.photopicker.common;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import ly.kite.facebookphotopicker.R;

public class PhotoAdaptor extends PagingBaseAdaptor<Photo>
  {
  static public final String LOG_TAG = "PhotoAdaptor";


  public PhotoAdaptor( List<Photo> itemList )
    {
    super( itemList );
    }

  public PhotoAdaptor()
    {
    super();
    }

  @Override
  public int getCount()
    {
    return mItemList.size();
    }

  @Override
  public Photo getItem( int position )
    {
    return mItemList.get( position );
    }

  @Override
  public long getItemId( int position )
    {
    return position;
    }

  @Override
  public View getView( int position, View convertView, ViewGroup parent )
    {
    Context context = parent.getContext();


    View       view = null;
    Object     tag;
    ViewHolder viewHolder;

    if ( convertView             != null &&
         ( tag = convertView.getTag() ) != null &&
         tag instanceof ViewHolder )
      {
      view       = convertView;
      viewHolder = (ViewHolder)tag;
      }
    else
      {
      view       = LayoutInflater.from( context ).inflate( R.layout.grid_item_photo, null );
      viewHolder = new ViewHolder( view );

      view.setTag( viewHolder );
      }


    // Decide which image to load as the thumbnail

    Photo photo = getItem( position );

    int width  = viewHolder.imageView.getWidth();
    int height = viewHolder.imageView.getHeight();

    if ( width < 1 || height < 1 )
      {
      Resources resources = context.getResources();

      width  = resources.getDimensionPixelSize( R.dimen.default_thumbnail_image_width );
      height = Photo.Image.UNKNOWN_DIMENSION;
      }

    URL thumbnailImageURL = photo.getBestImageURL( width, height );


    if ( thumbnailImageURL != null )
      {
      // Load the image
      Picasso.with( parent.getContext() ).load( thumbnailImageURL.toString() ).into( viewHolder.imageView );
      }
    else
      {
      Log.e( LOG_TAG, "Unable to find thumbnail image URL for size " + width + " x " + height );
      }


    GridView gridView = (GridView)parent;

    viewHolder.checkbox.setImageResource( gridView.isItemChecked( position ) ? R.drawable.checkbox_on : R.drawable.checkbox_off );


    return ( view );
    }


  private static final class ViewHolder
    {
    ImageView imageView;
    ImageView checkbox;

    ViewHolder( View view )
      {
      this.imageView = (ImageView)view.findViewById( R.id.imageview );
      this.checkbox  = (ImageView)view.findViewById( R.id.checkbox );
      }

    }


  }
