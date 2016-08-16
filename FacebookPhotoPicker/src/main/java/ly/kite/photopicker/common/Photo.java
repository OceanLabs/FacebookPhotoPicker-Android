/*****************************************************
 *
 * Photo.java
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

package ly.kite.photopicker.common;


///// Import(s) /////

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a photo.
 *
 *****************************************************/
public class Photo implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  static private final String  LOG_TAG           = "Photo";

  static private final boolean DEBUGGING_ENABLED = true;


  ////////// Static Variable(s) //////////

  static public final Creator CREATOR = new Creator()
    {
    public Photo createFromParcel( Parcel in )
      {
      return new Photo( in );
      }

    public Photo[] newArray( int size )
      {
      return new Photo[ size ];
      }
    };


  ////////// Member Variable(s) //////////

  private List<Image>  mImageList;
  private Image        mThumbnailImage;
  private Image        mLargestImage;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public Photo()
    {
    mImageList = new ArrayList<>();
    }

  public Photo( String thumbnailURLString, int width ) throws MalformedURLException
    {
    this();

    mThumbnailImage = addImage( thumbnailURLString, width );
    }

  public Photo( Parcel sourceParcel )
    {
    mImageList      = sourceParcel.readArrayList( Image.class.getClassLoader() );
    mThumbnailImage = (Image)sourceParcel.readValue( Image.class.getClassLoader() );
    mLargestImage   = (Image)sourceParcel.readValue( Image.class.getClassLoader() );
    }


  ////////// Parcelable Method(s) //////////

  @Override
  public int describeContents()
    {
    return 0;
    }

  @Override
  public void writeToParcel( Parcel targetParcel, int flags )
    {
    targetParcel.writeList( mImageList );
    targetParcel.writeValue( mThumbnailImage );
    targetParcel.writeValue( mLargestImage );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Adds an image.
   *
   *****************************************************/
  public Image addImage( String imageURLString, int width, int height ) throws MalformedURLException
    {
    URL   imageURL = new URL( imageURLString );
    Image image    = new Image( imageURL, width, height );

    mImageList.add( image );


    // If this is the first, or largest image so far, save it

    int largestImageWidth;
    int largestImageHeight;

    if ( mLargestImage == null ||
         ( width  > 0 && ( largestImageWidth  = mLargestImage.getWidth()  ) > 0 && width  > largestImageWidth  ) ||
         ( height > 0 && ( largestImageHeight = mLargestImage.getHeight() ) > 0 && height > largestImageHeight ) )
      {
      mLargestImage = image;
      }


    return ( image );
    }


  /*****************************************************
   *
   * Adds an image.
   *
   *****************************************************/
  public Image addImage( String imageSourceURLString, int width ) throws MalformedURLException
    {
    return ( addImage( imageSourceURLString, width, Image.UNKNOWN_DIMENSION ) );
    }


  /*****************************************************
   *
   * Returns the URL of the thumbnail image.
   *
   *****************************************************/
  public URL getThumbnailURL()
    {
    return ( mThumbnailImage.getSourceURL() );
    }


  /*****************************************************
   *
   * Returns the URL of the full image.
   *
   *****************************************************/
  public URL getFullURL()
    {
    return ( mLargestImage.getSourceURL() );
    }


  /*****************************************************
   *
   * Returns the URL of the image is best suited to the
   * supplied required dimensions. This will be the smallest
   * image that is larger than the dimensions.
   *
   *****************************************************/
  public URL getBestImageURL( int minWidth, int minHeight )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "getBestImage( minWidth = " + minWidth + ", minHeight = " + minHeight + " )" );

    Image bestSoFarImage = null;

    for ( Image candidateImage : mImageList )
      {
      if ( bestSoFarImage == null )
        {
        bestSoFarImage = candidateImage;
        }
      else
        {
        int bestSoFarImageWidth  = bestSoFarImage.getWidth();
        int bestSoFarImageHeight = bestSoFarImage.getHeight();

        int candidateImageWidth  = candidateImage.getWidth();
        int candidateImageHeight = candidateImage.getHeight();

        if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "  Candidate image: " + candidateImage );

        boolean widthDimensionIsBetter  = dimensionIsBetter( minWidth,  bestSoFarImageWidth,  candidateImageWidth );
        boolean heightDimensionIsBetter = dimensionIsBetter( minHeight, bestSoFarImageHeight, candidateImageHeight );

        if ( minWidth < 1 && minHeight < 1 )
          {
          if ( widthDimensionIsBetter && heightDimensionIsBetter ) bestSoFarImage = candidateImage;
          }
        else
          {
          if ( minWidth < 1 )
            {
            if ( heightDimensionIsBetter ) bestSoFarImage = candidateImage;
            }
          else if ( minHeight < 1 )
            {
            if ( widthDimensionIsBetter ) bestSoFarImage = candidateImage;
            }
          else
            {
            if ( widthDimensionIsBetter && heightDimensionIsBetter ) bestSoFarImage = candidateImage;
            }
          }
        }
      }


    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "  Picked image: " + bestSoFarImage );

    return ( bestSoFarImage.getSourceURL() );
    }


  /*****************************************************
   *
   * Returns true, if the dimensions are better for a
   * candidate image.
   *
   *****************************************************/
  private boolean dimensionIsBetter( int minValue, int bestSoFarValue, int candidateValue )
    {
    if ( minValue < 1 ) return ( candidateValue < bestSoFarValue );

    if ( bestSoFarValue < minValue ) return ( candidateValue > minValue );

    return ( candidateValue >= minValue && candidateValue < bestSoFarValue );
    }


  /*****************************************************
   *
   * Returns a hash code for this photo.
   *
   *****************************************************/
  @Override
  public int hashCode()
    {
    int v = 17;
    v = v * 31 + mThumbnailImage.hashCode();
    v = v * 31 + mLargestImage.hashCode();
    return v;
    }


  /*****************************************************
   *
   * Returns true if this photo equals the other photo.
   *
   * As a shortcut, we just match the thumbnail image
   * and largest image.
   *
   *****************************************************/
  @Override
  public boolean equals( Object otherObject )
    {
    if ( otherObject == null ) return ( false );

    if ( otherObject == this ) return ( true );

    if ( ! ( otherObject instanceof Photo ) )
      {
      return ( false );
      }

    Photo otherPhoto = (Photo)otherObject;

    return ( otherPhoto.mThumbnailImage.equals( mThumbnailImage ) && otherPhoto.mLargestImage.equals( mLargestImage ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An image of this photo.
   *
   *****************************************************/
  static public class Image implements Parcelable
    {
    static public final int UNKNOWN_DIMENSION = -1;


    static public final Creator CREATOR = new Creator()
      {
      public Image createFromParcel( Parcel in )
        {
        return new Image( in );
        }

      public Image[] newArray( int size )
        {
        return new Image[ size ];
        }
      };


    private URL  mSourceURL;
    private int  mWidth;
    private int  mHeight;


    public Image( URL sourceURL, int width, int height )
      {
      mSourceURL = sourceURL;
      mWidth     = width;
      mHeight    = height;
      }

    public Image( URL sourceURL, int width )
      {
      this( sourceURL, width, UNKNOWN_DIMENSION );
      }

    public Image( Parcel sourceParcel )
      {
      mSourceURL = (URL)sourceParcel.readValue( URL.class.getClassLoader() );
      mWidth     = sourceParcel.readInt();
      mHeight    = sourceParcel.readInt();
      }


    @Override
    public int describeContents()
      {
      return 0;
      }

    @Override
    public void writeToParcel( Parcel targetParcel, int flags )
      {
      targetParcel.writeValue( mSourceURL );
      targetParcel.writeInt( mWidth );
      targetParcel.writeInt( mHeight );
      }


    URL getSourceURL()
      {
      return ( mSourceURL );
      }


    int getWidth()
      {
      return ( mWidth );
      }


    int getHeight()
      {
      return ( mHeight );
      }


    @Override
    public boolean equals( Object otherObject )
      {
      if ( otherObject == null ) return ( false );

      if ( otherObject == this ) return ( true );

      if ( ! ( otherObject instanceof Image ) )
        {
        return ( false );
        }

      Image otherImage = (Image)otherObject;

      return ( otherImage.mSourceURL.equals( mSourceURL ) && otherImage.mWidth == mWidth && otherImage.mHeight == mHeight );
      }

    @Override
    public String toString()
      {
      return ( mSourceURL.toString() + " : " + mWidth + " x " + mHeight );
      }
    }

  }
