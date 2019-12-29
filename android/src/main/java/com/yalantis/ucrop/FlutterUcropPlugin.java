package com.yalantis.ucrop;

import androidx.appcompat.app.AppCompatDelegate;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterUcropPlugin
 */
public class FlutterUcropPlugin implements MethodCallHandler {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String CHANNEL = "plugins.com.baiheche/image_crop";

    private final Registrar registrar;
    private final ImageCropperDelegate delegate;


    public FlutterUcropPlugin(Registrar registrar, ImageCropperDelegate delegate) {
        this.registrar = registrar;
        this.delegate = delegate;
    }


    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        if (registrar.activity() == null) {
            return;
        }

        final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);
        final ImageCropperDelegate delegate = new ImageCropperDelegate(registrar.activity());
        registrar.addActivityResultListener(delegate);

        channel.setMethodCallHandler(new FlutterUcropPlugin(registrar, delegate));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (registrar.activity() == null) {
            result.error("no_activity", "image_cropper plugin requires a foreground activity.", null);
            return;
        }
        if (call.method.equals("cropImage")) {
            delegate.startCrop(call, result);
        } else if (call.method.equals("cropFile")) {
            delegate.cropFile(call, result);
        }
    }
}
