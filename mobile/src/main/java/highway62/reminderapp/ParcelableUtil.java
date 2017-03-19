package highway62.reminderapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pbkou on 10/02/2017.
 */

public class ParcelableUtil {
    public static byte[] marshall(Parcelable parceable) {
        Parcel parcel = Parcel.obtain();
        parceable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // This is extremely important!
        return parcel;
    }

    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }

    public static byte[] toByteArray(Parcelable parcelable) {
        Parcel parcel=Parcel.obtain();

        parcelable.writeToParcel(parcel, 0);

        byte[] result=parcel.marshall();

        parcel.recycle();

        return(result);
    }

    public static <T> T toParcelable(byte[] bytes,
                                     Parcelable.Creator<T> creator) {
        Parcel parcel=Parcel.obtain();

        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);

        T result=creator.createFromParcel(parcel);

        parcel.recycle();

        return(result);
    }

}
