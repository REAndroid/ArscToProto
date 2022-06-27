package com.reandroid.arsctoproto.converters;

import com.android.aapt.ConfigurationOuterClass;
import com.android.aapt.Resources;

import com.reandroid.lib.arsc.value.ResConfig;
import com.reandroid.lib.arsc.value.ResConfigHelper;

public class ProtoHelperConfig {


    public static com.android.aapt.ConfigValue.Builder convertConfigValue(ResConfig resConfig){
        com.android.aapt.ConfigValue.Builder builder = com.android.aapt.ConfigValue.newBuilder();
        ConfigurationOuterClass.Configuration config=convert(resConfig);
        builder.setConfig(config);
        return builder;
    }
    public static ConfigurationOuterClass.Configuration convert(ResConfig resConfig){

        ConfigurationOuterClass.Configuration.Builder builder = ConfigurationOuterClass.Configuration.newBuilder();
        builder.setDensity(resConfig.getDensity());
        builder.setKeyboardValue(resConfig.getKeyboard());
        builder.setMcc(resConfig.getMcc());
        builder.setMnc(resConfig.getMnc());
        builder.setLayoutDirectionValue(resConfig.getScreenLayout());
        builder.setSdkVersion(resConfig.getSdkVersion());
        convertUiMode(resConfig, builder);
        builder.setSmallestScreenWidthDp(resConfig.getSmallestScreenWidthDp());
        builder.setOrientationValue(resConfig.getOrientation());
        builder.setScreenWidthDp(resConfig.getScreenWidthDp());
        builder.setScreenHeightDp(resConfig.getScreenHeightDp());
        builder.setScreenWidth(resConfig.getScreenWidth());
        builder.setScreenHeight(resConfig.getScreenHeight());
        String locale=getLocale(resConfig);
        if(locale!=null){
            builder.setLocale(locale);
        }
        return builder.build();
    }

    private static void convertUiMode(ResConfig resConfig, ConfigurationOuterClass.Configuration.Builder builder) {

        byte uiMode = resConfig.getUiMode();
        switch(uiMode & 15) {
            case 2:
                builder.setUiModeType(ConfigurationOuterClass.Configuration.UiModeType.UI_MODE_TYPE_DESK);
                break;
            case 3:
                builder.setUiModeType(ConfigurationOuterClass.Configuration.UiModeType.UI_MODE_TYPE_CAR);
                break;
            case 4:
                builder.setUiModeType(ConfigurationOuterClass.Configuration.UiModeType.UI_MODE_TYPE_TELEVISION);
                break;
            case 5:
                builder.setUiModeType(ConfigurationOuterClass.Configuration.UiModeType.UI_MODE_TYPE_APPLIANCE);
                break;
            case 6:
                builder.setUiModeType(ConfigurationOuterClass.Configuration.UiModeType.UI_MODE_TYPE_WATCH);
                break;
            case 7:
                builder.setUiModeType(ConfigurationOuterClass.Configuration.UiModeType.UI_MODE_TYPE_VRHEADSET);
            case 8:
            case 9:
            case 10:
            default:
                break;
            case 11:
                //ret.append("-godzillaui");
                break;
            case 12:
                //ret.append("-smallui");
                break;
            case 13:
                //ret.append("-mediumui");
                break;
            case 14:
                //ret.append("-largeui");
                break;
            case 15:
                //ret.append("-hugeui");
        }

        switch(uiMode & 48) {
            case 16:
                builder.setUiModeNight(ConfigurationOuterClass.Configuration.UiModeNight.UI_MODE_NIGHT_NOTNIGHT);
                break;
            case 32:
                builder.setUiModeNight(ConfigurationOuterClass.Configuration.UiModeNight.UI_MODE_NIGHT_NIGHT);
        }

    }

    private static String getLocale(ResConfig resConfig){
        char[] localeVariant = resConfig.getLocaleVariant();
        if(lengthOf(localeVariant)==0){
            return null;
        }
        StringBuilder builder=new StringBuilder();
        builder.append(localeVariant);
        return builder.toString();
    }
    private static int lengthOf(char[] chs){
        if(chs==null){
            return 0;
        }
        int result=0;
        for(char ch:chs){
            if(ch!=0){
                result++;
            }
        }
        return result;
    }
}
