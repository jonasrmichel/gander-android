package edu.utexas.ece.mpc.gander.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationHelper {

	private static final String TAG = LocationHelper.class.getSimpleName();

	LocationListener mLocationListener;
	LocationManager mLocationManager;
	private Location mLocation = null;
	public long timestamp;
	private int count;

	private static LocationHelper mInstance;

	private static final int TEN_MINUTES = 1000 * 60 * 10;

	private LocationHelper(Context context) {
		mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		createLocationListener();

	}

	public static LocationHelper getInstance(Context context) {
		if (mInstance == null)
			mInstance = new LocationHelper(context);
		return mInstance;
	}

	public int getCount() {
		return count;
	}

	/**
	 * Tries to get a location from the listener if that was successful or the
	 * last known location otherwise.
	 * 
	 * @return
	 */
	public Location getLocation() {
		if (mLocation != null)
			return mLocation;
		else {
			if ((mLocationManager != null)) {
				return mLocationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
		}
		return null;
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TEN_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TEN_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than ten minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	private void createLocationListener() {

		mLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if (isBetterLocation(location, mLocation)) {
					mLocation = location;
					count++;
				}
			}

			public void onProviderDisabled(String provider) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		};

	}

	/**
	 * Starts listening to location updates
	 */
	public void registerLocationListener(long minUpdateInterval) {
		try {
			if ((mLocationManager != null) && (mLocationListener != null)) {
				mLocationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, minUpdateInterval, 10,
						mLocationListener);
				mLocationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, minUpdateInterval,
						50, mLocationListener);
			}
		} catch (Exception e) {
			Log.i(TAG, "Can't request location Updates: " + e.toString());
			return;
		}
	}

	/**
	 * Stops listening to location updates
	 */
	public void unRegisterLocationListener() {
		try {
			if ((mLocationManager != null) && (mLocationListener != null)) {

				mLocationManager.removeUpdates(mLocationListener);

			}
		} catch (IllegalArgumentException e) {
			Log.i(TAG, "Can't unregister location listener: " + e.toString());
			return;
		} finally {
			count = 0;
		}
	}

	/**
	 * Returns true if latitude is in the range [-90, +90] and longitude is in
	 * the range [-180, +180].
	 * 
	 * @param latitude
	 *            latitude in degrees.
	 * @param longitude
	 *            longitude in degrees.
	 * @return true if latitude and longitude are valid, false otherwise.
	 */
	public static boolean validCoordinates(double latitude, double longitude) {
		return latitude <= 90 && latitude >= -90 && longitude <= 180
				&& longitude >= -180;
	}
}
