/*****************************************************
 * FacebookPhotoPicker.java
 * <p/>
 * <p/>
 * Modified MIT License
 * <p/>
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers.
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *****************************************************/

///// Package Declaration /////

package ly.kite.facebookphotopicker;


///// Import(s) /////


///// Class Declaration /////

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import ly.kite.photopicker.common.Photo;

/*****************************************************
 *
 * This class is the gateway to the Facebook photo picker
 * functionality.
 *
 *****************************************************/
public class FacebookPhotoPicker {
    ////////// Static Constant(s) //////////

    @SuppressWarnings("unused")
    static private final String LOG_TAG = "FacebookPhotoPicker";

    static public final String EXTRA_SELECTED_PHOTOS = "ly.kite.facebookphotopicker.EXTRA_SELECTED_PHOTOS";


    ////////// Static Variable(s) //////////


    ////////// Member Variable(s) //////////


    ////////// Static Initialiser(s) //////////


    ////////// Static Method(s) //////////

    /*****************************************************
     *
     * Starts the Facebook photo picker.
     *
     *****************************************************/
    static public void startPhotoPickerForResult(Fragment fragment, int activityRequestCode) {
        FacebookPhotoPickerActivity.startForResult(fragment, activityRequestCode);
    }


    /*****************************************************
     *
     * Returns an array of picked photos.
     *
     *****************************************************/
    static public Photo[] getResultPhotos(Intent data) {
        Parcelable[] photos = data.getParcelableArrayExtra(FacebookPhotoPicker.EXTRA_SELECTED_PHOTOS);

        Photo[] facebookPhotos = new Photo[photos.length];

        System.arraycopy(photos, 0, facebookPhotos, 0, photos.length);

        return (facebookPhotos);
    }


    ////////// Constructor(s) //////////


    ////////// Method(s) //////////


    ////////// Inner Class(es) //////////

}

