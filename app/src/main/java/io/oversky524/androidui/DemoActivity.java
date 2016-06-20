package io.oversky524.androidui;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import io.base.model.Size;
import io.base.utils.BitmapUtils;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        ImageView imageView = (ImageView)findViewById(R.id.image);
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.mipmap.ic_launcher);
        Size size = BitmapUtils.getBitmapSize(uri, this);
        imageView.setImageBitmap(BitmapUtils.circleBitmap(BitmapUtils.getBitmap(uri, size.width, size.height, this), null));
        /*Glide.with(BaseApplication.getGlobalApp()).load(R.mipmap.ic_launcher).asBitmap()
                .transform(new ImageLoadUtils.CircleTransformation(BaseApplication.getGlobalApp())).into(imageView);*/
    }
}
