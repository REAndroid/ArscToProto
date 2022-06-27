package com.reandroid.arsctoproto.converters;

import com.android.aapt.Item;
import com.reandroid.lib.arsc.value.ValueType;

public class ProtoItemUtil {

    public static Item.Builder toItemBuilder(ValueType valueType, int rawValue){
        if(valueType==ValueType.REFERENCE||valueType==ValueType.ATTRIBUTE){
            return loadReference(rawValue);
        }
        Item.Builder builder=loadPrimitive(valueType, rawValue);
        if(builder!=null){
            return builder;
        }
        return null;
    }
    private static Item.Builder loadPrimitive(ValueType valueType, int rawValue){
        com.android.aapt.Item.Builder itemBuilder= com.android.aapt.Item.newBuilder();
        com.android.aapt.Primitive.Builder builder= com.android.aapt.Primitive.newBuilder();
        if(valueType==ValueType.INT_BOOLEAN){
            boolean val= rawValue != 0;
            builder.setBooleanValue(val);
        }else if(valueType==ValueType.INT_COLOR_ARGB4){
            builder.setColorArgb4Value(rawValue);
        }else if(valueType==ValueType.INT_COLOR_ARGB8){
            builder.setColorArgb8Value(rawValue);
        }else if(valueType==ValueType.INT_COLOR_RGB4){
            builder.setColorRgb4Value(rawValue);
        }else if(valueType==ValueType.INT_COLOR_RGB8){
            builder.setColorRgb8Value(rawValue);
        }else if(valueType==ValueType.FIRST_COLOR_INT){
            builder.setColorArgb8Value(rawValue);
        }else if(valueType==ValueType.FLOAT){
            float val = Float.intBitsToFloat(rawValue);
            builder.setFloatValue(val);
        }else if(valueType==ValueType.FRACTION){
            builder.setFractionValue(rawValue);
        }else if(valueType==ValueType.DIMENSION){
            builder.setDimensionValue(rawValue);
        }else if(valueType==ValueType.INT_DEC){
            builder.setIntDecimalValue(rawValue);
        }else if(valueType==ValueType.INT_HEX){
            builder.setIntHexadecimalValue(rawValue);
        }else if(valueType==ValueType.FIRST_INT){
            builder.setIntDecimalValue(rawValue);
        }else{
            return null;
        }
        itemBuilder.setPrim(builder);
        return itemBuilder;
    }
    private static Item.Builder loadReference(int rawValue){
        com.android.aapt.Item.Builder itemBuilder= com.android.aapt.Item.newBuilder();
        com.android.aapt.Reference.Builder refBuilder= com.android.aapt.Reference.newBuilder();
        refBuilder.setId(rawValue);
        itemBuilder.setRef(refBuilder);
        return itemBuilder;
    }
}
