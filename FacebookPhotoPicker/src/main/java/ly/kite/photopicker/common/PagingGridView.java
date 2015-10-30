package ly.kite.photopicker.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class PagingGridView extends GridViewWithHeaderAndFooter
  {

  public interface Pagingable
    {
    void onLoadMoreItems();
    }

  private boolean      mIsLoading;
  private boolean      mHasMoreItems;
  private Pagingable   mPagingableListener;
  private LoadingView  mLoadingView;

  public PagingGridView( Context context )
    {
    super( context );
    init();
    }

  public PagingGridView( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    init();
    }

  public PagingGridView( Context context, AttributeSet attrs, int defStyle )
    {
    super( context, attrs, defStyle );

    init();
    }

  public boolean isLoading()
    {
    return mIsLoading;
    }

  public void setIsLoading( boolean isLoading )
    {
    mIsLoading = isLoading;
    }

  public void setPagingableListener( Pagingable pagingableListener )
    {
    mPagingableListener = pagingableListener;
    }

  public void setHasMoreItems( boolean hasMoreItems )
    {
    mHasMoreItems = hasMoreItems;

    if ( ! mHasMoreItems )
      {
      removeFooterView( mLoadingView );
      }
    }

  public boolean hasMoreItems()
    {
    return mHasMoreItems;
    }


  public void onFinishLoading( boolean hasMoreItems, List<? extends Object> newItems )
    {
    setHasMoreItems( hasMoreItems );
    setIsLoading( false );
    if ( newItems != null && newItems.size() > 0 )
      {
      ListAdapter adapter = super.getOriginalAdapter();
      if ( adapter instanceof PagingBaseAdaptor )
        {
        ( (PagingBaseAdaptor) adapter ).addMoreItems( newItems );
        }
      }
    }

  private void init()
    {
    mIsLoading = false;
    mLoadingView = new LoadingView( getContext() );
    addFooterView( mLoadingView );
    setOnScrollListener( new AbsListView.OnScrollListener()
    {
    @Override
    public void onScrollStateChanged( AbsListView view, int scrollState )
      {
      //DO NOTHING...
      }

    @Override
    public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount )
      {
      if ( totalItemCount > 0 )
        {
        int lastVisibleItem = firstVisibleItem + visibleItemCount;

        if ( !mIsLoading && mHasMoreItems && ( lastVisibleItem == totalItemCount ) )
          {
          if ( mPagingableListener != null )
            {
            mIsLoading = true;

            mPagingableListener.onLoadMoreItems();
            }

          }
        }
      }
    } );
    }


  }
