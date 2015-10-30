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

import java.net.URL;


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
  static private final String LOG_TAG = "Photo";


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

  private final URL     mThumbnailURL;
  private final URL     mFullURL;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public Photo( URL thumbnailURL, URL fullURL )
    {
    mThumbnailURL = thumbnailURL;
    mFullURL      = fullURL;
    }

  public Photo( Parcel in )
    {
    mThumbnailURL = (URL)in.readValue( URL.class.getClassLoader() );
    mFullURL      = (URL)in.readValue( URL.class.getClassLoader() );
    }


  ////////// Parcelable Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/
  @Override
  public int describeContents()
    {
    return 0;
    }

  @Override
  public void writeToParcel( Parcel targetParcel, int flags )
    {
    targetParcel.writeValue( mThumbnailURL );
    targetParcel.writeValue( mFullURL );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the URL of the thumbnail image.
   *
   *****************************************************/
  public URL getThumbnailURL()
    {
    return ( mThumbnailURL );
    }


  /*****************************************************
   *
   * Returns the URL of the full image.
   *
   *****************************************************/
  public URL getFullURL()
    {
    return ( mFullURL );
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
    v = v * 31 + mThumbnailURL.hashCode();
    v = v * 31 + mFullURL.hashCode();
    return v;
    }


  /*****************************************************
   *
   * Returns true if this photo equals the other photo.
   * Note that this does not take into account the ids,
   * so photos with matching URLs but different ids are
   * taken to be equal.
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

    return ( otherPhoto.mThumbnailURL.equals( mThumbnailURL ) && otherPhoto.mFullURL.equals( mFullURL ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
