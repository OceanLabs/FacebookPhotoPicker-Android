# Facebook Photo Picker[![](https://jitpack.io/v/droidizer/FacebookPhotoPicker-Android.svg)](https://jitpack.io/#droidizer/FacebookPhotoPicker-Android)

## How to build

    ./gradlew clean build

## How to install

To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}

Step 2. Add the dependency

	dependencies {
	        compile 'com.github.droidizer:FacebookPhotoPicker-Android:1.0.+'
	}

Step 3. Add Facebook Api Id [R.string.facebook_app_id](https://github.com/droidizer/FacebookPhotoPicker-Android/blob/master/FacebookPhotoPicker/src/main/res/values/facebook_resources.xml)

Step 4. Add Internet permission to your AndroidManifest.xml

    <uses-permission android:name="android.permission.INTERNET"/>

Step 4. Start Intent

        public static final int FACEBOOKPHOTOPICKERACTIVITY_REQUEST_CODE = 10012;

        FacebookPhotoPickerActivity.startForResult(myFragment, FACEBOOKPHOTOPICKERACTIVITY_REQUEST_CODE);

Step 5. Listen to result in your Fragment by overriding Fragment.onActivityResult

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == FACEBOOKPHOTOPICKERACTIVITY_REQUEST_CODE) {
            Photo[] photoArray = FacebookPhotoPicker.getResultPhotos(data);
        }
    }
