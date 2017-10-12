package com.emreay.music.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;
import com.emreay.music.glide.artistimage.ArtistImage;
import com.emreay.music.glide.artistimage.ArtistImageLoader;
import com.emreay.music.glide.audiocover.AudioFileCover;
import com.emreay.music.glide.audiocover.AudioFileCoverLoader;

import java.io.InputStream;
public class PhonographGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(AudioFileCover.class, InputStream.class, new AudioFileCoverLoader.Factory());
        glide.register(ArtistImage.class, InputStream.class, new ArtistImageLoader.Factory(context));
    }
}
